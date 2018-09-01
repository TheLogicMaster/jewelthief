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

package at.therefactory.jewelthief.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import at.therefactory.jewelthief.JewelThief;

import static at.therefactory.jewelthief.constants.Config.WINDOW_HEIGHT;
import static at.therefactory.jewelthief.constants.Config.WINDOW_WIDTH;

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
