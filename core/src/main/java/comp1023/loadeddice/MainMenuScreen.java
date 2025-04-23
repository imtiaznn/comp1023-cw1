package comp1023.loadeddice;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuScreen implements Screen {
    private final Game game;
    private Stage stage;
    private Texture background;
    private Texture playButtonTexture;
    private Texture loadButtonTexture;
    private Texture leaderboardButtonTexture;
    private Texture exitButtonTexture;

    public MainMenuScreen(Game game) {
        this.game = game;

        // Create viewport with virtual dimensions that match the background's aspect ratio
        stage = new Stage(new FitViewport(1850, 1250));
        Gdx.input.setInputProcessor(stage);

        // Load textures
        try {
            background = new Texture(Gdx.files.internal("assets/ui/dungeon_background.png"));
            playButtonTexture = new Texture(Gdx.files.internal("assets/ui/play_button.png"));
            loadButtonTexture = new Texture(Gdx.files.internal("assets/ui/load_button.png"));
            leaderboardButtonTexture = new Texture(Gdx.files.internal("assets/ui/leader_button.png"));
            exitButtonTexture = new Texture(Gdx.files.internal("assets/ui/ExitButton.png"));
        } catch (Exception e) {
            Gdx.app.error("MainMenuScreen", "Error loading textures: " + e.getMessage());
            // Create a simple white texture as fallback
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            background = new Texture(pixmap);
            playButtonTexture = new Texture(pixmap);
            loadButtonTexture = new Texture(pixmap);
            leaderboardButtonTexture = new Texture(pixmap);
            exitButtonTexture = new Texture(pixmap);
            pixmap.dispose();
        }

        createUI();
    }

    private void createUI() {
        Image backgroundImage = new Image(background);
        backgroundImage.setFillParent(true);
        backgroundImage.setScaling(Scaling.stretch);
        stage.addActor(backgroundImage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Create buttons using the custom textures with scaling
        ImageButton playButton = createImageButton(playButtonTexture, 0.1f);
        ImageButton loadButton = createImageButton(loadButtonTexture, 0.1f);
        ImageButton leaderboardButton = createImageButton(leaderboardButtonTexture, 0.1f);
        ImageButton exitButton = createImageButton(exitButtonTexture, 0.1f);

        // Add button listeners
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new Main(game));
            }
        });

        loadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: Implement save/load system
                game.setScreen(new Main(game));
            }
        });

        leaderboardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LeaderboardScreen(game));
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Center the table in the stage
        table.center();

        // Add top padding to move buttons down
        table.padTop(450f);

        // Add buttons to table with spacing
        float padding = 10f;
        table.add(playButton).pad(padding).row();
        table.add(loadButton).pad(padding).row();
        table.add(leaderboardButton).pad(padding).row();
        table.add(exitButton).pad(padding);
    }

    private ImageButton createImageButton(Texture texture, float scale) {
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

        // Draw UI
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
        background.dispose();
        playButtonTexture.dispose();
        loadButtonTexture.dispose();
        leaderboardButtonTexture.dispose();
        exitButtonTexture.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
