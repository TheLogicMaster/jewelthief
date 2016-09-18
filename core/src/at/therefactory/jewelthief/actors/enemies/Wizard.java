package at.therefactory.jewelthief.actors.enemies;

import com.badlogic.gdx.math.Polygon;

import at.therefactory.jewelthief.actors.Enemy;

public class Wizard extends Enemy {

    public Wizard() {
        super(Wizard.class.getSimpleName(), 3f);
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 6, y - 15,
                x + 6, y,
                x + 8, y,
                x + 8, y + 3,
                x + 12, y + 15,
                x + 9, y + 15,
                x + 5, y + 4,
                x - 2, y + 14,
                x - 3, y + 14,
                x - 3, y + 7,
                x - 7, y + 7,
                x - 11, y + 3,
                x - 8, y - 1,
                x - 12, y - 15,
        };
        return new Polygon(vertices);
    }

}
