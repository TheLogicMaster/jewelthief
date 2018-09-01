package at.therefactory.jewelthief;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import at.therefactory.jewelthief.misc.Utils;

/**
 * Created by Christian on 16.02.2018.
 */

public class MediaManager {

    public static AssetManager assetManager = new AssetManager();

    public static Sound playSoundRandomPitch(String assetPath) {
        return playSound(assetPath, 0.5f + Utils.randomWithin(0f, 1.5f), false, 1f);
    }

    public static Sound playSoundRandomPitch(String assetPath, float volume) {
        return playSound(assetPath, 0.5f + Utils.randomWithin(0f, 1.5f), false, volume);
    }

    public static Sound playSound(String assetPath) {
        return playSound(assetPath, 1f, false, 1f);
    }

    public static Sound playSound(String assetPath, float volume) {
        return playSound(assetPath, 1f, false, volume);
    }

    public static Sound playSound(String assetPath, float pitch, boolean looping, float volume) {
        preloadSound(assetPath);
        Sound sound = assetManager.get(assetPath, Sound.class);
        long soundId = sound.play();
        sound.setLooping(soundId, looping);
        sound.setPitch(soundId, pitch);
        sound.setVolume(soundId, volume);
        return sound;
    }

    public static Music playMusic(String assetPath) {
        return playMusic(assetPath, false, 1f);
    }

    public static Music playMusic(String assetPath, boolean looping) {
        return playMusic(assetPath, looping, 1f);
    }

    public static Music playMusic(String assetPath, float volume) {
        return playMusic(assetPath, false, volume);
    }

    public static Music playMusic(String assetPath, boolean looping, float volume) {
        preloadMusic(assetPath);
        Music music = assetManager.get(assetPath, Music.class);
        music.setLooping(looping);
        music.setVolume(volume);
        music.play();
        return music;
    }

    public static void preloadSound(String assetPath) {
        if (!assetManager.isLoaded(assetPath)) {
            assetManager.load(assetPath, Sound.class);
            assetManager.finishLoading();
        }
    }

    public static void preloadMusic(String assetPath) {
        if (!assetManager.isLoaded(assetPath)) {
            assetManager.load(assetPath, Music.class);
            assetManager.finishLoading();
        }
    }

    public static void unload(String assetPath) {
        assetManager.unload(assetPath);
    }
}
