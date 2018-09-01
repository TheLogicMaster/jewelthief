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

package at.therefactory.jewelthief.misc;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import java.util.Random;

import at.therefactory.jewelthief.JewelThief;
import at.therefactory.jewelthief.constants.I18NKeys;
import at.therefactory.jewelthief.constants.PrefsKeys;
import at.therefactory.jewelthief.ui.buttons.GrayButton;

public class Utils {

    // sprite animation
    private static float scaleMin = 0.9f;
    private static float scaleMax = 1.1f;
    private static float period = 1.8f;
    private static float rotate = 5f;

    private static final Random random = new Random();
    private static final GlyphLayout testLayout = new GlyphLayout(JewelThief.getInstance().getFont(), "Teststring");

    public static boolean within(float v, float min, float max) {
        return v >= min && v <= max;
    }

    public static boolean within(short v, int min, int max) {
        return v >= min && v <= max;
    }

    public static int randomWithin(int min, int max) {
        return random.nextInt(max + 1) + min;
    }

    public static float randomWithin(float min, float max) {
        return random.nextFloat() * (max - min) + min;
    }

    /**
     * Returns true if the current execution platform is Android, else returns false.
     *
     * @return
     */
    public static boolean isAndroid() {
        return Gdx.app.getType().equals(Application.ApplicationType.Android);
    }


    /**
     * Returns true if the current execution platform is Desktop, else returns false.
     *
     * @return
     */
    public static boolean isDesktop() {
        return Gdx.app.getType().equals(Application.ApplicationType.Desktop);
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
     *
     * @return
     */
    public static String getBestScoreString() {
        Preferences prefs = JewelThief.getInstance().getPreferences();
        if (prefs.contains(PrefsKeys.BEST_SCORE_NUM_JEWELS) && prefs.contains(PrefsKeys.BEST_SCORE_NUM_SECONDS))
            return JewelThief.getInstance().getBundle().format(I18NKeys.YOUR_BEST_SCORE_IS,
                    prefs.getInteger(PrefsKeys.BEST_SCORE),
                    prefs.getInteger(PrefsKeys.BEST_SCORE_NUM_JEWELS),
                    Utils.secondsToTimeString(prefs.getInteger(PrefsKeys.BEST_SCORE_NUM_SECONDS)));
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
        if (Utils.within(vec3, button)) {
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
     *  @param vec3
     * @param buttons
     */
    public static void pressOrReleaseButtons(Vector3 vec3, GrayButton... buttons) {
        for (GrayButton button : buttons) {
            pressOrReleaseButton(vec3, button);
        }
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

    public static float oscilliate(float x, float min, float max, float period) {
        return max - (float) (Math.sin(x * 2f * Math.PI / period) * ((max - min) / 2f) + ((max - min) / 2f));
    }

    public static void oscilliate(SpriteBatch batch, Sprite sprite, float x, float y, float width, float height, float elapsedTime) {
        batch.draw(sprite, x, y, 0, 0, width, height,
                Utils.oscilliate(elapsedTime, scaleMin, scaleMax, period),
                Utils.oscilliate(elapsedTime, scaleMin, scaleMax, -period),
                Utils.oscilliate(elapsedTime, -rotate, rotate, period));
    }

    public static void oscilliate(SpriteBatch batch, Sprite sprite, float elapsedTime) {
        oscilliate(batch, sprite, sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight(), elapsedTime);
    }
}
