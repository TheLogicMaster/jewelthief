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

package at.therefactory.jewelthief;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.FitViewport;

import at.therefactory.jewelthief.constants.PrefsKeys;
import at.therefactory.jewelthief.misc.AndroidInterface;
import at.therefactory.jewelthief.misc.Utils;
import at.therefactory.jewelthief.net.HttpServer;
import at.therefactory.jewelthief.screens.GameIntroScreen;
import at.therefactory.jewelthief.screens.GameScreen;
import at.therefactory.jewelthief.screens.LogoScreen;
import at.therefactory.jewelthief.screens.MenuScreen;
import at.therefactory.jewelthief.ui.Particles;

import static at.therefactory.jewelthief.constants.Config.DEFAULT_LOCALE;
import static at.therefactory.jewelthief.constants.Config.VERSION_NAME;
import static at.therefactory.jewelthief.constants.Config.WINDOW_HEIGHT;
import static at.therefactory.jewelthief.constants.Config.WINDOW_WIDTH;

public class JewelThief extends Game {

    // general
    private static JewelThief instance;
    private AndroidInterface androidInterface;

    // rendering
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private Particles particles;

    // assets
    private TextureAtlas textureAtlas;
    private AssetManager assetManager;
    private Preferences preferences;
    private BitmapFont font;
    private Sound soundClick;
    private Sound soundCymbal;
    private Music music;
    private Sprite fade;
    private I18NBundle bundle;
    private String[] musicFiles; // list of music files located in "assets/audio/music"
    private short currentMusicFile = -1; // pointer to the music file that is currently being played

    // screens
    private MenuScreen menuScreen;
    private GameIntroScreen gameIntroScreen;
    private GameScreen gameScreen;
    private LogoScreen theRefactoryLogoScreen;

    public JewelThief() {
    }

    public JewelThief(AndroidInterface androidInterface) {
        this.androidInterface = androidInterface;
    }

    @Override
    public void create() {
        instance = this;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/amiga4ever pro2.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 8;
        parameter.mono = true;
        font = generator.generateFont(parameter);
        generator.dispose();

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WINDOW_WIDTH, WINDOW_HEIGHT, camera);
        camera.position.set(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2, 0);
        camera.update();

        textureAtlas = new TextureAtlas("textures.pack");
        assetManager = new AssetManager();

        fade = textureAtlas.createSprite("fade");
        fade.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        fade.setPosition(0, 0);

        particles = new Particles(textureAtlas);

        loadInitialPreferences();
        loadAssets();
        tryToSubmitLatestHighscores();

        // load and show logo screen
        theRefactoryLogoScreen = new LogoScreen(batch, shapeRenderer, viewport, camera);
        setScreen(theRefactoryLogoScreen);
    }

    private void loadInitialPreferences() {
        preferences = Gdx.app.getPreferences(PrefsKeys.PREFERENCES_FILE_ID);
        if (!preferences.contains(PrefsKeys.ID)) {
            preferences.putString(PrefsKeys.ID, System.nanoTime() + "0" + Utils.randomWithin(1000, 9999)).flush();
        }
        if (!preferences.contains(PrefsKeys.LANGUAGE)) {
            preferences.putString(PrefsKeys.LANGUAGE, DEFAULT_LOCALE).flush();
        }
        for (String setting : new String[]{PrefsKeys.ENABLE_SOUND, PrefsKeys.ENABLE_MUSIC}) {
            if (!preferences.contains(setting)) {
                preferences.putBoolean(setting, true).flush();
            }
        }
    }

    private void loadAssets() {
        assetManager.load("audio/sounds/mouse_click.ogg", Sound.class);
        assetManager.load("audio/sounds/finger_cymbal_hit.ogg", Sound.class);
        assetManager.load("i18n/" + preferences.getString("language"), I18NBundle.class);
        assetManager.finishLoading();
        soundClick = assetManager.get("audio/sounds/mouse_click.ogg");
        soundCymbal = assetManager.get("audio/sounds/finger_cymbal_hit.ogg");
        bundle = assetManager.get("i18n/" + preferences.getString("language"), I18NBundle.class);
    }

    private void tryToSubmitLatestHighscores() {
        if (preferences.contains(PrefsKeys.BEST_SCORE_NUM_JEWELS) && preferences.contains(PrefsKeys.BEST_SCORE_NUM_SECONDS)) {
            HttpServer.submitHighscores(preferences.getString(PrefsKeys.ID),
                    preferences.getString(PrefsKeys.PLAYER_NAME), preferences.getInteger(PrefsKeys.BEST_SCORE_NUM_JEWELS),
                    preferences.getInteger(PrefsKeys.BEST_SCORE_NUM_SECONDS));
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (soundClick != null)
            soundClick.dispose();
        if (soundCymbal != null)
            soundCymbal.dispose();
        if (music != null)
            music.dispose();
        if (musicFiles != null) {
            for (String musicFile : musicFiles) {
                if (assetManager.isLoaded(musicFile)) {
                    assetManager.unload(musicFile);
                }
            }
        }
        if (particles != null)
            particles.dispose();
        if (textureAtlas != null)
            textureAtlas.dispose();
        if (batch != null)
            batch.dispose();
        if (gameScreen != null)
            gameScreen.dispose();
        if (theRefactoryLogoScreen != null)
            theRefactoryLogoScreen.dispose();
        if (gameIntroScreen != null)
            gameIntroScreen.dispose();
        if (menuScreen != null)
            menuScreen.dispose();
        if (assetManager != null)
            assetManager.dispose();
        if (preferences != null)
            preferences.flush();
    }

    public void switchToMainMenu() {
        if (menuScreen == null) {
            menuScreen = new MenuScreen(batch, shapeRenderer, viewport, camera);
        }
        setScreen(menuScreen);
    }

    public void showIntroScreen() {
        if (gameIntroScreen == null) {
            gameIntroScreen = new GameIntroScreen(batch, viewport, camera);
        }
        setScreen(gameIntroScreen);
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void startSinglePlayerGame() {
        if (gameScreen == null) {
            gameScreen = new GameScreen(batch, shapeRenderer, viewport, camera);
        }
        gameScreen.resetGame();
        setScreen(gameScreen);
    }

    public BitmapFont getFont() {
        return font;
    }

    public static JewelThief getInstance() {
        return instance;
    }

    public TextureAtlas getTextureAtlas() {
        return textureAtlas;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public I18NBundle getBundle() {
        return bundle;
    }

    public void playButtonClickSound() {
        if (preferences.getBoolean(PrefsKeys.ENABLE_SOUND)) {
            soundClick.play();
        }
    }

    public Sprite getFadeSprite() {
        return fade;
    }

    /**
     * Either continues the music playback of a previously paused music file or proceeds to the next music file depending on the proceedToNext flag.
     * If there was no previous music file, a new music file is chosen randomly.
     *
     * @param proceedToNext If set to true, a new music file is chosen randomly. If set to false, the previously paused file is being resumed.
     */
    public void playMusicFile(boolean proceedToNext) {

        // remember all available music files
        if (musicFiles == null) {
            FileHandle dirHandle = Gdx.files.internal("audio/music");
            FileHandle[] fileList = dirHandle.list();
            musicFiles = new String[fileList.length];
            for (int i = 0; i < musicFiles.length; i++) {
                musicFiles[i] = fileList[i].path();
                Gdx.app.log(getClass().getName(), "Found '" + fileList[i].path() + "'");
            }
        }

        if (musicFiles.length == 0) {
            Gdx.app.error(getClass().getName(), "Could not find any music files!");
        } else {
            // select a music file to play
            if (currentMusicFile == -1) {
                // if there is no previous music file, choose a new one randomly
                currentMusicFile = (short) Utils.randomWithin(0, musicFiles.length - 1);
                music = loadMusicAsset(musicFiles[currentMusicFile]);
            } else if (proceedToNext) {
                // switch to the next music file randomly
                int previousMusicFile = currentMusicFile;
                do {
                    currentMusicFile = (short) Utils.randomWithin(0, musicFiles.length - 1);
                } while (previousMusicFile == currentMusicFile);
                assetManager.unload(musicFiles[previousMusicFile]); // free the resources of the previous music file
                if (music != null) {
                    music.dispose();
                    music = null;
                }
                music = loadMusicAsset(musicFiles[currentMusicFile]);
            } else {
                // resume previously paused music file
            }

            // play the selected music file
            music.play();
            music.setOnCompletionListener(new Music.OnCompletionListener() {
                @Override
                public void onCompletion(Music music) {
                    playMusicFile(true);
                }
            });
        }
    }

    private Music loadMusicAsset(String path) {
        assetManager.load(path, Music.class);
        assetManager.finishLoading();
        Gdx.app.log(getClass().getName(), "Loaded '" + path + "'");
        return assetManager.get(path);
    }

    public void pauseMusic() {
        if (music != null) {
            music.pause();
        }
    }

    public void toast(String message, boolean longDuration) {
        if (androidInterface != null) {
            androidInterface.toast(message, longDuration);
        } else {
            Gdx.app.log(JewelThief.class.getName(), "Toast Message: " + message);
        }
    }

    public String getVersionName() {
        return (androidInterface == null ? VERSION_NAME : androidInterface.getVersionName());
    }

    /**
     * Globally sets the locale of the application.
     *
     * @param localeId The locale id to set (en, de, es, fr, ...)
     * @return The bundle associated with the given locale id
     */
    public I18NBundle setLocale(String localeId) {
        preferences.putString("language", localeId).flush();
        if (!assetManager.isLoaded("i18n/" + localeId)) {
            assetManager.load("i18n/" + localeId, I18NBundle.class);
            assetManager.finishLoading();
        }
        bundle = assetManager.get("i18n/" + localeId, I18NBundle.class);
        return bundle;
    }

    public void playCymbalSound() {
        if (preferences.getBoolean(PrefsKeys.ENABLE_SOUND)) {
            long id = soundCymbal.play();
            soundClick.setPitch(id, 0.5f + Utils.randomWithin(0f, 1.5f));
        }
    }

    void renderFireworksEffect(SpriteBatch batch, float delta) {
        particles.renderFireworks(batch, delta);
    }

    void resetFireworksEffects() {
        particles.resetFireworksEffects();
    }

}