package at.therefactory.jewelthief.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import at.therefactory.jewelthief.Game;
import at.therefactory.jewelthief.JewelThief;
import at.therefactory.jewelthief.misc.Utils;
import at.therefactory.jewelthief.screens.GameScreen;
import at.therefactory.jewelthief.ui.Hud;

import static at.therefactory.jewelthief.constants.Config.DEBUG_MODE;

/**
 * The input handler for the GameScreen.
 */
public class GameScreenInputAdapter extends InputAdapter {

    private final Hud hud;
    private final Game game;
    private final Viewport viewport;
    private float deltaX;
    private float deltaY;
    private short numTouches;
    private boolean playerDragging;
    private boolean allowButtonClick; // prevent button click when dialog appears while still dragging player around

    public GameScreenInputAdapter(Game game, Viewport viewport, Hud hud) {
        this.game = game;
        this.viewport = viewport;
        this.hud = hud;
        playerDragging = false;
        numTouches = 0;
        allowButtonClick = false;
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean returnValue = super.keyDown(keycode);
        if (keycode == Keys.BACK) {
            if (game.isMenuShown()) {
                JewelThief.getInstance().switchToMainMenu();
                return true;
            } else {
                game.showMenu();
                return true;
            }
        }
        return returnValue;
    }

    @Override
    public boolean keyTyped(char character) {
        boolean returnValue = super.keyTyped(character);
        if (DEBUG_MODE) {
            if (character == '1') {
                game.switchDebug();
                return true;
            }
            else if (character == '2') {
                game.rearrangeEnemies();
                return true;
            }
            else if (character == '3') {
                game.collectAllJewels();
                return true;
            }
        }
        return returnValue;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        boolean returnValue = super.touchDown(screenX, screenY, pointer, button);
        numTouches++;
        if (!(JewelThief.getInstance().getScreen() instanceof GameScreen)) {
            return true;
        }
        pressOrReleaseButtonAt(viewport.unproject(new Vector3(screenX, screenY, 0)));
        return returnValue;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX, screenY, pointer, button);
        numTouches = (short) Math.max(0, numTouches - 1);
        playerDragging = false;
        releaseAllButtons();

        // check if button has been clicked
        Vector3 touchCoordinates = viewport.unproject(new Vector3(screenX, screenY, 0));
        if (!game.isMenuShown() && game.getPlayer().getNumMen() > 0
                && Utils.within(touchCoordinates, hud.getButtonShowMenu())) {
            game.showMenu();
        } else if (game.getPlayer().getNumMen() <= 0) {
            if (allowButtonClick) {
                allowButtonClick = false;

                // play again? yes
                if (Utils.within(touchCoordinates, game.getButtonPlayAgain())) {
                    game.resetGame();
                }
                // play again? no
                else if (Utils.within(touchCoordinates, game.getButtonExit())) {
                    JewelThief.getInstance().switchToMainMenu();
                }
            } else {
                allowButtonClick = true;
            }
        } else if (game.isMenuShown()) {
            // yes
            if (Utils.within(touchCoordinates, game.getButtonYes())) {
                JewelThief.getInstance().switchToMainMenu();
            }
            // no
            else if (Utils.within(touchCoordinates, game.getButtonNo())) {
                game.hideMenu();
            }
            // restart
            else if (Utils.within(touchCoordinates, game.getButtonRestart())) {
                game.resetGame();
            }
        }
        return true;
    }

    private void releaseAllButtons() {
        hud.getButtonShowMenu().release();
        game.getButtonYes().release();
        game.getButtonNo().release();
        game.getButtonRestart().release();
        game.getButtonPlayAgain().release();
        game.getButtonExit().release();
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        super.touchDragged(screenX, screenY, pointer);
        Vector3 touchCoordinates = viewport.unproject(new Vector3(screenX, screenY, 0));
        if (game.isPaused()) {
            pressOrReleaseButtonAt(touchCoordinates);
        } else {
            if (playerDragging && numTouches == 1) {

                // do not exceed right border
                float newX = Math.min(touchCoordinates.x - deltaX, game.getSpriteBackground().getX() + game.getSpriteBackground().getWidth()
                        - game.getPlayer().getSprite().getWidth() / 2 - 1);

                // do not exceed left border
                newX = Math.max(game.getSpriteBackground().getX() + game.getPlayer().getSprite().getWidth() / 2, newX);

                // do not exceed lower border
                float newY = Math.max(touchCoordinates.y - deltaY, game.getSpriteBackground().getY() + game.getPlayer().getSprite().getHeight() / 2);

                // do not exceed upper border
                newY = Math.min(game.getSpriteBackground().getY() + game.getSpriteBackground().getHeight() - game.getPlayer().getSprite().getHeight() / 2 - 1, newY);

                game.getPlayer().setPosition(newX, newY);
            } else {
                playerDragging = true;
            }
            // delta between touch point and player position
            deltaX = (touchCoordinates.x - game.getPlayer().getPosition().x);
            deltaY = (touchCoordinates.y - game.getPlayer().getPosition().y);
        }
        return true;
    }

    private void pressOrReleaseButtonAt(Vector3 screenCoord) {
        // "play again?" dialog after winning game or no men left
        if (game.getPlayer().getNumMen() <= 0) {
            Utils.pressOrReleaseButton(screenCoord, game.getButtonPlayAgain());
            Utils.pressOrReleaseButton(screenCoord, game.getButtonExit());
        }

        // "givin' up already" dialog
        else if (game.isMenuShown()) {
            Utils.pressOrReleaseButton(screenCoord, game.getButtonYes());
            Utils.pressOrReleaseButton(screenCoord, game.getButtonNo());
            Utils.pressOrReleaseButton(screenCoord, game.getButtonRestart());
        }

        // get ready
        else {
            // close button in upper right corner of status bar
            if (Utils.within(screenCoord, hud.getButtonShowMenu())) {
                Utils.pressOrReleaseButton(screenCoord, hud.getButtonShowMenu());
            } else if (game.isGetReadyShown()) {
                game.play();
            }
        }
    }

}
