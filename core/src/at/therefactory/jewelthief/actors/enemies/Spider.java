package at.therefactory.jewelthief.actors.enemies;

import com.badlogic.gdx.math.Polygon;

import at.therefactory.jewelthief.actors.Enemy;

public class Spider extends Enemy {

    public Spider() {
        super(Spider.class.getSimpleName(), 2.2f);
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x, y - 3,
                x + 3, y - 5,
                x + 6, y,
                x + 4, y + 5,
                x, y + 2,
                x - 4, y + 5,
                x - 6, y,
                x - 3, y - 5,
        };
        return new Polygon(vertices);
    }

}
