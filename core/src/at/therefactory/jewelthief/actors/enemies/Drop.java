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

package at.therefactory.jewelthief.actors.enemies;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

import at.therefactory.jewelthief.actors.Enemy;

public class Drop extends Enemy {

    public Drop() {
        super(Drop.class.getSimpleName(), 1.3f);
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 5, y - 3,
                x, y + 9,
                x - 5, y - 3,
                x - 2, y - 9,
                x + 2, y - 9
        };
        return new Polygon(vertices);
    }

    @Override
    public void update(Rectangle enemyField) {
        if (!sprite.getBoundingRectangle().contains(enemyField)) {
            if (sprite.getBoundingRectangle().x + sprite.getBoundingRectangle().width >= enemyField.x + enemyField.width) {
                movementInverter.x = -1;
            } else if (sprite.getBoundingRectangle().x <= enemyField.x) {
                movementInverter.x = 1;
            } else if (sprite.getBoundingRectangle().y <= enemyField.y) {
                position.y = (enemyField.y + enemyField.getHeight());
            }
        }
        position.x += speed * movementInverter.x;
        position.y -= speed;
        sprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);
    }

}
