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

package at.therefactory.jewelthief.jewels;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import at.therefactory.jewelthief.JewelThief;

public abstract class Jewel {

    private final Sprite sprite;
    private final Vector2 position;

    Jewel(String spriteId) {
        position =  new Vector2();
        sprite = JewelThief.getInstance().getTextureAtlas().createSprite(spriteId);

        // initially hidden from visible screen
        setPosition(-100, -100);
        sprite.setPosition(-100, -100);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setPosition(float x, float y) {
        position.x = x;
        position.y = y;
    }

    public void update() {
        sprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);
    }

    Vector2 getPosition() {
        return position;
    }

    public abstract Polygon getPolygon();

}
