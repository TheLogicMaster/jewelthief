/*
 * Copyright (C) 2016  Christian DeTamble
 *
 * This file is part of Jewel Thief.
 *
 * Jewel Thief is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jewel Thief is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Jewel Thief.  If not, see <http://www.gnu.org/licenses/>.
 */

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
import at.therefactory.jewelthief.constants.PrefsKeys;
import at.therefactory.jewelthief.net.HttpServer;
import at.therefactory.jewelthief.screens.MenuScreen;
import at.therefactory.jewelthief.ui.buttons.GrayButton;

import static at.therefactory.jewelthief.constants.Config.HIGHSCORES_LINE_HEIGHT;
import static at.therefactory.jewelthief.constants.Config.URL_TO_PLAY_STORE;
import static at.therefactory.jewelthief.constants.Config.URL_TO_SOUNDTRACK;
import static at.therefactory.jewelthief.constants.I18NKeys.HIGHSCORE_IS_RESET;
import static at.therefactory.jewelthief.constants.I18NKeys.PLEASE_ENTER_YOUR_NAME;
import static at.therefactory.jewelthief.constants.I18NKeys.TAP_AGAIN_TO_RESET_HIGHSCORE;

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
    private GrayButton pressedButton;
    private Preferences prefs = JewelThief.getInstance().getPreferences();

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
            if (menuScreen.getState().equals(MenuScreen.MenuState.ShowSettings)
                    || menuScreen.getState().equals(MenuScreen.MenuState.ShowHighscores)
                    || menuScreen.getState().equals(MenuScreen.MenuState.ShowAbout)) {
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
        super.touchDown(screenX, screenY, pointer, button);
        numTouches++;
        Vector3 touchCoordinates = viewport.unproject(new Vector3(screenX, screenY, 0));
        pressOrReleaseButtons(touchCoordinates);

        if (pressedButton == null) {
            handleTouchOnStars(touchCoordinates);
        }

        touchStartY = touchCoordinates.y;
//        if (DEBUG_MODE && Utils.within(touchCoordinates, menuScreen.getSpriteTitle())) {
//            menuScreen.setState(MenuScreen.MenuState.ShowPromo);
//        } else if (menuScreen.getState().equals(MenuScreen.MenuState.ShowPromo)) {
//            menuScreen.setState(MenuScreen.MenuState.ShowMenu);
//        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        super.touchDragged(screenX, screenY, pointer);
        Vector3 touchCoordinates = viewport.unproject(new Vector3(screenX, screenY, 0));
        if (numTouches == 1 && !menuScreen.buttonUpdateHighscores.isPressed() && !menuScreen.buttonExitToMainMenu.isPressed()) {
            deltaY = lastDeltaY + (touchStartY - touchCoordinates.y);
            deltaY = Math.max(deltaY, menuScreen.getHighscores() == null ? 0 : -HIGHSCORES_LINE_HEIGHT * (menuScreen.getHighscores().length - 1)); // stop scrolling if only last line is visible
            deltaY = -deltaY; // invert vertical scrolling direction
            menuScreen.setScrollbarPositionY((22f - 200f) / (HIGHSCORES_LINE_HEIGHT * (menuScreen.getHighscores().length - 1)) * deltaY + 200);
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
        super.touchUp(screenX, screenY, pointer, button);
        numTouches = Math.max(0, numTouches - 1);

        Vector3 touchCoordinates = viewport.unproject(new Vector3(screenX, screenY, 0));
        if (pressedButton != null && pressedButton.contains(touchCoordinates)) {
            if (menuScreen.buttonExitToMainMenu.isPressed()) {
                menuScreen.buttonExitToMainMenu.release();
                menuScreen.setShowLicenseYOffset(0);
                menuScreen.setState(MenuScreen.MenuState.ShowMenu);
            } else if (menuScreen.getState().equals(MenuScreen.MenuState.ShowAbout)) {
                if (menuScreen.buttonShowLicense.isPressed()) {
                    menuScreen.buttonShowLicense.release();
                    if (menuScreen.getShowLicenseYOffset() == 0) {
                        menuScreen.setShowLicenseYOffset(54);
                    } else {
                        menuScreen.setShowLicenseYOffset(0);
                    }
                } else if (menuScreen.buttonSoundtrack.isPressed()) {
                    menuScreen.buttonSoundtrack.release();
                    Gdx.net.openURI(URL_TO_SOUNDTRACK);
                } else if (menuScreen.buttonRate.isPressed()) {
                    menuScreen.buttonRate.release();
                    Gdx.net.openURI(URL_TO_PLAY_STORE);
                }
            } else if (menuScreen.getState().equals(MenuScreen.MenuState.ShowHighscores)) {
                if (menuScreen.buttonUpdateHighscores.isPressed()) {
                    menuScreen.buttonUpdateHighscores.release();
                    menuScreen.setFetchingHighscores(true);
                    HttpServer.fetchHighscores(menuScreen, prefs.getString(PrefsKeys.ID), prefs.getString(PrefsKeys.PLAYER_NAME),
                            prefs.getInteger(PrefsKeys.BEST_SCORE_NUM_JEWELS), prefs.getInteger(PrefsKeys.BEST_SCORE_NUM_SECONDS));
                }
            } else if (menuScreen.getState().equals(MenuScreen.MenuState.ShowSettings)) {
                if (menuScreen.buttonChangePlayername.isPressed()) {
                    menuScreen.buttonChangePlayername.release();
                    Gdx.input.getTextInput(listener, bundle.get(PLEASE_ENTER_YOUR_NAME),
                            prefs.getString(PrefsKeys.PLAYER_NAME), "");
                } else if (menuScreen.buttonToggleSound.isPressed()) {
                    menuScreen.buttonToggleSound.release();
                    menuScreen.buttonToggleSound.nextState();
                    prefs.putBoolean(PrefsKeys.ENABLE_SOUND, !prefs.getBoolean(PrefsKeys.ENABLE_SOUND));
                    prefs.flush();
                } else if (menuScreen.buttonToggleMusic.isPressed()) {
                    menuScreen.buttonToggleMusic.release();
                    menuScreen.buttonToggleMusic.nextState();
                    prefs.putBoolean(PrefsKeys.ENABLE_MUSIC, !prefs.getBoolean(PrefsKeys.ENABLE_MUSIC));
                    prefs.flush();
                    if (prefs.getBoolean(PrefsKeys.ENABLE_MUSIC)) {
                        JewelThief.getInstance().playMusicFile(true);
                    } else {
                        JewelThief.getInstance().pauseMusic();
                    }
                } else if (menuScreen.buttonChangeLanguage.isPressed()) {
                    menuScreen.buttonChangeLanguage.release();
                    menuScreen.buttonChangeLanguage.nextState();
                    bundle = JewelThief.getInstance().setLocale(menuScreen.buttonChangeLanguage.getState() == 0 ? "en" : "de");
                    menuScreen.setBundle(bundle);
                } else if (menuScreen.buttonResetHighscore.isPressed()) {
                    menuScreen.buttonResetHighscore.release();
                    if (timestampLastClickOnResetHighscoreSettingButton > System.currentTimeMillis() - 1000) {
                        timestampLastClickOnResetHighscoreSettingButton = 0;
                        prefs.remove(PrefsKeys.MY_RANK);
                        prefs.remove(PrefsKeys.BEST_SCORE);
                        prefs.remove(PrefsKeys.BEST_SCORE_NUM_JEWELS);
                        prefs.remove(PrefsKeys.BEST_SCORE_NUM_SECONDS);
                        JewelThief.getInstance().toast(bundle.get(HIGHSCORE_IS_RESET), true);
                    } else {
                        timestampLastClickOnResetHighscoreSettingButton = System.currentTimeMillis();
                        JewelThief.getInstance().toast(bundle.get(TAP_AGAIN_TO_RESET_HIGHSCORE), false);
                    }
                }
            }
            // main menu
            else {
                if (menuScreen.buttonStartSinglePlayerGame.isPressed()) {
                    JewelThief.getInstance().showIntroScreen();
                    menuScreen.buttonStartSinglePlayerGame.release();
                } else if (menuScreen.buttonShowHighscores.isPressed()) {
                    deltaY = 0;
                    menuScreen.setScrollbarPositionY(Config.INITIAL_SCROLLBAR_POSITION_Y);
                    if (prefs.contains(PrefsKeys.CACHED_HIGHSCORES)) {
                        menuScreen.setHighscores(prefs.getString(PrefsKeys.CACHED_HIGHSCORES).split("\n"));
                    }
                    menuScreen.setState(MenuScreen.MenuState.ShowHighscores);
                    menuScreen.buttonShowHighscores.release();
                } else if (menuScreen.buttonShowSettings.isPressed()) {
                    menuScreen.setState(MenuScreen.MenuState.ShowSettings);
                    menuScreen.buttonShowSettings.release();
                } else if (menuScreen.buttonShowAbout.isPressed()) {
                    menuScreen.setState(MenuScreen.MenuState.ShowAbout);
                    menuScreen.buttonShowAbout.release();
                }
            }
        }
        pressOrReleaseButtons(touchCoordinates);
        menuScreen.positionImagesOnButtons();

        pressedButton = null;
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
        for (GrayButton grayButton : menuScreen.buttons) {
            if (grayButton != null && grayButton.isPressed()) {
                pressedButton = grayButton;
                break;
            }
        }
    }

    public void update(float delta) {
        if (!touchDragging && deltaY < 0) { // scroll highscore list back to top if dragged down too far
            deltaY = Math.min(0, deltaY + Math.abs(deltaY) / 5);
            lastDeltaY = deltaY;
        }
    }

    public float getDeltaY() {
        return deltaY;
    }

}
