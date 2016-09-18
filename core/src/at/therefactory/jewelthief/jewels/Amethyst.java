package at.therefactory.jewelthief.jewels;

import com.badlogic.gdx.math.Polygon;

public class Amethyst extends Jewel {

    public Amethyst() {
        super(Amethyst.class.getSimpleName());
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 7, y - 7,
                x + 7, y + 7,
                x - 7, y + 7,
                x - 7, y - 7
        };
        return new Polygon(vertices);
    }

}
