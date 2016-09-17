package net.bplaced.therefactory.jewelthief.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.bplaced.therefactory.jewelthief.JewelThief;
import net.bplaced.therefactory.jewelthief.constants.Config;

class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        int scale = 2;
        config.width = (Config.WINDOW_WIDTH * scale);
        config.height = (Config.WINDOW_HEIGHT * scale);
        config.vSyncEnabled = true;
        config.resizable = true;
        new LwjglApplication(new JewelThief(), config);
    }
}
