package at.therefactory.jewelthief.misc;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

import at.therefactory.jewelthief.JewelThief;
import at.therefactory.jewelthief.constants.I18NKeys;
import at.therefactory.jewelthief.constants.PrefsKeys;
import at.therefactory.jewelthief.ui.buttons.GrayButton;

import java.util.Random;

public class Util {

	private static final Random random = new Random();
	private static final GlyphLayout testLayout = new GlyphLayout(JewelThief.getInstance().getFont(), "Teststring");
    
	private static Random random() {
		return random;
	}

	public static boolean within(float v, float min, float max) {
		return v >= min && v <= max;
	}

	public static int randomWithin(int min, int max) {
		return random().nextInt(max + 1) + min;
	}

	public static float randomWithin(float min, float max) {
		return random().nextFloat() * (max - min) + min;
	}

    /**
     * Returns true if the current execution platform is Android, else returns false.
     * @return
     */
    public static boolean isAndroid() {
        return Gdx.app.getType() == Application.ApplicationType.Android;
    }


    /**
     * Returns true if the current execution platform is Desktop, else returns false.
     * @return
     */
    public static boolean isDesktop() {
        return Gdx.app.getType() == Application.ApplicationType.Desktop;
    }

	public static float randomSignum() {
		float randomNumber = 0;
		int upperBound = 100;
		while (randomNumber == 0)
			randomNumber = randomWithin(0, upperBound);
		return randomNumber < upperBound / 2 ? -1 : 1;
	}

    /**
     * Converts an integer into the format "(mm:ss)".
     *
     * @param numSeconds
     * @return
     */
	public static CharSequence secondsToTimeString(int numSeconds) {
        int hours = numSeconds / 3600;
		int minutes = (numSeconds - (hours * 3600)) / 60;
		int seconds = Math.max(0, numSeconds - (hours * 3600) - (minutes * 60));
		return (hours > 0 ? String.format("%2s", hours).replace(' ', '0') + ":" : "")
                + String.format("%2s", minutes).replace(' ', '0')
                + ":" + String.format("%2s", seconds).replace(' ', '0');
	}

    /**
     * Returns a human readable string presenting the locally saved best score.
     * @return
     */
	public static String getBestScoreString() {
		Preferences prefs = JewelThief.getInstance().getPreferences();
		if (prefs.contains(PrefsKeys.BEST_SCORE_NUM_JEWELS) && prefs.contains(PrefsKeys.BEST_SCORE_NUM_SECONDS))
			return JewelThief.getInstance().getBundle().format(I18NKeys.YOUR_BEST_SCORE_IS,
			        prefs.getInteger(PrefsKeys.BEST_SCORE),
			        prefs.getInteger(PrefsKeys.BEST_SCORE_NUM_JEWELS),
			        Util.secondsToTimeString(prefs.getInteger(PrefsKeys.BEST_SCORE_NUM_SECONDS)));
		else
			return "";
	}

    /**
     * Returns true if the given vector is enclosed by the button's area.
     *
     * @param vec3
     * @param btn
     * @return
     */
	public static boolean within(Vector3 vec3, GrayButton btn) {
		return within(vec3.x, btn.getX(), btn.getX() + btn.getWidth()) &&
				within(vec3.y, btn.getY(), btn.getY() + btn.getHeight());
	}

    /**
     * Fires the given button's "pressed" state if the given vector is enclosed by its area,
     * and releases the button otherwise.
     *
     * @param vec3
     * @param button
     */
	public static void pressOrReleaseButton(Vector3 vec3, GrayButton button) {
		if (Util.within(vec3, button)) {
			if (!button.isPressed()) {
				JewelThief.getInstance().playButtonClickSound();
			}
			button.press();
		} else {
			button.release();
		}
	}

    /**
     * Fires the "pressed" state of the given buttons if the given vector is enclosed by its area,
     * and releases the buttons otherwise.
     *
     * @param vec3
     * @param buttons
     */
    public static void pressOrReleaseButtons(Vector3 vec3, GrayButton... buttons) {
        for (GrayButton button : buttons) {
            pressOrReleaseButton(vec3, button);
        }
    }
    
	public static float getFontHeight() {
	    return testLayout.height;
	}

    /**
     * Returns true if the given vector is enclosed by the sprite's area.
     *
     * @param vec3
     * @param sprite
     * @return
     */
    public static boolean within(Vector3 vec3, Sprite sprite) {
        return within(vec3.x, sprite.getX(), sprite.getX() + sprite.getWidth()) &&
				within(vec3.y, sprite.getY(), sprite.getY() + sprite.getHeight());
    }

}
