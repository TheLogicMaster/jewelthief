package net.bplaced.therefactory.jewelthief.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.bplaced.therefactory.jewelthief.Game;
import net.bplaced.therefactory.jewelthief.JewelThief;
import net.bplaced.therefactory.jewelthief.constants.Config;
import net.bplaced.therefactory.jewelthief.misc.Util;
import net.bplaced.therefactory.jewelthief.screens.GameScreen;
import net.bplaced.therefactory.jewelthief.ui.Hud;

/**
 * The input handler for the GameScreen.
 */
public class GameInputHandler implements InputProcessor {

    private final Hud hud;
    private final Game game;
    private final Viewport viewport;
    private boolean playerDragging = false;
    private float deltaX;
    private float deltaY;
    private int numTouches = 0;
    private boolean allowButtonClick; // prevent button click when dialog appears while still dragging player around

    public GameInputHandler(Game game, Viewport viewport, Hud hud) {
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
            } else {
                game.showMenu();
            }
            return true;
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
        if (Config.DEBUG_MODE) {
            if (character == '1') {
                game.switchDebug();
                return true;
            }
            if (character == '2') {
                game.rearrangeEnemies();
            }
            if (character == '3') {
                game.collectAllJewels();
            }
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        numTouches++;
        if (!(JewelThief.getInstance().getScreen() instanceof GameScreen)) {
            return false;
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
        Vector3 screenCoord = viewport.unproject(new Vector3(screenX, screenY, 0));
        if (!game.isMenuShown() && game.getPlayer().getNumMen() > 0
                && Util.within(screenCoord, hud.getShowMenuButton())) {
            game.showMenu();
        } else if (game.getPlayer().getNumMen() <= 0) {
            if (allowButtonClick) {
                allowButtonClick = false;

                // play again? yes
                if (Util.within(screenCoord, game.getGameOverPlayAgainBtn())) {
                    game.resetGame();
                }
                // play again? no
                else if (Util.within(screenCoord, game.getGameOverExitBtn())) {
                    JewelThief.getInstance().switchToMainMenu();
                }
            } else {
                allowButtonClick = true;
            }
        } else if (game.isMenuShown()) {
            // yes
            if (Util.within(screenCoord, game.getMenuYesBtn())) {
                JewelThief.getInstance().switchToMainMenu();
            }
            // no
            else if (Util.within(screenCoord, game.getMenuNoBtn())) {
                game.hideMenu();
            }
            // restart
            else if (Util.within(screenCoord, game.getMenuRestartBtn())) {
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
        Vector3 unprojectedCoordinates = viewport.unproject(new Vector3(screenX, screenY, 0));
        if (game.isPaused()) {
            pressOrReleaseButtonAt(unprojectedCoordinates);
        } else {
            if (playerDragging && numTouches == 1) {

                // do not exceed right border
                float newX = Math.min(unprojectedCoordinates.x - deltaX, game.getBackground().getX() + game.getBackground().getWidth()
                        - game.getPlayer().getSprite().getWidth() / 2 - 1);

                // do not exceed left border
                newX = Math.max(game.getBackground().getX() + game.getPlayer().getSprite().getWidth() / 2, newX);

                // do not exceed lower border
                float newY = Math.max(unprojectedCoordinates.y - deltaY, game.getBackground().getY() + game.getPlayer().getSprite().getHeight() / 2);

                // do not exceed upper border
                newY = Math.min(game.getBackground().getY() + game.getBackground().getHeight() - game.getPlayer().getSprite().getHeight() / 2 - 1, newY);

                game.getPlayer().setPosition(newX, newY);
            } else {
                playerDragging = true;
            }
            // delta between touch point and player position
            deltaX = (unprojectedCoordinates.x - game.getPlayer().getPosition().x);
            deltaY = (unprojectedCoordinates.y - game.getPlayer().getPosition().y);
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        // TODO Auto-generated method stub
        return false;
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
