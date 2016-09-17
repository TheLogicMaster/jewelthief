package net.bplaced.therefactory.jewelthief.actors.enemies;

import com.badlogic.gdx.math.Polygon;

import net.bplaced.therefactory.jewelthief.actors.Enemy;

public class Helicopter extends Enemy {

    public Helicopter() {
        super(Helicopter.class.getSimpleName(), 2.5f);
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 16, y - 3,
                x + 8, y + 3,
                x + 13, y + 5,
                x + 13, y + 6,
                x - 4, y + 6,
                x - 16, y + 7,
                x - 11, y + 4,
                x - 16, y - 1,
                x - 6, y - 1,
                x - 3, y - 7,
                x + 11, y - 7,
        };
        return new Polygon(vertices);
    }

}
