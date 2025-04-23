package comp1023.loadeddice;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class LeaderboardScreen implements Screen {
    private final Game game;
    private Stage stage;
    private Texture background;
    private Texture backButtonTexture;
    private Table leaderboardTable;
    private BitmapFont font;

    public LeaderboardScreen(Game game) {
        this.game = game;

        // Create viewport with virtual dimensions
        stage = new Stage(new FitViewport(1850, 1250));
        Gdx.input.setInputProcessor(stage);

        // Load textures
        background = new Texture(Gdx.files.internal("assets/ui/Leaderboard_background.png"));
        backButtonTexture = new Texture(Gdx.files.internal("assets/ui/back_button.png"));

        // Create font
        font = new BitmapFont();
        font.getData().setScale(2f); // Scale the font to make it more visible

        createUI();
    }

    private void createUI() {
        // Add background
        Image backgroundImage = new Image(background);
        backgroundImage.setFillParent(true);
        backgroundImage.setScaling(Scaling.stretch);
        stage.addActor(backgroundImage);

        // Create main table
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // Create leaderboard table
        leaderboardTable = new Table();
        leaderboardTable.setFillParent(true);
        mainTable.add(leaderboardTable).expand().fill();

        // Add back button
        ImageButton backButton = createImageButton(backButtonTexture, 0.1f);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        mainTable.row();
        mainTable.add(backButton).pad(20f);

        // Add leaderboard entries
        addLeaderboardEntries();
    }

    private void addLeaderboardEntries() {
        // Create label style
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.WHITE);

        // Example leaderboard entries
        String[] names = {"Player1", "Player2", "Player3", "Player4", "Player5"};
        int[] scores = {1000, 800, 600, 400, 200};

        // Add header
        leaderboardTable.add(new Label("Rank", labelStyle)).pad(10f);
        leaderboardTable.add(new Label("Name", labelStyle)).pad(10f);
        leaderboardTable.add(new Label("Score", labelStyle)).pad(10f);
        leaderboardTable.row();

        // Add entries
        for (int i = 0; i < names.length; i++) {
            leaderboardTable.add(new Label(String.valueOf(i + 1), labelStyle)).pad(10f);
            leaderboardTable.add(new Label(names[i], labelStyle)).pad(10f);
            leaderboardTable.add(new Label(String.valueOf(scores[i]), labelStyle)).pad(10f);
            leaderboardTable.row();
        }
    }

    private ImageButton createImageButton(Texture texture, float scale) {
        TextureRegion region = new TextureRegion(texture);
        TextureRegionDrawable drawable = new TextureRegionDrawable(region);
        ImageButton button = new ImageButton(drawable);

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
        background.dispose();
        backButtonTexture.dispose();
        font.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
