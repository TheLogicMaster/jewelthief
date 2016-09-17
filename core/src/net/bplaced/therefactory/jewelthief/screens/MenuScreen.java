package net.bplaced.therefactory.jewelthief.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.FitViewport;

import net.bplaced.therefactory.jewelthief.JewelThief;
import net.bplaced.therefactory.jewelthief.constants.Config;
import net.bplaced.therefactory.jewelthief.constants.I18NKeys;
import net.bplaced.therefactory.jewelthief.constants.PrefsKeys;
import net.bplaced.therefactory.jewelthief.input.KeyboardInput;
import net.bplaced.therefactory.jewelthief.misc.Util;
import net.bplaced.therefactory.jewelthief.net.HTTP;
import net.bplaced.therefactory.jewelthief.ui.buttons.GrayButton;
import net.bplaced.therefactory.jewelthief.ui.buttons.GrayStateButton;

public class MenuScreen extends ScreenAdapter implements InputProcessor {

    private boolean showSettings = false;
    private boolean showAbout = false;
    private boolean showHighscores = false;

    private final SpriteBatch batch;
    private final ShapeRenderer sr;

    private final Sprite title;
    private final Sprite player;
    private final Sprite redplayer;
    private final Sprite blueplayer;
    private final Sprite pearl;
    private final Sprite soldier;
    private final Sprite settings;
    private final Sprite skyline;
    private final Sprite therefactory;
    private final Sprite badge;
    private final Sprite[] stars;

    private final GrayStateButton soundSettingButton;
    private final GrayStateButton musicSettingButton;
    private final GrayStateButton languageSettingButton;
    private final GrayButton resetHighscoreSettingButton;
    private final GrayButton playernameSettingButton;
    private final GrayButton singlePlayerButton;
    private final GrayButton highscoresButton;
    private final GrayButton settingsButton;
    private final GrayButton aboutButton;
    private final GrayButton updateHighscoresButton;
    private final GrayButton returnToMainMenuButton;
    private final GrayButton licenseButton;

    private final OrthographicCamera camera;
    private final FitViewport viewport;

    private final BitmapFont font;
    private final float[] starSpeeds;
    private final float borderSize;
    private final KeyboardInput listener;
    private final Preferences prefs;

    private String[] highscores;
    private int numTouches;
    private boolean fetchingHighscores;
    private I18NBundle bundle;
    private int showLicenseYOffset = 0;
    private float touchStartY; // required for highscore list scrolling
    private float deltaY; // required for highscore list scrolling
    private float lastDeltaY; // required for highscore list scrolling
    private boolean touchDragging;
    private long timestampLastClickOnResetHighscoreSettingButton = 0; // only reset highscore when tapped twice within x seconds
    private boolean showPromo;

    public MenuScreen(SpriteBatch batch, ShapeRenderer sr) {

        // initialize member variables
        prefs = JewelThief.getInstance().getPreferences();
        this.batch = batch;
        this.sr = sr;
        this.bundle = JewelThief.getInstance().getBundle();
        font = JewelThief.getInstance().getFont();
        listener = new KeyboardInput();

        camera = new OrthographicCamera();
        viewport = new FitViewport(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT, camera);
        camera.position.set(Config.WINDOW_WIDTH / 2, Config.WINDOW_HEIGHT / 2, 0);
        camera.update();

        // sprites
        title = new Sprite(new Texture("title.png"));
        skyline = new Sprite(new Texture("skyline.png"));
        therefactory = new Sprite(new Texture("therefactory.png"));

        borderSize = (Config.WINDOW_WIDTH - skyline.getWidth()) / 2;
        skyline.setPosition(borderSize, borderSize);

        player = JewelThief.getInstance().getTextureAtlas().createSprite("WhitePlayer");
        redplayer = JewelThief.getInstance().getTextureAtlas().createSprite("RedPlayer");
        blueplayer = JewelThief.getInstance().getTextureAtlas().createSprite("BluePlayer");
        pearl = JewelThief.getInstance().getTextureAtlas().createSprite("Pearl");
        soldier = JewelThief.getInstance().getTextureAtlas().createSprite("Soldier");
        settings = JewelThief.getInstance().getTextureAtlas().createSprite("settings");
        badge = new Sprite(new Texture("google-play-badge.png"));

        player.setFlip(true, false);
        redplayer.setFlip(true, false);
        blueplayer.setFlip(true, false);

        // stars
        stars = new Sprite[Config.MENU_SCREEN_NUM_STARS];
        starSpeeds = new float[Config.MENU_SCREEN_NUM_STARS];
        for (int i = 0; i < Config.MENU_SCREEN_NUM_STARS; i++) {
            Sprite star = JewelThief.getInstance().getTextureAtlas().createSprite("star");
            star.setPosition(
                    Util.randomWithin(borderSize + star.getWidth(), Config.WINDOW_WIDTH - borderSize - star.getWidth()),
                    Util.randomWithin(borderSize, Config.WINDOW_HEIGHT - star.getHeight()));
            stars[i] = star;
            starSpeeds[i] = Util.randomWithin(0.01f, 0.2f);
        }

        // buttons in settings submenu
        soundSettingButton = new GrayStateButton(new String[]{
                bundle.get(I18NKeys.SOUND) + " " + bundle.get(I18NKeys.IS) + " " + bundle.get(I18NKeys.OFF),
                bundle.get(I18NKeys.SOUND) + " " + bundle.get(I18NKeys.IS) + " " + bundle.get(I18NKeys.ON)},
                new String[]{"checkbox_unchecked", "checkbox_checked"}, prefs.getBoolean(PrefsKeys.ENABLE_SOUND) ? 1 : 0,
                        false, 16, 66, 130, 40);
        musicSettingButton = new GrayStateButton(new String[]{
                bundle.get(I18NKeys.MUSIC) + " " + bundle.get(I18NKeys.IS) + " " + bundle.get(I18NKeys.OFF),
                bundle.get(I18NKeys.MUSIC) + " " + bundle.get(I18NKeys.IS) + " " + bundle.get(I18NKeys.ON)},
                new String[]{"checkbox_unchecked", "checkbox_checked"}, prefs.getBoolean(PrefsKeys.ENABLE_MUSIC) ? 1 : 0,
                        false, soundSettingButton.getX(), 16, soundSettingButton.getWidth(), soundSettingButton.getHeight());
        playernameSettingButton = new GrayButton(bundle.get(I18NKeys.PLAYERNAME), 155, soundSettingButton.getY(), 100,
                soundSettingButton.getHeight(), true);
        languageSettingButton = new GrayStateButton(new String[]{"English", "Deutsch"}, new String[]{"flag_usa",
        "flag_germany"}, prefs.getString("language").equals("en") ? 0 : 1, true,
                playernameSettingButton.getX(), 16, 100, 40);
        resetHighscoreSettingButton = new GrayButton(bundle.get(I18NKeys.RESET_HIGHSCORE), 264, 16, 100, 40, true);

        // buttons in main menu
        int spaceBetweenButtons = 13; // in pixels
        int buttonWidth = 110, buttonHeight = 90;
        int borderDist = (Config.WINDOW_WIDTH - 4 * buttonWidth - 3 * spaceBetweenButtons) / 2;
        singlePlayerButton = new GrayButton(bundle.get(I18NKeys.SINGLEPLAYER), borderDist, borderDist, buttonWidth,
                buttonHeight);
        highscoresButton = new GrayButton(bundle.get(I18NKeys.HIGHSCORES), singlePlayerButton.getX()
                + singlePlayerButton.getWidth() + spaceBetweenButtons, singlePlayerButton.getY(), buttonWidth,
                buttonHeight);
        settingsButton = new GrayButton(bundle.get(I18NKeys.SETTINGS), highscoresButton.getX()
                + highscoresButton.getWidth() + spaceBetweenButtons, singlePlayerButton.getY(), buttonWidth,
                buttonHeight);
        aboutButton = new GrayButton(bundle.get(I18NKeys.ABOUT), settingsButton.getX() + settingsButton.getWidth()
                + spaceBetweenButtons, singlePlayerButton.getY(), buttonWidth, buttonHeight);
        singlePlayerButton.setCaptionOffsetY(25);
        highscoresButton.setCaptionOffsetY(singlePlayerButton.getCaptionOffsetY());
        settingsButton.setCaptionOffsetY(singlePlayerButton.getCaptionOffsetY());
        aboutButton.setCaptionOffsetY(singlePlayerButton.getCaptionOffsetY());

        // button for returning back to main menu when in submenu
        returnToMainMenuButton = new GrayButton("X", Config.WINDOW_WIDTH - buttonWidth / 2 - borderDist,
                Config.WINDOW_HEIGHT - buttonHeight / 2 - borderDist, buttonWidth / 2, buttonHeight / 2);

        // button for fetching highscores
        updateHighscoresButton = new GrayButton(bundle.get(I18NKeys.UPDATE), borderDist, Config.WINDOW_HEIGHT
                - buttonHeight / 2 - borderDist, buttonWidth / 2 + 5, buttonHeight / 2, true);

        // button for showing license
        licenseButton = new GrayButton(bundle.get(I18NKeys.LICENSE), borderDist, Config.WINDOW_HEIGHT - buttonHeight
                / 2 - borderDist, buttonWidth / 2 + 5, buttonHeight / 2, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchBackKey(true);
        timestampLastClickOnResetHighscoreSettingButton = 0;

        // play background music
        if (prefs.getBoolean(PrefsKeys.ENABLE_MUSIC)) {
            JewelThief.getInstance().playMusicFile(false);
        }
    }

    @Override
    public void render(float delta) {

        // clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // simulate world
        update(delta);

        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeType.Filled);
        sr.setColor(0, 0, 0.7f, 1); // dark blue background color
        sr.rect(0, skyline.getY(), Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT - skyline.getY());
        sr.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Sprite star : stars) {
            star.draw(batch);
        }

        skyline.draw(batch);
        skyline.setPosition(borderSize, 115);
        title.setPosition(Config.WINDOW_WIDTH / 2 - title.getWidth() / 2, Config.WINDOW_HEIGHT / 2 + 70
                + showLicenseYOffset);

        if (!showHighscores)
            title.draw(batch);
        batch.end();

        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeType.Filled);
        sr.setColor(Color.BLACK);
        sr.rect(0, 0, Config.WINDOW_WIDTH, showHighscores ? Config.WINDOW_HEIGHT - 70 : skyline.getY()
                + showLicenseYOffset);

        if (showPromo) {
            sr.end();
            batch.begin();
            batch.draw(badge, 0, 0, (int)(646/2.1), (int)(250/2.1));
            font.setColor(Color.WHITE);
            font.draw(batch, "christian.detamble@outlook.com", 300, 45);
            batch.draw(therefactory, 310, 70, 180, 14);
            batch.end();
        } else {
            // buttons
            if (showAbout || showHighscores || showSettings) {
                returnToMainMenuButton.renderShape(sr);
                if (showHighscores) {
                    updateHighscoresButton.renderShape(sr);
                } else if (showSettings) {
                    soundSettingButton.renderShape(sr);
                    musicSettingButton.renderShape(sr);
                    playernameSettingButton.renderShape(sr);
                    languageSettingButton.renderShape(sr);
                    resetHighscoreSettingButton.renderShape(sr);
                } else if (showAbout) {
                    licenseButton.renderShape(sr);
                }
            } else {
                singlePlayerButton.renderShape(sr);
                highscoresButton.renderShape(sr);
                settingsButton.renderShape(sr);
                aboutButton.renderShape(sr);
            }
            sr.end();

            batch.begin();
            if (showAbout || showHighscores || showSettings) {
                returnToMainMenuButton.renderCaption(batch);
                skyline.setY(Config.WINDOW_HEIGHT - 173 + showLicenseYOffset);
            }
            if (showHighscores) {
                updateHighscoresButton.setCaption(bundle.get(I18NKeys.UPDATE));
                updateHighscoresButton.renderCaption(batch);
                skyline.setY(Config.WINDOW_HEIGHT - 75);
                if (fetchingHighscores) {
                    font.setColor(Color.WHITE);
                    font.draw(batch, bundle.get(I18NKeys.FETCHING) + "...", 15, 205);
                } else {
                    if (highscores != null) {
                        for (int i = 0; i < highscores.length; i++) {
                            font.setColor(i == getMyRank() ? Color.GREEN : Color.WHITE);
                            float posY = (205 - i * Config.HIGHSCORES_LINE_HEIGHT + deltaY);
                            if (posY < skyline.getY()) { // lines disappear when over skyline sprite
                                font.draw(batch, highscores[i], 15, posY);
                            }
                        }
                        
                        // scrollbar
                        font.draw(batch, "^", Config.WINDOW_WIDTH - 20, 205);
                        float scrollbarPos = (22f-200f)/(Config.HIGHSCORES_LINE_HEIGHT * (highscores.length - 1)) * deltaY + 200;
                        font.draw(batch, "#", Config.WINDOW_WIDTH - 20, Math.min(200, scrollbarPos));
                        font.getData().setScale(1, -1);
                        font.draw(batch, "^", Config.WINDOW_WIDTH - 20, 10);
                        font.getData().setScale(1, 1);
                    }
                }
            } else if (showSettings) {

                // playername
                playernameSettingButton.setCaption(prefs.getString(PrefsKeys.PLAYER_NAME).trim().length() == 0 ? "<"
                        + bundle.get(I18NKeys.PLAYERNAME) + ">" : bundle.get(I18NKeys.PLAYERNAME) + ": " + prefs.getString(PrefsKeys.PLAYER_NAME));
                playernameSettingButton.renderCaption(batch);
                font.setColor(Color.WHITE);

                // sound
                soundSettingButton.setCaption(bundle.get(I18NKeys.SOUND) + " " + bundle.get(I18NKeys.IS) + " "
                        + (prefs.getBoolean(PrefsKeys.ENABLE_SOUND) ? bundle.get(I18NKeys.ON) : bundle.get(I18NKeys.OFF)));
                soundSettingButton.renderCaption(batch);

                // music
                musicSettingButton.setCaption(bundle.get(I18NKeys.MUSIC) + " " + bundle.get(I18NKeys.IS) + " "
                        + (prefs.getBoolean(PrefsKeys.ENABLE_MUSIC) ? bundle.get(I18NKeys.ON) : bundle.get(I18NKeys.OFF)));
                musicSettingButton.renderCaption(batch);

                // language
                languageSettingButton.renderCaption(batch);

                // reset highscore
                resetHighscoreSettingButton.setCaption(bundle.get(I18NKeys.RESET_HIGHSCORE));
                resetHighscoreSettingButton.renderCaption(batch);
            } else if (showAbout) {
                font.setColor(Color.WHITE);
                font.draw(batch, bundle.format(I18NKeys.ABOUT_TEXT, Config.PLUS_ONE_MAN_INTERVAL, Config.VERSION_NAME), 15,
                        100 + showLicenseYOffset);
                if (showLicenseYOffset > 0) {
                    font.draw(batch, bundle.get(I18NKeys.LICENSE_TEXT), 15, showLicenseYOffset + 2);
                }
                batch.draw(therefactory, 144, showLicenseYOffset + 19, 135, 11);
                licenseButton.setCaption(bundle.get(I18NKeys.LICENSE));
                licenseButton.renderCaption(batch);
            } else {
                // buttons' icons
                player.draw(batch);
                redplayer.draw(batch);
                blueplayer.draw(batch);
                pearl.draw(batch);
                soldier.draw(batch);
                settings.draw(batch);

                // buttons themselves
                singlePlayerButton.setCaption(bundle.get(I18NKeys.SINGLEPLAYER));
                highscoresButton.setCaption(bundle.get(I18NKeys.HIGHSCORES));
                settingsButton.setCaption(bundle.get(I18NKeys.SETTINGS));
                aboutButton.setCaption(bundle.get(I18NKeys.ABOUT));
                singlePlayerButton.renderCaption(batch);
                highscoresButton.renderCaption(batch);
                settingsButton.renderCaption(batch);
                aboutButton.renderCaption(batch);
            }

            batch.end();
        }
    }

    private void update(float delta) {

        // highscore list scrolling (scroll list back to top if dragged down too far)
        if (!touchDragging && deltaY < 0) {
            deltaY = Math.min(0, deltaY + 5);
            lastDeltaY = deltaY;
        }

        // stars
        for (int i = 0; i < stars.length; i++) {
            Sprite star = stars[i];
            if (star.getY() < borderSize) {
                star.setY(Config.WINDOW_HEIGHT - star.getHeight());
            } else {
                star.setY(star.getY() - starSpeeds[i]);
            }
        }

        // images on buttons
        player.setPosition(singlePlayerButton.getX() + singlePlayerButton.getWidth() / 2 - player.getWidth() / 2
                + singlePlayerButton.getPressedOffset(), singlePlayerButton.getY() + singlePlayerButton.getHeight() / 2
                - 5 - singlePlayerButton.getPressedOffset());
        redplayer.setPosition(highscoresButton.getX() + highscoresButton.getWidth() / 2 - redplayer.getWidth() / 2
                + highscoresButton.getPressedOffset(), highscoresButton.getY() + highscoresButton.getHeight() / 2 - 3
                - highscoresButton.getPressedOffset());
        blueplayer.setPosition(redplayer.getX() - 5, redplayer.getY() - 5);
        settings.setPosition(settingsButton.getX() + settingsButton.getWidth() / 2 - settings.getWidth() / 2
                + settingsButton.getPressedOffset(), 52 - settingsButton.getPressedOffset());
        soldier.setPosition(
                aboutButton.getX() + aboutButton.getWidth() / 2 - soldier.getWidth() / 2
                + aboutButton.getPressedOffset() - 1, 63 - aboutButton.getPressedOffset());
        pearl.setPosition(
                aboutButton.getX() + aboutButton.getWidth() / 2 - soldier.getWidth() / 2
                + aboutButton.getPressedOffset(), 50 - aboutButton.getPressedOffset());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public void setHighscores(String[] string) {
        highscores = string;
    }

    @Override
    public void dispose() {
        super.dispose();
        sr.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.BACK) {
            if (showSettings || showHighscores || showAbout) {
                showSettings = false;
                showHighscores = false;
                showAbout = false;
            } else {
                Gdx.app.exit();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        numTouches++;
        Vector3 screenCoord = viewport.unproject(new Vector3(screenX, screenY, 0));
        pressOrReleaseButtons(screenCoord);
        handleTouchOnStars(screenCoord);
        touchStartY = screenCoord.y;

        if (Config.DEBUG_MODE && Util.within(screenCoord, title)) {
            showPromo = true;
        } else if (showPromo) {
            showPromo = false;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 screenCoord = viewport.unproject(new Vector3(screenX, screenY, 0));
        pressOrReleaseButtons(screenCoord);
        handleTouchOnStars(screenCoord);
        if (numTouches == 1 && !updateHighscoresButton.isPressed() && !returnToMainMenuButton.isPressed()) {
            deltaY = lastDeltaY + (touchStartY - screenCoord.y);
            deltaY = Math.min(deltaY, highscores == null ? 0 : Config.HIGHSCORES_LINE_HEIGHT * (highscores.length - 1)); // stop scrolling if only last line is visible
        }
        touchDragging = true;
        return true;
    }

    private void handleTouchOnStars(Vector3 unprojected) {
        if (touchDragging || unprojected.y < skyline.getY()) {
            return; // only allow single taps on the stars to prevent unintentional disappearing of stars 
        }
        for (Sprite star : stars) {
            if (Util.within(unprojected.x, star.getX(), star.getX() + star.getWidth())
                    && Util.within(unprojected.y, star.getY(), star.getY() + star.getHeight())) {
                JewelThief.getInstance().playCymbalSound();
                star.setPosition(star.getX(), Config.WINDOW_HEIGHT + star.getHeight() * 3);
            }
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Preferences prefs = JewelThief.getInstance().getPreferences();
        numTouches = Math.max(0, numTouches - 1);
        
        if (returnToMainMenuButton.isPressed()) {
            returnToMainMenuButton.release();
            showLicenseYOffset = 0;
            showSettings = false;
            showHighscores = false;
            showAbout = false;
        } else if (showAbout) {
            if (licenseButton.isPressed()) {
                licenseButton.release();
                if (showLicenseYOffset == 0) {
                    showLicenseYOffset = 54;
                } else {
                    showLicenseYOffset = 0;
                }
            }
        } else if (showHighscores) {
            if (updateHighscoresButton.isPressed()) {
                updateHighscoresButton.release();
                fetchingHighscores = true;
                HTTP.fetchHighscores(this, prefs.getString(PrefsKeys.ID), prefs.getString(PrefsKeys.PLAYER_NAME),
                        prefs.getInteger(PrefsKeys.BEST_SCORE_NUM_JEWELS), prefs.getInteger(PrefsKeys.BEST_SCORE_NUM_SECONDS));
            }
        } else if (showSettings) {
            if (playernameSettingButton.isPressed()) {
                playernameSettingButton.release();
                Gdx.input.getTextInput(listener, bundle.get(I18NKeys.PLEASE_ENTER_YOUR_NAME),
                        prefs.getString(PrefsKeys.PLAYER_NAME), "");
            } else if (soundSettingButton.isPressed()) {
                soundSettingButton.release();
                soundSettingButton.nextState();
                prefs.putBoolean(PrefsKeys.ENABLE_SOUND, !prefs.getBoolean(PrefsKeys.ENABLE_SOUND));
                prefs.flush();
            } else if (musicSettingButton.isPressed()) {
                musicSettingButton.release();
                musicSettingButton.nextState();
                prefs.putBoolean(PrefsKeys.ENABLE_MUSIC, !prefs.getBoolean(PrefsKeys.ENABLE_MUSIC));
                prefs.flush();
                if (prefs.getBoolean(PrefsKeys.ENABLE_MUSIC)) {
                    JewelThief.getInstance().playMusicFile(true);
                } else {
                    JewelThief.getInstance().pauseMusic();
                }
            } else if (languageSettingButton.isPressed()) {
                languageSettingButton.release();
                languageSettingButton.nextState();
                bundle = JewelThief.getInstance().setLocale(languageSettingButton.getState() == 0 ? "en" : "de");
            } else if (resetHighscoreSettingButton.isPressed()) {
                resetHighscoreSettingButton.release();
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
            if (singlePlayerButton.isPressed()) {
                JewelThief.getInstance().showIntroScreen();
                singlePlayerButton.release();
            } else if (highscoresButton.isPressed()) {
                deltaY = 0;
                if (prefs.contains(PrefsKeys.CACHED_HIGHSCORES)) {
                    highscores = prefs.getString(PrefsKeys.CACHED_HIGHSCORES).split("\n");
                    //Gdx.app.log(getClass().getName(), prefs.getString(PrefsKeys.CACHED_HIGHSCORES));
                }
                showHighscores = true;
                highscoresButton.release();
            } else if (settingsButton.isPressed()) {
                showSettings = true;
                settingsButton.release();
            } else if (aboutButton.isPressed()) {
                showAbout = true;
                aboutButton.release();
            }
        }
        lastDeltaY = deltaY;
        touchDragging = false;
        return true;
    }

    private void pressOrReleaseButtons(Vector3 screenCoord) {
        if (numTouches == 1) {
            if (showAbout) {
                Util.pressOrReleaseButtons(screenCoord, returnToMainMenuButton, licenseButton);
            } else if (showHighscores) {
                Util.pressOrReleaseButtons(screenCoord, returnToMainMenuButton, updateHighscoresButton);
            } else if (showSettings) {
                Util.pressOrReleaseButtons(screenCoord, returnToMainMenuButton, soundSettingButton,
                        musicSettingButton, playernameSettingButton,  languageSettingButton, resetHighscoreSettingButton);
            } else {
                Util.pressOrReleaseButtons(screenCoord, singlePlayerButton, highscoresButton, settingsButton, aboutButton);
            }
        } else {
            releaseAllButtons();
        }
    }

    private void releaseAllButtons() {
        singlePlayerButton.release();
        highscoresButton.release();
        settingsButton.release();
        aboutButton.release();
        returnToMainMenuButton.release();
        updateHighscoresButton.release();
        soundSettingButton.release();
        musicSettingButton.release();
        playernameSettingButton.release();
        resetHighscoreSettingButton.release();
        licenseButton.release();
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        // TODO Auto-generated method stub
        return false;
    }

    public void setFetchingHighscores(boolean fetchingHighscores) {
        this.fetchingHighscores = fetchingHighscores;
    }

    public void setMyRank(int myRank) {
        prefs.putInteger(PrefsKeys.MY_RANK, myRank);
        prefs.flush();
    }

    public int getMyRank() {
        return prefs.contains(PrefsKeys.MY_RANK) ? prefs.getInteger(PrefsKeys.MY_RANK) : -1;
    }
}
