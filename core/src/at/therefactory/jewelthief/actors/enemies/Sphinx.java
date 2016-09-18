package at.therefactory.jewelthief.actors.enemies;

import com.badlogic.gdx.math.Polygon;

public class Sphinx extends at.therefactory.jewelthief.actors.Enemy {

    public Sphinx() {
        super(Sphinx.class.getSimpleName(), 1.6f);
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 10, y - 5,
                x + 14, y + 3,
                x + 8, y + 13,
                x - 8, y + 13,
                x - 14, y + 3,
                x - 10, y - 5,
                x - 3, y - 13,
                x + 3, y - 13
        };
        return new Polygon(vertices);
    }

}
