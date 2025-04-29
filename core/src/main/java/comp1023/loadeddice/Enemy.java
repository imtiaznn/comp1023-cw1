package comp1023.loadeddice;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Enemy extends Entity {
    private int damage = 20;

    public Enemy(float x, float y, int health, int speed) {
        super(x, y, health, speed, "enemy.png");
    }

    @Override
    public void update(Dungeon dungeon) {
        super.update(dungeon);

        // Random movement logic
        if (!isMoving) {
            int dir = MathUtils.random(3);
            switch (dir) {
                case 0:
                    move(1, 0, dungeon);
                    break;
                case 1:
                    move(-1, 0, dungeon);
                    break;
                case 2:
                    move(0, 1, dungeon);
                    break;
                case 3:
                    move(0, -1, dungeon);
                    break;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
    }

    // Getters
    public int getDamage() {return damage;}

    // Setters
    public void setDamage(int damage) {this.damage = damage;}
}
