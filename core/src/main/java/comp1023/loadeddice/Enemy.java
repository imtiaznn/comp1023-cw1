package comp1023.loadeddice;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class Enemy extends Entity {
    public Enemy(float x, float y, int health, int speed) {
        super(x, y, health, speed, "enemy.png");
    }

    @Override
    public void update(Dungeon dungeon) {
        super.update(dungeon);

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
}
