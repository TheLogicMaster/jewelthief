package net.bplaced.therefactory.jewelthief.actors.enemies;

import com.badlogic.gdx.math.Polygon;

import net.bplaced.therefactory.jewelthief.actors.Enemy;

public class Bat extends Enemy {

    public Bat() {
        super(Bat.class.getSimpleName(), 1.7f);
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 16, y - 5,
                x + 16, y + 1,
                x + 8, y + 9,
                x, y + 8,
                x - 8, y + 9,
                x - 16, y + 1,
                x - 16, y - 5,
                x, y - 9
        };
        return new Polygon(vertices);
    }
}
