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
import at.therefactory.jewelthief.misc.Util;

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
		new Level("Mars", 		Emerald.class, 		4, 		Bandit.class, 		3),
        new Level("Mars", 		Opal.class, 		6, 		BigBandit.class, 	4),
        new Level("Egypt", 		Sapphire.class, 	8, 		Camel.class, 		4),
        new Level("Egypt", 		Garnet.class,		10,		Sphinx.class,		5),
        new Level("Sea", 		Pearl.class,		11, 	Sailboat.class,		6),
        new Level("Sea",		Onyx.class, 		13, 	Battleship.class,	6),
        new Level("Cave",		Ruby.class,			11,		Bat.class,			6),
        new Level("Cave",		Aquamarine.class,	16,		Spider.class, 		17),
        new Level("Castle",		Gold.class, 		20, 	Soldier.class, 		6),
        new Level("Castle", 	Amethyst.class,		22, 	Wizard.class, 		7),
        new Level("City", 		Peridot.class,		24,		Helicopter.class,	8),
        new Level("City",		Topaz.class,		26, 	Battlecopter.class, 9),
        new Level("Rainbow", 	Citrine.class,		35, 	Drop.class,			20),
        new Level("Rainbow",    Diamond.class,      31,     Cloud.class,        8),
	};

	// application
    public static final String DEFAULT_LOCALE = "en"; // en, de, es
    public static final String VERSION_NAME = "1.0.7";
	
    // ui
    public static final long TIME_PLAYER_STAYS_RED_WHEN_HURT = 400; // in ms
    public static final int MENU_SCREEN_NUM_STARS = Util.randomWithin(20, 40);
    public static final int FONT_OFFSET_ON_BUTTON_PRESS = 1; // in pixels
    public static final float FADING_SPEED = 0.05f;
    public static final int HIGHSCORES_LINE_HEIGHT = 9;
    
	// other
	public static final int PLAYERNAME_MAXLEN = 20;
	public static final int NUM_HTTP_REQUEST_TRIES = 3;
}
