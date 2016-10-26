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

import at.therefactory.jewelthief.actors.Enemy;

public class Battlecopter extends Enemy {

    public Battlecopter() {
        super(Battlecopter.class.getSimpleName(), 2.5f);
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 13, y,
                x + 14, y + 3,
                x + 10, y + 7,
                x + 6, y,
                x + 3, y,
                x - 5, y + 6,
                x + 3, y + 8,
                x - 15, y + 6,
                x - 7, y + 4,
                x - 14, y - 1,
                x - 14, y - 8,
                x - 9, y - 6,
                x - 9, y - 8,
                x - 4, y - 8,
                x - 2, y - 5,
        };
        return new Polygon(vertices);
    }

}
