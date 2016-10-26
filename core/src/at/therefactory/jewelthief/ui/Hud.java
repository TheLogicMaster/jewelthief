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

package at.therefactory.jewelthief.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.StringBuilder;

import at.therefactory.jewelthief.Game;
import at.therefactory.jewelthief.JewelThief;
import at.therefactory.jewelthief.jewels.Jewel;
import at.therefactory.jewelthief.misc.Utils;
import at.therefactory.jewelthief.ui.buttons.GrayButton;

import static at.therefactory.jewelthief.constants.Colors.WINDOWS_GRAY;
import static at.therefactory.jewelthief.constants.Config.WINDOW_WIDTH;
import static at.therefactory.jewelthief.constants.Config.levels;
import static at.therefactory.jewelthief.constants.I18NKeys.MAN;
import static at.therefactory.jewelthief.constants.I18NKeys.MEN;

public class Hud {

    private final GrayButton buttonShowMenu;
    private final Game game;
    private final BitmapFont font;
    private final Rectangle rectangleHud;
    private final Jewel[] jewelsToDisplay;
    private I18NBundle bundle;
    private final StringBuilder stringBuilder;

    // cache temporary variables to avoid instantiation in render method
    private final short yOffsetFromHudY = 19;
    private short currWidth;
    private short indexOfFirstJewelToShow;

    public Hud(Game game) {
        this.game = game;
        this.stringBuilder = new StringBuilder();
        bundle = JewelThief.getInstance().getBundle();
        font = JewelThief.getInstance().getFont();
        rectangleHud = new Rectangle((WINDOW_WIDTH - game.getSpriteBackground().getWidth()) / 2,
                game.getSpriteBackground().getHeight() + game.getSpriteBackground().getY(), WINDOW_WIDTH, 31);
        jewelsToDisplay = new Jewel[levels.length];
        for (int i = 0; i < jewelsToDisplay.length; i++) {
            try {
                jewelsToDisplay[i] = levels[i].getJewelClass().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        buttonShowMenu = new GrayButton("x", rectangleHud.getX() + rectangleHud.getWidth() - 31, rectangleHud.getY(), rectangleHud.getHeight(), rectangleHud.getHeight());
        buttonShowMenu.setBorderSize((short) 2);
    }

    public void render(ShapeRenderer sr) {
        sr.setColor(WINDOWS_GRAY);
        sr.rect(rectangleHud.x, rectangleHud.y, rectangleHud.width, rectangleHud.height);
        buttonShowMenu.renderShape(sr);
    }

    public void postRender(ShapeRenderer sr) {
        // do nothing
    }

    public void render(SpriteBatch batch) {
        font.setColor(Color.DARK_GRAY);
        buttonShowMenu.renderCaption(batch);
        font.setColor(Color.BLACK);
        font.draw(batch, bundle.get(game.getCurrentJewelName()), rectangleHud.getX() + 15, rectangleHud.getY() + yOffsetFromHudY);
        font.draw(batch, Utils.secondsToTimeString(game.getGameDuration()), rectangleHud.getX() + 370, rectangleHud.getY() + yOffsetFromHudY);
        stringBuilder.append(Math.max(0, game.getPlayer().getNumMen())).append(" ").append((game.getPlayer().getNumMen() == 1 ? bundle.get(MAN) : bundle.get(MEN)));
        font.draw(batch, stringBuilder, rectangleHud.getX() + 415, rectangleHud.getY() + yOffsetFromHudY);
        stringBuilder.setLength(0);
        currWidth = 0;
        indexOfFirstJewelToShow = Utils.within(game.getCurrentLevel(), 0, 13) ? 0 : (short) 13;
        for (int i = indexOfFirstJewelToShow; i <= game.getCurrentLevel(); i++) {
            if (i > indexOfFirstJewelToShow)
                currWidth += jewelsToDisplay[i - 1].getSprite().getWidth() + 5;
            batch.draw(jewelsToDisplay[i].getSprite(), rectangleHud.getX() + 95 + currWidth, rectangleHud.getY() + (15 - jewelsToDisplay[i].getSprite().getHeight() / 2));
        }
    }

    public void postRender(SpriteBatch batch) {
        // do nothing
    }

    public GrayButton getButtonShowMenu() {
        return buttonShowMenu;
    }

    public void show() {
        bundle = JewelThief.getInstance().getBundle();
    }
}
