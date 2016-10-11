package at.therefactory.jewelthief.desktop;

import static at.therefactory.jewelthief.constants.Config.WINDOW_HEIGHT;
import static at.therefactory.jewelthief.constants.Config.WINDOW_WIDTH;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import at.therefactory.jewelthief.JewelThief;

class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        int scale = 2;
        config.width = (WINDOW_WIDTH * scale);
        config.height = (WINDOW_HEIGHT * scale);
        config.vSyncEnabled = true;
        config.resizable = true;
        new LwjglApplication(new JewelThief(), config);
    }
}
