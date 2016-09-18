package at.therefactory.jewelthief.screens;

import at.therefactory.jewelthief.JewelThief;
import at.therefactory.jewelthief.constants.Config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class LogoScreen extends ScreenAdapter {

    private final FitViewport viewport;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final ShapeRenderer sr;
    private final Music libGdxSound;
    private Sprite there, factory, libGdxLogo;
    private int numFrames, deltaXThere, deltaXCursor, deltaXFactory = 64;
    private boolean showCursor = true;
    private float alpha;

    // flags for states in the animation
    private boolean writeThefactory = true;
    private boolean writeRe = false;
    private Sound thefactorySound;
    private Sound goBackSound;
    private Sound reSound;
    private long timestamp;
    private boolean libGdxLogoFinished;

    public LogoScreen(SpriteBatch batch, ShapeRenderer sr) {
        this.batch = batch;
        this.sr = sr;

        camera = new OrthographicCamera();
        viewport = new FitViewport(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT, camera);
        camera.position.set(Config.WINDOW_WIDTH / 2, Config.WINDOW_HEIGHT / 2, 0);
        camera.update();

        there = new Sprite(new Texture("there.png"));
        factory = new Sprite(new Texture("factory.png"));
        libGdxLogo = new Sprite(new Texture("libgdx.png"));

        AssetManager am = JewelThief.getInstance().getAssetManager();
        am.load("audio/sounds/keyboard.ogg", Sound.class);
        am.load("audio/sounds/keyboard_go_back.ogg", Sound.class);
        am.load("audio/sounds/re.ogg", Sound.class);
        am.load("audio/sounds/libgdx.ogg", Music.class);
        am.finishLoading();

        thefactorySound = am.get("audio/sounds/keyboard.ogg", Sound.class);
        goBackSound = am.get("audio/sounds/keyboard_go_back.ogg", Sound.class);
        reSound = am.get("audio/sounds/re.ogg", Sound.class);

        libGdxSound =  am.get("audio/sounds/libgdx.ogg", Music.class);

        resetAnimation();
    }

    @Override
    public void render(float delta) {
        if (!libGdxLogoFinished) {
            Gdx.gl.glClearColor(1, 1, 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            updateAndRenderLibGdxLogo(delta);
        } else {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            updateAndRenderTheRefactoryLogo(delta);
        }
    }

    private void updateAndRenderLibGdxLogo(float delta) {
        updateLibGdxLogo(delta);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        //JewelThief.getInstance().getFont().setColor(Color.BLACK);
        //JewelThief.getInstance().getFont().draw(batch, "built with", 225, 180);
        batch.draw(libGdxLogo, Config.WINDOW_WIDTH/2 - libGdxLogo.getWidth()/4,
                Config.WINDOW_HEIGHT/2 - libGdxLogo.getHeight()/4,
                libGdxLogo.getWidth()/2,
                libGdxLogo.getHeight()/2);

        JewelThief.getInstance().getFadeSprite().setAlpha(alpha);
        JewelThief.getInstance().getFadeSprite().draw(batch);

        batch.end();
    }

    private void updateLibGdxLogo(float delta) {

        // go to next screen as soon as user taps the screen
        if (Gdx.input.justTouched()) {
            libGdxSound.stop();
            alpha = 1;
            libGdxLogoFinished = true;
            return;
        }

        // fade in
        if (timestamp == 0 && alpha > 0) {
            alpha = Math.max(0, alpha - Config.FADING_SPEED/4);
        } else {

            // wait a bit
            if (timestamp == 0)
                timestamp = System.currentTimeMillis();
            if (System.currentTimeMillis() - 1200 > timestamp) {
                
                // fade out
                alpha = Math.min(1, alpha + Config.FADING_SPEED/4);

                if (alpha == 1) {
                    libGdxLogoFinished = true;
                }
            }
        }
    }

    private void updateAndRenderTheRefactoryLogo(float delta) {
        updateTheRefactory(delta);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        there.setX((Config.WINDOW_WIDTH - there.getWidth() - factory.getWidth()) / 2);
        there.setY(Config.WINDOW_HEIGHT / 2 - there.getHeight() / 2);
        there.draw(batch);

        batch.end();

        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeType.Filled);
        sr.setColor(Color.BLACK);

        // black background of "factory" for overlaying the "there" sprite
        sr.rect(factory.getX(), 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        sr.end();

        batch.begin();
        factory.setX(there.getX() + there.getWidth() + 1 - deltaXFactory);
        factory.setY(there.getY());
        factory.draw(batch);
        
        JewelThief.getInstance().getFadeSprite().setAlpha(alpha);
        JewelThief.getInstance().getFadeSprite().draw(batch);
        batch.end();

        sr.begin(ShapeType.Filled);

        // hide letters
        sr.rect(there.getX() + deltaXThere, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        // cursor
        if (showCursor) {
            sr.setColor(Color.WHITE);
            sr.rect(there.getX() + deltaXCursor, Config.WINDOW_HEIGHT / 2 - 50 / 2, 4, 50);
        }
        sr.end();
    }

    private void updateTheRefactory(float delta) {

        // go to main menu as soon as user taps the screen
        if (Gdx.input.justTouched()) {
            JewelThief.getInstance().switchToMainMenu();
        }

        // show logo animation
        numFrames++;
        int typeSpeedForOneLetter = 8;
        if (numFrames < 0) {
            blinkCursor();
        } else {
            if (writeThefactory) {
                if (numFrames == 1) {
                    alpha = 0;
                    showCursor = true;
                    thefactorySound.play();
                }
                if (numFrames == 9 * typeSpeedForOneLetter) {
                    deltaXThere += 41;
                } else if (numFrames % typeSpeedForOneLetter == 0 && numFrames < 9 * typeSpeedForOneLetter) {
                    deltaXThere += 33;
                }
                deltaXCursor = deltaXThere;
                if (numFrames > 10 * typeSpeedForOneLetter) {
                    blinkCursor();
                    if (numFrames > 17 * typeSpeedForOneLetter) {
                        writeThefactory = false;
                        writeRe = true;
                        deltaXCursor -= 41;
                        goBackSound.play();
                        showCursor = true;
                    }
                }
            } else if (writeRe) {
                if (numFrames % typeSpeedForOneLetter == 0 && numFrames < 24 * typeSpeedForOneLetter) {
                    deltaXCursor -= 33;
                } else if (numFrames >= 24 * typeSpeedForOneLetter && numFrames < 26 * typeSpeedForOneLetter) {
                    blinkCursor();
                }

                // insert letter "R"
                else if (numFrames == 26 * typeSpeedForOneLetter) {
                    reSound.play();
                    showCursor = true;
                    deltaXFactory -= 33;
                    deltaXCursor += 33;
                    deltaXThere += 33;
                }

                // insert letter "E"
                else if (numFrames == 30 * typeSpeedForOneLetter) {
                    deltaXFactory -= 33;
                    deltaXCursor += 33;
                    deltaXThere += 33;
                } else if (numFrames > 32 * typeSpeedForOneLetter && numFrames <= 37 * typeSpeedForOneLetter) {
                    //blinkCursor();
                    showCursor = false;
                    
                }

                // fade out
                else if (numFrames > 37 * typeSpeedForOneLetter) {
                    showCursor = false;
                    alpha = Math.min(1, alpha + Config.FADING_SPEED/4);
                }

                if (alpha == 1) {
                    if (Config.DEBUG_MODE) {
                        resetAnimation();
                    } else {
                        JewelThief.getInstance().switchToMainMenu();
                    }
                }
            }
        }
    }

    private void resetAnimation() {
        timestamp = 0;
        libGdxLogoFinished = false;
        alpha = 1;
        numFrames = -40;
        deltaXThere = 0;
        deltaXCursor = 0;
        deltaXFactory = 64;
        showCursor = true;
        writeThefactory = true;
        writeRe = false;
        libGdxSound.play();
    }

    private void blinkCursor() {
        if ((numFrames % 20 == 0)) {
            showCursor = !showCursor;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        goBackSound.dispose();
        thefactorySound.dispose();
        reSound.dispose();
        libGdxSound.dispose();
    }

    @Override
    public void hide() {
        super.hide();
        goBackSound.stop();
        thefactorySound.stop();
        reSound.stop();
        libGdxSound.stop();
    }

}
