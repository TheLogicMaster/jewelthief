package net.bplaced.therefactory.jewelthief.jewels;

import com.badlogic.gdx.math.Polygon;

public class Diamond extends Jewel {

    public Diamond() {
        super(Diamond.class.getSimpleName());
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 11, y + 5,
                x + 5, y + 11,
                x - 5, y + 11,
                x - 11, y + 5,
                x, y - 11
        };
        return new Polygon(vertices);
    }

}
