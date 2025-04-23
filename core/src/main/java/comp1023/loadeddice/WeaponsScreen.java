package comp1023.loadeddice;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class WeaponsScreen implements Screen {

    private final Game game;
    private Stage stage;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Texture backButtonTexture;
    private Texture weapon1Texture;
    private Texture weapon2Texture;
    private Texture weapon3Texture;

    // Fixed dimensions for the weapons screen
    private static final float FIXED_WIDTH = 1850;
    private static final float FIXED_HEIGHT = 1250;

    // Animation constants
    private static final float HOVER_SCALE = 1.1f;
    private static final float HOVER_DURATION = 0.2f;

    public WeaponsScreen(Game game) {
        this.game = game;

        // Create stage with fixed dimensions
        stage = new Stage(new FitViewport(FIXED_WIDTH, FIXED_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Create sprite batch for rendering
        batch = new SpriteBatch();

        // Load textures
        loadTextures();

        // Set up background
        setupBackground();

        // Create UI elements
        createBackButton();
        createWeaponButtons();
    }

    private void loadTextures() {
        backgroundTexture = new Texture(Gdx.files.internal("assets/ui/weapons_background.png"));
        backButtonTexture = new Texture(Gdx.files.internal("assets/ui/back_button.png"));
        weapon1Texture = new Texture(Gdx.files.internal("assets/ui/wea1.png"));
        weapon2Texture = new Texture(Gdx.files.internal("assets/ui/wea2.png"));
        weapon3Texture = new Texture(Gdx.files.internal("assets/ui/wea3.png"));
    }

    private void setupBackground() {
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        backgroundImage.setScaling(Scaling.fill);
        stage.addActor(backgroundImage);
    }

    private void createBackButton() {
        TextureRegion region = new TextureRegion(backButtonTexture);
        TextureRegionDrawable drawable = new TextureRegionDrawable(region);
        ImageButton backButton = new ImageButton(drawable);

        // Scale the button
        float scale = 0.4f;
        float width = backButtonTexture.getWidth() * scale;
        float height = backButtonTexture.getHeight() * scale;
        backButton.setSize(width, height);

        // Position in bottom left
        backButton.setPosition(10, 10);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        stage.addActor(backButton);
    }

    private void createWeaponButtons() {
        // Calculate positions for the three weapons
        float centerX = FIXED_WIDTH / 2;
        float centerY = FIXED_HEIGHT / 3;
        float weaponSpacing = 300;
        float weaponSize = 300;

        // Create weapon 1 button
        createWeaponButton(weapon1Texture, centerX - weaponSpacing, centerY, weaponSize, "Weapon 1");

        // Create weapon 2 button
        createWeaponButton(weapon2Texture, centerX, centerY, weaponSize, "Weapon 2");

        // Create weapon 3 button
        createWeaponButton(weapon3Texture, centerX + weaponSpacing, centerY, weaponSize, "Weapon 3");
    }

    private void createWeaponButton(Texture texture, float x, float y, float size, String name) {
        // Create the weapon image
        Image weaponImage = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
        weaponImage.setSize(size, size);
        weaponImage.setPosition(x - size / 2, y - size / 2);
        weaponImage.setOrigin(Align.center);

        // Add hover effect
        weaponImage.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                // Scale up and brighten
                weaponImage.addAction(Actions.sequence(
                    Actions.parallel(
                        Actions.scaleTo(HOVER_SCALE, HOVER_SCALE, HOVER_DURATION, Interpolation.pow2Out),
                        Actions.alpha(1.0f, HOVER_DURATION, Interpolation.pow2Out)
                    )
                ));

                // Log hover event
                Gdx.app.log("WeaponsScreen", "Hovering over " + name);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                // Scale down and dim
                weaponImage.addAction(Actions.sequence(
                    Actions.parallel(
                        Actions.scaleTo(1.0f, 1.0f, HOVER_DURATION, Interpolation.pow2Out),
                        Actions.alpha(0.8f, HOVER_DURATION, Interpolation.pow2Out)
                    )
                ));
            }
        });

        // Set initial alpha
        weaponImage.setColor(1, 1, 1, 0.8f);

        // Add to stage
        stage.addActor(weaponImage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update and draw stage
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
        batch.dispose();
        backgroundTexture.dispose();
        backButtonTexture.dispose();
        weapon1Texture.dispose();
        weapon2Texture.dispose();
        weapon3Texture.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
