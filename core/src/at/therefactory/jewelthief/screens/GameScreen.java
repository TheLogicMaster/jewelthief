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

public class GameScreen extends ScreenAdapter {

    private final FitViewport viewport;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final Hud hud;
    private final Game game;

    public GameScreen(SpriteBatch batch, ShapeRenderer shapeRenderer, FitViewport viewport, OrthographicCamera camera) {
        this.batch = batch;
        this.shapeRenderer = shapeRenderer;
        this.viewport = viewport;
        this.camera = camera;
        Player player = new Player();
        game = new Game(player);
        hud = new Hud(game);
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

        // render shapes
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        hud.render(shapeRenderer);
        game.render(shapeRenderer);
        shapeRenderer.end();

        // render sprites/fonts
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        hud.render(batch);
        game.render(batch, delta);
        batch.end();

        // render shapes for dialogs
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        hud.postRender(shapeRenderer);
        game.postRender(shapeRenderer);
        shapeRenderer.end();

        // render sprites/fonts for dialogs
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        hud.postRender(batch);
        game.postRender(batch, delta);
        batch.end();

//        if (DEBUG_MODE) {
//            shapeRenderer.setProjectionMatrix(camera.combined);
//            game.debug(shapeRenderer);
//            batch.setProjectionMatrix(camera.combined);
//            game.debug(batch);
//        }
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
