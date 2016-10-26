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

public class BigBandit extends at.therefactory.jewelthief.actors.Enemy {

    public BigBandit() {
        super(BigBandit.class.getSimpleName(), 1.3f);
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x, y - 16,
                x + 2, y - 8,
                x + 16, y + 2,
                x + 16, y + 12,
                x, y + 12,
                x - 1, y + 15,
                x - 5, y + 15,
                x - 8, y + 10,
                x - 11, y + 16,
                x - 14, y + 12,
                x - 11, y + 1,
                x - 16, y,
                x - 16, y - 3,
                x - 8, y - 8,
                x - 7, y - 15
        };
        return new Polygon(vertices);
    }
}
