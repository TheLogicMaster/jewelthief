package at.therefactory.jewelthief.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import at.therefactory.jewelthief.Game;
import at.therefactory.jewelthief.JewelThief;
import at.therefactory.jewelthief.misc.Util;
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
    private boolean playerDragging = false;
    private float deltaX;
    private float deltaY;
    private int numTouches = 0;
    private boolean allowButtonClick; // prevent button click when dialog appears while still dragging player around

    public GameScreenInputAdapter(Game game, Viewport viewport, Hud hud) {
        this.game = game;
        this.viewport = viewport;
        this.hud = hud;
        allowButtonClick = false;
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.BACK) {
            if (game.isMenuShown()) {
                JewelThief.getInstance().switchToMainMenu();
                return true;
            } else {
                game.showMenu();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
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
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        numTouches++;
        if (!(JewelThief.getInstance().getScreen() instanceof GameScreen)) {
            return true;
        }
        pressOrReleaseButtonAt(viewport.unproject(new Vector3(screenX, screenY, 0)));
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        numTouches = Math.max(0, numTouches - 1);
        playerDragging = false;
        releaseAllButtons();

        // check if button has been clicked
        Vector3 touchCoordinates = viewport.unproject(new Vector3(screenX, screenY, 0));
        if (!game.isMenuShown() && game.getPlayer().getNumMen() > 0
                && Util.within(touchCoordinates, hud.getShowMenuButton())) {
            game.showMenu();
        } else if (game.getPlayer().getNumMen() <= 0) {
            if (allowButtonClick) {
                allowButtonClick = false;

                // play again? yes
                if (Util.within(touchCoordinates, game.getGameOverPlayAgainBtn())) {
                    game.resetGame();
                }
                // play again? no
                else if (Util.within(touchCoordinates, game.getGameOverExitBtn())) {
                    JewelThief.getInstance().switchToMainMenu();
                }
            } else {
                allowButtonClick = true;
            }
        } else if (game.isMenuShown()) {
            // yes
            if (Util.within(touchCoordinates, game.getMenuYesBtn())) {
                JewelThief.getInstance().switchToMainMenu();
            }
            // no
            else if (Util.within(touchCoordinates, game.getMenuNoBtn())) {
                game.hideMenu();
            }
            // restart
            else if (Util.within(touchCoordinates, game.getMenuRestartBtn())) {
                game.resetGame();
            }
        }
        return true;
    }

    private void releaseAllButtons() {
        hud.getShowMenuButton().release();
        game.getMenuYesBtn().release();
        game.getMenuNoBtn().release();
        game.getMenuRestartBtn().release();
        game.getGameOverPlayAgainBtn().release();
        game.getGameOverExitBtn().release();
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 touchCoordinates = viewport.unproject(new Vector3(screenX, screenY, 0));
        if (game.isPaused()) {
            pressOrReleaseButtonAt(touchCoordinates);
        } else {
            if (playerDragging && numTouches == 1) {

                // do not exceed right border
                float newX = Math.min(touchCoordinates.x - deltaX, game.getBackground().getX() + game.getBackground().getWidth()
                        - game.getPlayer().getSprite().getWidth() / 2 - 1);

                // do not exceed left border
                newX = Math.max(game.getBackground().getX() + game.getPlayer().getSprite().getWidth() / 2, newX);

                // do not exceed lower border
                float newY = Math.max(touchCoordinates.y - deltaY, game.getBackground().getY() + game.getPlayer().getSprite().getHeight() / 2);

                // do not exceed upper border
                newY = Math.min(game.getBackground().getY() + game.getBackground().getHeight() - game.getPlayer().getSprite().getHeight() / 2 - 1, newY);

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
            Util.pressOrReleaseButton(screenCoord, game.getGameOverPlayAgainBtn());
            Util.pressOrReleaseButton(screenCoord, game.getGameOverExitBtn());
        }

        // "givin' up already" dialog
        else if (game.isMenuShown()) {
            Util.pressOrReleaseButton(screenCoord, game.getMenuYesBtn());
            Util.pressOrReleaseButton(screenCoord, game.getMenuNoBtn());
            Util.pressOrReleaseButton(screenCoord, game.getMenuRestartBtn());
        }

        // get ready
        else {
            // close button in upper right corner of status bar
            if (Util.within(screenCoord, hud.getShowMenuButton())) {
                Util.pressOrReleaseButton(screenCoord, hud.getShowMenuButton());
            } else if (game.isGetReadyShown()) {
                game.play();
            }
        }
    }

}
