package at.therefactory.jewelthief.jewels;

import com.badlogic.gdx.math.Polygon;

public class Opal extends Jewel {

    public Opal() {
        super(Opal.class.getSimpleName());
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 5, y + 2,
                x, y + 9,
                x - 5, y + 2,
                x, y - 8
        };
        return new Polygon(vertices);
    }

}
