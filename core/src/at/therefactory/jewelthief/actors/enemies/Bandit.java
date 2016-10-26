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

public class Bandit extends Enemy {

    public Bandit() {
        super(Bandit.class.getSimpleName(), 1f);
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 5, y - 10,
                x + 13, y + 2,
                x + 13, y + 7,
                x + 10, y + 10,
                x + 7, y,
                x, y + 10,
                x - 4, y + 10,
                x - 14, y + 5,
                x - 14, y - 1,
                x - 12, y - 8,
                x - 14, y - 8,
                x - 14, y - 11,
                x - 9, y - 11,
                x - 5, y - 5,
                x, y - 10
        };
        return new Polygon(vertices);
    }
}
