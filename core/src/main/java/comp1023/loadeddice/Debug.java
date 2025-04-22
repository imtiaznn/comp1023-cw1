package comp1023.loadeddice;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Input;

public class Debug {
    private final Rectangle buttonBounds = new Rectangle(10, 10, 140, 30);
    private final BitmapFont font = new BitmapFont();

    public boolean renderButton(SpriteBatch batch) {
        font.setColor(Color.WHITE);
        batch.setColor(0.2f, 0.2f, 0.2f, 1f);
        batch.draw(
            new com.badlogic.gdx.graphics.Texture("white.png"),
            buttonBounds.x, buttonBounds.y,
            buttonBounds.width, buttonBounds.height
        );
        font.draw(batch, "Regenerate", buttonBounds.x + 10, buttonBounds.y + 20);
        batch.setColor(1f, 1f, 1f, 1f); // Reset to normal
        return Gdx.input.justTouched() && buttonBounds.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
    }
}