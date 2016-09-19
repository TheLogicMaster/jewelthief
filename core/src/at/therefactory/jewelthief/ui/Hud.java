package at.therefactory.jewelthief.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.I18NBundle;

import at.therefactory.jewelthief.Game;
import at.therefactory.jewelthief.JewelThief;
import at.therefactory.jewelthief.jewels.Jewel;
import at.therefactory.jewelthief.misc.Util;
import at.therefactory.jewelthief.ui.buttons.GrayButton;

import static at.therefactory.jewelthief.constants.Colors.WINDOWS_GRAY;
import static at.therefactory.jewelthief.constants.Config.WINDOW_WIDTH;
import static at.therefactory.jewelthief.constants.Config.levels;
import static at.therefactory.jewelthief.constants.I18NKeys.MAN;
import static at.therefactory.jewelthief.constants.I18NKeys.MEN;

public class Hud {

    private final GrayButton showMenuButton;
    private final Game game;
    private final BitmapFont font;
    private final Rectangle hud;
    private final Jewel[] jewelsToDisplay;
    private I18NBundle bundle;

    public Hud(Game game) {
        this.game = game;
        bundle = JewelThief.getInstance().getBundle();
        font = JewelThief.getInstance().getFont();
        hud = new Rectangle((WINDOW_WIDTH - game.getBackground().getWidth()) / 2,
                game.getBackground().getHeight() + game.getBackground().getY(), WINDOW_WIDTH, 31);
        jewelsToDisplay = new Jewel[levels.length];
        for (int i = 0; i < jewelsToDisplay.length; i++) {
            try {
                jewelsToDisplay[i] = levels[i].getJewelClass().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        showMenuButton = new GrayButton("x", hud.getX() + hud.getWidth() - 31, hud.getY(), hud.getHeight(), hud.getHeight());
        showMenuButton.setBorderSize(2);
    }

    public void render(ShapeRenderer sr) {
        sr.setColor(WINDOWS_GRAY);
        sr.rect(hud.x, hud.y, hud.width, hud.height);
        showMenuButton.renderShape(sr);
    }

    public void postRender(ShapeRenderer sr) {
        // do nothing
    }

    public void render(SpriteBatch batch) {
        font.setColor(Color.DARK_GRAY);
        showMenuButton.renderCaption(batch);
        font.setColor(Color.BLACK);
        int yOffsetFromHudY = 19;
        font.draw(batch, bundle.get(game.getCurrentJewelName()), hud.getX() + 15, hud.getY() + yOffsetFromHudY);
        font.draw(batch, Util.secondsToTimeString(game.getGameDuration()), hud.getX() + 370, hud.getY() + yOffsetFromHudY);
        font.draw(batch, Math.max(0, game.getPlayer().getNumMen()) + " " + (game.getPlayer().getNumMen() == 1 ? bundle.get(MAN) :
                bundle.get(MEN)), hud.getX() + 415, hud.getY() + yOffsetFromHudY);
        int currWidth = 0;
        int indexOfFirstJewelToShow = Util.within(game.getCurrentLevel(), 0, 13) ? 0 : 13;
        for (int i = indexOfFirstJewelToShow; i <= game.getCurrentLevel(); i++) {
            if (i > indexOfFirstJewelToShow)
                currWidth += jewelsToDisplay[i - 1].getSprite().getWidth() + 5;
            batch.draw(jewelsToDisplay[i].getSprite(), hud.getX() + 95 + currWidth, hud.getY() + (15 - jewelsToDisplay[i].getSprite().getHeight() / 2));
        }
    }

    public void postRender(SpriteBatch batch) {
        // do nothing
    }

    public GrayButton getShowMenuButton() {
        return showMenuButton;
    }

    public void show() {
        bundle = JewelThief.getInstance().getBundle();
    }
}
