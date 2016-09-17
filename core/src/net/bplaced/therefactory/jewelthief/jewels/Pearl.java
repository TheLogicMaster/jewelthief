package net.bplaced.therefactory.jewelthief.jewels;

import com.badlogic.gdx.math.Polygon;

public class Pearl extends Jewel {

    public Pearl() {
        super(Pearl.class.getSimpleName());
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 4, y - 2,
                x + 4, y + 2,
                x, y + 5,
                x - 4, y + 2,
                x - 4, y - 2,
                x, y - 5
        };
        return new Polygon(vertices);
    }
}
