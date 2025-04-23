package comp1023.loadeddice;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameOverScreen implements Screen {

    private final Game game;
    private Stage stage;
    private Texture gameOverImage;
    private Texture againButtonTexture;
    private Texture mainButtonTexture;

    // Fixed dimensions for the game over screen
    private static final float FIXED_WIDTH = 1850;
    private static final float FIXED_HEIGHT = 1250;

    public GameOverScreen(Game game) {
        this.game = game;

        // Create stage with fixed dimensions
        stage = new Stage(new FitViewport(FIXED_WIDTH, FIXED_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Load textures
        gameOverImage = new Texture(Gdx.files.internal("assets/ui/game_over_screen.jpg"));
        againButtonTexture = new Texture(Gdx.files.internal("assets/ui/Again_button.png"));
        mainButtonTexture = new Texture(Gdx.files.internal("assets/ui/Main_button.png"));

        // Set up background
        Image backgroundImage = new Image(gameOverImage);
        backgroundImage.setFillParent(true);
        backgroundImage.setScaling(Scaling.fill);
        stage.addActor(backgroundImage);

        // Create buttons
        ImageButton againButton = createButton(againButtonTexture, 0.3f);
        ImageButton mainButton = createButton(mainButtonTexture, 0.3f);

        // Position buttons at bottom corners
        float buttonY = 7; // Distance from bottom
        float buttonPadding = 3; // Padding from screen edges

        // Again button on left
        againButton.setPosition(buttonPadding, buttonY);

        // Main button on right
        mainButton.setPosition(FIXED_WIDTH - againButton.getWidth() - buttonPadding, buttonY);

        // Add click listeners
        mainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        againButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: Restart the game
                // For now, just go back to main menu
                game.setScreen(new MainMenuScreen(game));
            }
        });

        // Add buttons to stage
        stage.addActor(mainButton);
        stage.addActor(againButton);
    }

    private ImageButton createButton(Texture texture, float scale) {
        TextureRegion region = new TextureRegion(texture);
        TextureRegionDrawable drawable = new TextureRegionDrawable(region);
        ImageButton button = new ImageButton(drawable);

        // Scale the button
        float width = texture.getWidth() * scale;
        float height = texture.getHeight() * scale;
        button.setSize(width, height);

        return button;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        gameOverImage.dispose();
        againButtonTexture.dispose();
        mainButtonTexture.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
