package net.bplaced.therefactory.jewelthief;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.I18NBundle;

import net.bplaced.therefactory.jewelthief.constants.Config;
import net.bplaced.therefactory.jewelthief.constants.PrefsKeys;
import net.bplaced.therefactory.jewelthief.misc.AndroidInterface;
import net.bplaced.therefactory.jewelthief.misc.Util;
import net.bplaced.therefactory.jewelthief.net.HTTP;
import net.bplaced.therefactory.jewelthief.screens.GameIntroScreen;
import net.bplaced.therefactory.jewelthief.screens.GameScreen;
import net.bplaced.therefactory.jewelthief.screens.LogoScreen;
import net.bplaced.therefactory.jewelthief.screens.MenuScreen;
import net.bplaced.therefactory.jewelthief.ui.Particles;

public class JewelThief extends Game {

    private static JewelThief instance;
    private AndroidInterface androidInterface;

    private SpriteBatch batch;
    private ShapeRenderer sr;
    private TextureAtlas textureAtlas;
    private AssetManager assetManager;
    private Preferences prefs;
    private Particles particles;

    private MenuScreen menuScreen;
    private GameIntroScreen gameIntroScreen;
    private GameScreen gameScreen;
    private LogoScreen theRefactoryLogoScreen;

    private BitmapFont font;
    private Sound soundClick, soundCymbal;
    private Music music;
    private Sprite fade;
    private I18NBundle bundle;

    private String[] musicFiles; // list of music files located in "assets/audio/music"
    private int currentMusicFile = -1; // pointer to the music file that is currently being played

    public JewelThief() {
    }

    public JewelThief(AndroidInterface androidInterface) {
        this.androidInterface = androidInterface;
    }

    @Override
    public void create() {
        instance = this;
        prefs = Gdx.app.getPreferences(PrefsKeys.PREFERENCES_FILE_ID);

        // init font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/amiga4ever pro2.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 8;
        parameter.mono = true;
        font = generator.generateFont(parameter);
        generator.dispose();

        // load initial preferences
        if (!prefs.contains(PrefsKeys.ID)) {
            prefs.putString(PrefsKeys.ID, System.nanoTime() + "0" + Util.randomWithin(1000, 9999)).flush();
        }
        if (!prefs.contains(PrefsKeys.LANGUAGE)) {
            prefs.putString(PrefsKeys.LANGUAGE, Config.DEFAULT_LOCALE).flush();
        }
        for (String setting : new String[]{PrefsKeys.ENABLE_SOUND, PrefsKeys.ENABLE_MUSIC}) {
            if (!prefs.contains(setting)) {
                prefs.putBoolean(setting, true).flush();
            }
        }

        // initialize singleton vars
        batch = new SpriteBatch();
        sr = new ShapeRenderer();
        textureAtlas = new TextureAtlas("textures.pack");
        assetManager = new AssetManager();
        fade = textureAtlas.createSprite("fade");
        fade.setSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        fade.setPosition(0, 0);
        particles = new Particles(textureAtlas);

        // load resources
        assetManager.load("audio/sounds/mouse_click.ogg", Sound.class);
        assetManager.load("audio/sounds/finger_cymbal_hit.ogg", Sound.class);
        assetManager.load("i18n/" + prefs.getString("language"), I18NBundle.class);
        assetManager.finishLoading();
        soundClick = assetManager.get("audio/sounds/mouse_click.ogg");
        soundCymbal = assetManager.get("audio/sounds/finger_cymbal_hit.ogg");
        bundle = assetManager.get("i18n/" + prefs.getString("language"), I18NBundle.class);

        // try to submit latest highscores
        if (prefs.contains(PrefsKeys.BEST_SCORE_NUM_JEWELS) && prefs.contains(PrefsKeys.BEST_SCORE_NUM_SECONDS)) {
            HTTP.submitHighscores(prefs.getString(PrefsKeys.ID),
                    prefs.getString(PrefsKeys.PLAYER_NAME), prefs.getInteger(PrefsKeys.BEST_SCORE_NUM_JEWELS),
                    prefs.getInteger(PrefsKeys.BEST_SCORE_NUM_SECONDS));
        }

        // first screen
        theRefactoryLogoScreen = new LogoScreen(batch, sr);
        setScreen(theRefactoryLogoScreen);
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
        if (prefs != null)
            prefs.flush();
    }

    public void switchToMainMenu() {
        if (menuScreen == null) {
            menuScreen = new MenuScreen(batch, sr);
        }
        setScreen(menuScreen);
    }

    public void showIntroScreen() {
        if (gameIntroScreen == null) {
            gameIntroScreen = new GameIntroScreen(batch);
        }
        setScreen(gameIntroScreen);
    }

    public Preferences getPreferences() {
        return prefs;
    }

    public void startSinglePlayerGame() {
        if (gameScreen == null) {
            gameScreen = new GameScreen(batch, sr);
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
        if (prefs.getBoolean(PrefsKeys.ENABLE_SOUND))
            soundClick.play();
    }

    public Sprite getFadeSprite() {
        return fade;
    }

    /**
     * Either continues the music playback of a previously paused music file or proceeds to the next music file depending on the proceedToNext flag.
     * If there was no previous music file, a new music file is chosen randomly.
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
                Gdx.app.log(getClass().getName(), "'" + fileList[i].path() + "' loaded");
            }
        }
        
        if (musicFiles.length == 0) {
            Gdx.app.error(getClass().getName(), "Could not find any music files!");
        }
        else {
            // select a music file to play
            if (currentMusicFile == -1) {
                // if there is no previous music file, choose a new one randomly
                currentMusicFile = Util.randomWithin(0, musicFiles.length - 1);
                music = loadMusicAsset(musicFiles[currentMusicFile]);
            } else if (proceedToNext) {
                // switch to the next music file randomly
                int previousMusicFile = currentMusicFile;
                do {
                    currentMusicFile = Util.randomWithin(0, musicFiles.length - 1);
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

    /**
     * Globally sets the locale of the application.
     *
     * @param localeId The locale id to set (en, de, es, fr, ...)
     * @return The bundle associated with the given locale id
     */
    public I18NBundle setLocale(String localeId) {
        prefs.putString("language", localeId).flush();
        if (!assetManager.isLoaded("i18n/" + localeId)) {
            assetManager.load("i18n/" + localeId, I18NBundle.class);
            assetManager.finishLoading();
        }
        bundle = assetManager.get("i18n/" + localeId, I18NBundle.class);
        return bundle;
    }

    public void playCymbalSound() {
        if (prefs.getBoolean(PrefsKeys.ENABLE_SOUND))
            soundCymbal.play();
    }

    public void renderFireworksEffect(SpriteBatch batch, float delta) {
        particles.renderFireworks(batch, delta);
    }

    public void resetFireworksEffects() {
        particles.resetFireworksEffects();
    }

}
