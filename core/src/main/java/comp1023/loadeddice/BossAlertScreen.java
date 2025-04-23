package comp1023.loadeddice;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class BossAlertScreen implements Screen {

    private final Game game;
    private final Stage stage;
    private final Image alertImage;
    private final Texture alertTexture;
    private static final float FIXED_WIDTH = 800;  // Fixed width for alert
    private static final float FIXED_HEIGHT = 400; // Fixed height for alert

    private float shakeTime = 2f;
    private float shakeMagnitude = 5f;
    private float shakeTimer = 0;
    private boolean isShaking = true;
    private boolean transitionStarted = false;
    private float originalX, originalY;

    public BossAlertScreen(Game game) {
        this.game = game;

        stage = new Stage(new FitViewport(1850, 1250));
        Gdx.input.setInputProcessor(stage);

        alertTexture = new Texture(Gdx.files.internal("assets/ui/bossAlertScreen.jpg"));
        alertImage = new Image(new TextureRegion(alertTexture));
        
        // Set fixed size for alert
        alertImage.setSize(FIXED_WIDTH, FIXED_HEIGHT);
        centerAlert();
        stage.addActor(alertImage);

        alertImage.getColor().a = 0f;
        alertImage.addAction(sequence(
            Actions.alpha(1f, 0.3f),
            Actions.parallel(
                Actions.forever(sequence(
                    Actions.scaleTo(1.05f, 1.05f, 0.3f),
                    Actions.scaleTo(1f, 1f, 0.3f)
                )),
                Actions.run(() -> isShaking = true)
            )
        ));

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                alertImage.addAction(Actions.sequence(
                    Actions.fadeOut(0.5f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            transitionStarted = true;
                        }
                    })
                ));
            }
        }, 3f);
    }

    private void centerAlert() {
        float x = (stage.getViewport().getWorldWidth() - FIXED_WIDTH) / 2f;
        float y = (stage.getViewport().getWorldHeight() - FIXED_HEIGHT) / 2f;
        alertImage.setPosition(x, y);
        originalX = x;
        originalY = y;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0.8f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (isShaking) {
            shakeTimer += delta;
            if (shakeTimer <= shakeTime) {
                float shakeX = originalX + MathUtils.random(-shakeMagnitude, shakeMagnitude);
                float shakeY = originalY + MathUtils.random(-shakeMagnitude, shakeMagnitude);
                alertImage.setPosition(shakeX, shakeY);
                shakeMagnitude = 5f * (1 - (shakeTimer / shakeTime));
            } else {
                isShaking = false;
                alertImage.setPosition(originalX, originalY);
            }
        }

        stage.act(delta);
        stage.draw();

        if (transitionStarted && alertImage.getColor().a == 0) {
            game.setScreen(new Main(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        centerAlert(); // Only recenter, don't resize
    }

    @Override
    public void dispose() {
        stage.dispose();
        alertTexture.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
