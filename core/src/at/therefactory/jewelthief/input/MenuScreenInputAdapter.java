package at.therefactory.jewelthief.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.FitViewport;

import at.therefactory.jewelthief.JewelThief;
import at.therefactory.jewelthief.constants.Config;
import at.therefactory.jewelthief.constants.I18NKeys;
import at.therefactory.jewelthief.constants.PrefsKeys;
import at.therefactory.jewelthief.misc.Util;
import at.therefactory.jewelthief.net.HTTP;
import at.therefactory.jewelthief.screens.MenuScreen;

/**
 * Created by Christian on 18.09.2016.
 */
public class MenuScreenInputAdapter extends InputAdapter {

    private final FitViewport viewport;
    private final MenuScreen menuScreen;
    private final KeyboardInputListener listener;
    private int numTouches;
    private boolean touchDragging;
    private long timestampLastClickOnResetHighscoreSettingButton = 0; // only reset highscore when tapped twice within x seconds
    private I18NBundle bundle;

    // required for highscore list scrolling
    private float lastDeltaY;
    private float touchStartY;
    private float deltaY;

    public MenuScreenInputAdapter(MenuScreen menuScreen, FitViewport viewport) {
        this.viewport = viewport;
        this.menuScreen = menuScreen;
        this.bundle = JewelThief.getInstance().getBundle();
        listener = new KeyboardInputListener();
        timestampLastClickOnResetHighscoreSettingButton = 0;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACK) {
            if (menuScreen.getState() == MenuScreen.MenuState.ShowSettings
                    || menuScreen.getState() == MenuScreen.MenuState.ShowHighscores
                    || menuScreen.getState() == MenuScreen.MenuState.ShowAbout) {
                menuScreen.setState(MenuScreen.MenuState.ShowMenu);
            } else {
                Gdx.app.exit();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        numTouches++;
        Vector3 touchCoordinates = viewport.unproject(new Vector3(screenX, screenY, 0));
        pressOrReleaseButtons(touchCoordinates);
        handleTouchOnStars(touchCoordinates);
        touchStartY = touchCoordinates.y;
        if (Config.DEBUG_MODE && Util.within(touchCoordinates, menuScreen.getTitle())) {
            menuScreen.setState(MenuScreen.MenuState.ShowPromo);
        } else if (menuScreen.getState() == MenuScreen.MenuState.ShowPromo) {
            menuScreen.setState(MenuScreen.MenuState.ShowMenu);
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 touchCoordinates = viewport.unproject(new Vector3(screenX, screenY, 0));
        pressOrReleaseButtons(touchCoordinates);
        handleTouchOnStars(touchCoordinates);
        if (numTouches == 1 && !menuScreen.updateHighscoresButton.isPressed() && !menuScreen.returnToMainMenuButton.isPressed()) {
            deltaY = lastDeltaY + (touchStartY - touchCoordinates.y);
            deltaY = Math.max(deltaY, menuScreen.getHighscores() == null ? 0 : -Config.HIGHSCORES_LINE_HEIGHT * (menuScreen.getHighscores().length - 1)); // stop scrolling if only last line is visible
            deltaY = -deltaY; // invert vertical scrolling direction
            menuScreen.setScrollbarPosition((22f - 200f) / (Config.HIGHSCORES_LINE_HEIGHT * (menuScreen.getHighscores().length - 1)) * deltaY + 200);
        }
        touchDragging = true;
        return true;
    }

    private void handleTouchOnStars(Vector3 touchCoordinates) {
        if (!touchDragging) { // only allow single taps on the stars to prevent unintentional disappearing of stars
            menuScreen.handleTouchOnStars(touchCoordinates);
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Preferences prefs = JewelThief.getInstance().getPreferences();
        numTouches = Math.max(0, numTouches - 1);

        if (menuScreen.returnToMainMenuButton.isPressed()) {
            menuScreen.returnToMainMenuButton.release();
            menuScreen.setShowLicenseYOffset(0);
            menuScreen.setState(MenuScreen.MenuState.ShowMenu);
        } else if (menuScreen.getState() == MenuScreen.MenuState.ShowAbout) {
            if (menuScreen.licenseButton.isPressed()) {
                menuScreen.licenseButton.release();
                if (menuScreen.getShowLicenseYOffset() == 0) {
                    menuScreen.setShowLicenseYOffset(54);
                } else {
                    menuScreen.setShowLicenseYOffset(0);
                }
            }
        } else if (menuScreen.getState() == MenuScreen.MenuState.ShowHighscores) {
            if (menuScreen.updateHighscoresButton.isPressed()) {
                menuScreen.updateHighscoresButton.release();
                menuScreen.setFetchingHighscores(true);
                HTTP.fetchHighscores(menuScreen, prefs.getString(PrefsKeys.ID), prefs.getString(PrefsKeys.PLAYER_NAME),
                        prefs.getInteger(PrefsKeys.BEST_SCORE_NUM_JEWELS), prefs.getInteger(PrefsKeys.BEST_SCORE_NUM_SECONDS));
            }
        } else if (menuScreen.getState() == MenuScreen.MenuState.ShowSettings) {
            if (menuScreen.playernameSettingButton.isPressed()) {
                menuScreen.playernameSettingButton.release();
                Gdx.input.getTextInput(listener, bundle.get(I18NKeys.PLEASE_ENTER_YOUR_NAME),
                        prefs.getString(PrefsKeys.PLAYER_NAME), "");
            } else if (menuScreen.soundSettingButton.isPressed()) {
                menuScreen.soundSettingButton.release();
                menuScreen.soundSettingButton.nextState();
                prefs.putBoolean(PrefsKeys.ENABLE_SOUND, !prefs.getBoolean(PrefsKeys.ENABLE_SOUND));
                prefs.flush();
            } else if (menuScreen.musicSettingButton.isPressed()) {
                menuScreen.musicSettingButton.release();
                menuScreen.musicSettingButton.nextState();
                prefs.putBoolean(PrefsKeys.ENABLE_MUSIC, !prefs.getBoolean(PrefsKeys.ENABLE_MUSIC));
                prefs.flush();
                if (prefs.getBoolean(PrefsKeys.ENABLE_MUSIC)) {
                    JewelThief.getInstance().playMusicFile(true);
                } else {
                    JewelThief.getInstance().pauseMusic();
                }
            } else if (menuScreen.languageSettingButton.isPressed()) {
                menuScreen.languageSettingButton.release();
                menuScreen.languageSettingButton.nextState();
                bundle = JewelThief.getInstance().setLocale(menuScreen.languageSettingButton.getState() == 0 ? "en" : "de");
                menuScreen.setBundle(bundle);
            } else if (menuScreen.resetHighscoreSettingButton.isPressed()) {
                menuScreen.resetHighscoreSettingButton.release();
                if (timestampLastClickOnResetHighscoreSettingButton > System.currentTimeMillis() - 1000) {
                    timestampLastClickOnResetHighscoreSettingButton = 0;
                    prefs.remove(PrefsKeys.MY_RANK);
                    prefs.remove(PrefsKeys.BEST_SCORE);
                    prefs.remove(PrefsKeys.BEST_SCORE_NUM_JEWELS);
                    prefs.remove(PrefsKeys.BEST_SCORE_NUM_SECONDS);
                    JewelThief.getInstance().toast(bundle.get(I18NKeys.HIGHSCORE_IS_RESET), true);
                } else {
                    timestampLastClickOnResetHighscoreSettingButton = System.currentTimeMillis();
                    JewelThief.getInstance().toast(bundle.get(I18NKeys.TAP_AGAIN_TO_RESET_HIGHSCORE), false);
                }
            }
        }
        // main menu
        else {
            if (menuScreen.singlePlayerButton.isPressed()) {
                JewelThief.getInstance().showIntroScreen();
                menuScreen.singlePlayerButton.release();
            } else if (menuScreen.highscoresButton.isPressed()) {
                deltaY = 0;
                if (prefs.contains(PrefsKeys.CACHED_HIGHSCORES)) {
                    menuScreen.setHighscores(prefs.getString(PrefsKeys.CACHED_HIGHSCORES).split("\n"));
                    //Gdx.app.log(getClass().getName(), prefs.getString(PrefsKeys.CACHED_HIGHSCORES));
                }
                menuScreen.setState(MenuScreen.MenuState.ShowHighscores);
                menuScreen.highscoresButton.release();
            } else if (menuScreen.settingsButton.isPressed()) {
                menuScreen.setState(MenuScreen.MenuState.ShowSettings);
                menuScreen.settingsButton.release();
            } else if (menuScreen.aboutButton.isPressed()) {
                menuScreen.setState(MenuScreen.MenuState.ShowAbout);
                menuScreen.aboutButton.release();
            }
        }
        lastDeltaY = -deltaY;
        touchDragging = false;
        return true;
    }

    private void pressOrReleaseButtons(Vector3 screenCoord) {
        if (numTouches == 1) {
            menuScreen.pressOrReleaseButtons(screenCoord);
        } else {
            menuScreen.releaseAllButtons();
        }
    }

    public void update(float delta) {
        // scroll highscore list back to top if dragged down too far
        if (!touchDragging && deltaY < 0) {
            deltaY = Math.min(0, deltaY + 5);
            lastDeltaY = deltaY;
        }
    }

    public float getDeltaY() {
        return deltaY;
    }

}
