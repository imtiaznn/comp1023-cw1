package comp1023.loadeddice;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Player extends Entity {
    private int score;
    private int attack;
    private int defense;
    private int ammo;

    private Item[] inventory = new Item[5];

    public Player(float x, float y, int health, int speed, int maxHealth, int score, int attack, int defense, int ammo) {
        super(x, y, health, speed, "player.png");
        this.score = score;
        this.attack = attack;
        this.defense = defense;
        this.ammo = ammo;
    }

    @Override
    public void update(Dungeon dungeon) {
        // Only process ONE movement direction per frame
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.UP)) {
            move(0, 1, dungeon);
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.DOWN)) {
            move(0, -1, dungeon);
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
            move(-1, 0, dungeon);
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
            move(1, 0, dungeon);
        }

        // Call parent update method to handle smooth movement
        super.update(dungeon);
    }

    public void reload(int ammo) {
        this.ammo += ammo;
    }

    public void pickUpItem() {

    }
}
