package net.bplaced.therefactory.jewelthief.jewels;

import com.badlogic.gdx.math.Polygon;

public class Garnet extends Jewel {

    public Garnet() {
        super(Garnet.class.getSimpleName());
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 5, y - 2,
                x + 5, y + 2,
                x, y + 8,
                x - 5, y + 2,
                x - 5, y - 2,
                x, y - 8,
        };
        return new Polygon(vertices);
    }

}
