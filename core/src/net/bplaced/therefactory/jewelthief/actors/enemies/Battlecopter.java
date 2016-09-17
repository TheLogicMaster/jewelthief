package net.bplaced.therefactory.jewelthief.actors.enemies;

import com.badlogic.gdx.math.Polygon;

import net.bplaced.therefactory.jewelthief.actors.Enemy;

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
