package at.therefactory.jewelthief.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;

import at.therefactory.jewelthief.Game;
import at.therefactory.jewelthief.actors.Player;
import at.therefactory.jewelthief.input.GameScreenInputAdapter;
import at.therefactory.jewelthief.ui.Hud;

import static at.therefactory.jewelthief.constants.Config.DEBUG_MODE;
import static at.therefactory.jewelthief.constants.Config.WINDOW_HEIGHT;
import static at.therefactory.jewelthief.constants.Config.WINDOW_WIDTH;

public class GameScreen extends ScreenAdapter {

    private final FitViewport viewport;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final ShapeRenderer sr;
    private final Hud hud;
    private final Game game;

    public GameScreen(SpriteBatch batch, ShapeRenderer sr) {
        this.batch = batch;
        this.sr = sr;
        Player player = new Player();
        game = new Game(player);
        hud = new Hud(game);

        camera = new OrthographicCamera();
        viewport = new FitViewport(WINDOW_WIDTH, WINDOW_HEIGHT, camera);
        camera.position.set(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2, 0);
        camera.update();
    }

    @Override
    public void show() {
        super.show();
        hud.show();
        game.show();
        Gdx.input.setInputProcessor(new GameScreenInputAdapter(game, viewport, hud));
    }

    @Override
    public void hide() {
        super.hide();
        game.pause();
        game.showGetReady();
    }

    @Override
    public void pause() {
        super.pause();
        game.pause();
        game.showGetReady();
    }

    @Override
    public void render(float delta) {

        // clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // simulate world
        update(delta);

        // renderCaption shapes
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        hud.render(sr);
        game.render(sr);
        sr.end();

        // renderCaption sprites/fonts
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        hud.render(batch);
        game.render(batch, delta);
        batch.end();

        // renderCaption shapes for dialogs
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        hud.postRender(sr);
        game.postRender(sr);
        sr.end();

        // renderCaption sprites/fonts for dialogs
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        hud.postRender(batch);
        game.postRender(batch, delta);
        batch.end();

        if (DEBUG_MODE) {
            sr.setProjectionMatrix(camera.combined);
            game.debug(sr);
            batch.setProjectionMatrix(camera.combined);
            game.debug(batch);
        }
    }

    private void update(float delta) {
        game.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public void resetGame() {
        game.resetGame();
    }

    @Override
    public void dispose() {
        super.dispose();
        game.dispose();
    }

}
