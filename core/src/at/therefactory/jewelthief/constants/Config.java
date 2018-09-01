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

package at.therefactory.jewelthief.constants;

import at.therefactory.jewelthief.actors.enemies.Bandit;
import at.therefactory.jewelthief.actors.enemies.Bat;
import at.therefactory.jewelthief.actors.enemies.Battlecopter;
import at.therefactory.jewelthief.actors.enemies.Battleship;
import at.therefactory.jewelthief.actors.enemies.BigBandit;
import at.therefactory.jewelthief.actors.enemies.Camel;
import at.therefactory.jewelthief.actors.enemies.Cloud;
import at.therefactory.jewelthief.actors.enemies.Drop;
import at.therefactory.jewelthief.actors.enemies.Helicopter;
import at.therefactory.jewelthief.actors.enemies.Sailboat;
import at.therefactory.jewelthief.actors.enemies.Soldier;
import at.therefactory.jewelthief.actors.enemies.Sphinx;
import at.therefactory.jewelthief.actors.enemies.Spider;
import at.therefactory.jewelthief.actors.enemies.Wizard;
import at.therefactory.jewelthief.jewels.Amethyst;
import at.therefactory.jewelthief.jewels.Aquamarine;
import at.therefactory.jewelthief.jewels.Citrine;
import at.therefactory.jewelthief.jewels.Diamond;
import at.therefactory.jewelthief.jewels.Emerald;
import at.therefactory.jewelthief.jewels.Garnet;
import at.therefactory.jewelthief.jewels.Gold;
import at.therefactory.jewelthief.jewels.Onyx;
import at.therefactory.jewelthief.jewels.Opal;
import at.therefactory.jewelthief.jewels.Pearl;
import at.therefactory.jewelthief.jewels.Peridot;
import at.therefactory.jewelthief.jewels.Ruby;
import at.therefactory.jewelthief.jewels.Sapphire;
import at.therefactory.jewelthief.jewels.Topaz;
import at.therefactory.jewelthief.levels.Level;
import at.therefactory.jewelthief.misc.Utils;

public class Config {

	/**
     * if set to true the following changes apply to the game:
     *  - logos are played in a loop until the screen is touched
     *  - polygons of players, enemies and jewels can be made visible
     *  - game can be paused at will
	 * Important: set to false in release versions
	 */
    public static final boolean DEBUG_MODE = false;

	// viewport (next best 16:9 resolution from original level background image size)
	public static final int WINDOW_WIDTH = 512;
	public static final int WINDOW_HEIGHT = 288;

	// game logic
	public static final int START_LEVEL = 0;
	public static final int PLUS_ONE_MAN_INTERVAL = 10; // num of jewels to collect to gain one extra man
	public static final int INITIAL_NUM_MEN = 18;
	public static final Level[] levels = {
		new Level("Mars", 		Emerald.class, 		(short) 4, 		Bandit.class, 		(short) 3),
        new Level("Mars", 		Opal.class, 		(short) 6, 		BigBandit.class, 	(short) 4),
        new Level("Egypt", 		Sapphire.class, 	(short) 8, 		Camel.class, 		(short) 4),
        new Level("Egypt", 		Garnet.class,		(short) 10,		Sphinx.class,		(short) 5),
        new Level("Sea", 		Pearl.class,		(short) 11, 	Sailboat.class,		(short) 6),
        new Level("Sea",		Onyx.class, 		(short) 13, 	Battleship.class,	(short) 6),
        new Level("Cave",		Ruby.class,			(short) 11,		Bat.class,			(short) 6),
        new Level("Cave",		Aquamarine.class,	(short) 16,		Spider.class, 		(short) 17),
        new Level("Castle",		Gold.class, 		(short) 20, 	Soldier.class, 		(short) 6),
        new Level("Castle", 	Amethyst.class,		(short) 22, 	Wizard.class, 		(short) 7),
        new Level("City", 		Peridot.class,		(short) 24,		Helicopter.class,	(short) 8),
        new Level("City",		Topaz.class,		(short) 26, 	Battlecopter.class, (short) 9),
        new Level("Rainbow", 	Citrine.class,		(short) 35, 	Drop.class,			(short) 20),
        new Level("Rainbow",    Diamond.class,      (short) 31,     Cloud.class,        (short) 8),
	};

	// application
    public static final String DEFAULT_LOCALE = "en"; // en, de, es
    public static final String VERSION_NAME = "2018.9";
	
    // ui
    public static final long TIME_PLAYER_STAYS_RED_WHEN_HURT = 400; // in ms
    public static final int MENU_SCREEN_NUM_STARS = Utils.randomWithin(20, 40);
    public static final int FONT_OFFSET_ON_BUTTON_PRESS = 1; // in pixels
    public static final float FADING_SPEED = .05f;
    public static final float HIGHSCORES_LINE_HEIGHT = 9;
    
	// other
	public static final int PLAYERNAME_MAXLEN = 20;
	public static final int NUM_HTTP_REQUEST_TRIES = 3;
	public static final float INITIAL_SCROLLBAR_POSITION_Y = 200;
	public static final String EMAIL = "christian.detamble@outlook.com";
	public static final String URL_TO_SOUNDTRACK = "https://youtu.be/KCuPMm8N57I";
	public static final String URL_TO_PLAY_STORE = "https://play.google.com/store/apps/details?id=at.therefactory.jewelthief";
}
