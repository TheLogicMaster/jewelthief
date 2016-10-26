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

package at.therefactory.jewelthief.actors;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;

import at.therefactory.jewelthief.JewelThief;

import static at.therefactory.jewelthief.constants.Config.INITIAL_NUM_MEN;
import static at.therefactory.jewelthief.constants.Config.TIME_PLAYER_STAYS_RED_WHEN_HURT;

public class Player extends Actor {

    private short numMen;
    private short numCollectedJewels;
    private float lastX;
    private long isHurtStartTime;

    private final Sprite facingLeftSprite;
    private final Sprite facingRightSprite;
    private final Sprite facingLeftRedSprite;
    private final Sprite facingRightRedSprite;

    private boolean isHurt;
    private boolean facingRight; // faces left if false
    private boolean alreadyTouchingEnemy;
    private boolean alreadyTouchingWall;

    public Player() {
        super("White" + Player.class.getSimpleName());
        facingLeftSprite = JewelThief.getInstance().getTextureAtlas().createSprite("WhitePlayer");
        facingRightSprite = JewelThief.getInstance().getTextureAtlas().createSprite("WhitePlayerRight");
        facingLeftRedSprite = JewelThief.getInstance().getTextureAtlas().createSprite("RedPlayer");
        facingRightRedSprite = JewelThief.getInstance().getTextureAtlas().createSprite("RedPlayerRight");
        reset();
    }

    @Override
    public void update() {
        super.update();
        facingLeftSprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);
        facingRightSprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);
        facingLeftRedSprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);
        facingRightRedSprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);

        if (position.x != lastX) {
            facingRight = position.x - lastX > 0;
        }
        if (isHurt) {
            if (isHurtStartTime < System.currentTimeMillis() - TIME_PLAYER_STAYS_RED_WHEN_HURT) {
                isHurt = false;
            }
            if (facingRight)
                sprite = facingRightRedSprite;
            else
                sprite = facingLeftRedSprite;
        } else if (facingRight)
            sprite = facingRightSprite;
        else
            sprite = facingLeftSprite;
        lastX = position.x;
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        if (facingRight) {
            float[] vertices = {x + 7, y + 16, x, y + 16, x - 8, y + 4, x - 8, y + 2, x - 6, y, x - 5, y - 5, x - 15, y - 9, x - 15, y - 12, x - 11, y - 16, x - 7,
                    y - 14, x - 7, y - 11, x + 2, y - 7, x + 6, y - 16, x + 11, y - 16, x + 15, y - 12, x + 14, y - 10, x + 10, y - 10, x + 6, y - 2, x + 13, y + 5,
                    x + 13, y + 6, x + 9, y + 9, x + 9, y + 13,};
            return new Polygon(vertices);
        } else {
            float[] vertices = {x, y + 16, x - 6, y + 16, x - 9, y + 13, x - 8, y + 8, x - 10, y + 8, x - 13, y + 5, x - 5, y - 2, x - 10, y - 11, x - 15, y - 11,
                    x - 15, y - 12, x - 10, y - 16, x - 6, y - 16, x, y - 7, x + 11, y - 16, x + 15, y - 9, x + 5, y - 5, x + 8, y + 3};
            return new Polygon(vertices);
        }
    }

    public void turnTemporarilyRed(boolean turnRed) {
        isHurt = turnRed;
        if (isHurt)
            isHurtStartTime = System.currentTimeMillis();
    }

    public void setNumCollectedJewels(short numCollectedJewels) {
        this.numCollectedJewels = numCollectedJewels;
    }

    public void decrementNumMen() {
        numMen--;
    }

    public void incrementNumMen() {
        numMen++;
    }

    public void incrementNumCollectedJewels() {
        numCollectedJewels++;
    }

    public int getNumMen() {
        return numMen;
    }

    public int getNumCollectedJewels() {
        return numCollectedJewels;
    }

    public boolean isAlreadyTouchingWall() {
        return alreadyTouchingWall;
    }

    public void setAlreadyTouchingWall(boolean alreadyTouchingWall) {
        this.alreadyTouchingWall = alreadyTouchingWall;
    }

    public void setAlreadyTouchingEnemy(boolean alreadyTouchingEnemy) {
        this.alreadyTouchingEnemy = alreadyTouchingEnemy;
    }

    public boolean isAlreadyTouchingEnemy() {
        return alreadyTouchingEnemy;
    }

    public void reset() {
        numMen = INITIAL_NUM_MEN;
        numCollectedJewels = 0;
        lastX = 0;
        isHurt = false;
        alreadyTouchingEnemy = false;
        alreadyTouchingWall = false;
        facingRight = true;
    }
}
