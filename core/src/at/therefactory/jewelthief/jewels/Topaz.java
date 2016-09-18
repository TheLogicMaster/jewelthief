package at.therefactory.jewelthief.jewels;

import com.badlogic.gdx.math.Polygon;

public class Topaz extends Jewel {

    public Topaz() {
        super(Topaz.class.getSimpleName());
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 3, y + 8,
                x - 3, y + 8,
                x - 8, y + 3,
                x - 8, y - 3,
                x - 3, y - 8,
                x + 3, y - 8,
                x + 8, y - 3,
                x + 8, y + 3
        };
        return new Polygon(vertices);
    }

}
