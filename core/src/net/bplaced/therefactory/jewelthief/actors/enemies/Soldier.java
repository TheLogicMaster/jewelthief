package net.bplaced.therefactory.jewelthief.actors.enemies;

import com.badlogic.gdx.math.Polygon;

import net.bplaced.therefactory.jewelthief.actors.Enemy;

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
