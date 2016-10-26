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

public class Soldier extends Enemy {

    public Soldier() {
        super(Soldier.class.getSimpleName(), 2.5f);
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 5, y - 2,
                x + 5, y + 4,
                x + 1, y + 15,
                x - 5, y + 16,
                x - 5, y - 4,
                x - 3, y - 4,
                x - 3, y - 16,
                x + 3, y - 16,
                x + 3, y - 4,
                x + 5, y - 4,
        };
        return new Polygon(vertices);
    }

}
