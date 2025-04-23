package comp1023.loadeddice;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainGame extends Game {
    private SpriteBatch batch;
    private static MainGame instance;

    public MainGame() {
        instance = this;
    }

    public static MainGame getInstance() {
        return instance;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        super.dispose();
    }
}
