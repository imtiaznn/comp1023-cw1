package comp1023.loadeddice;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Player extends Entity {
    private int score;
    private int attack;
    private int defense;
    private int ammo;

    private int pendingDX, pendingDY;

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
        super.update(dungeon);
    
        // if we're not moving and no pending move, sample input once
        if (!isMoving() && pendingDX==0 && pendingDY==0) {
            if      (Gdx.input.isKeyPressed(Input.Keys.UP))    {pendingDX=0;   pendingDY=1;}
            else if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  {pendingDX=0;   pendingDY=-1;}
            else if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  {pendingDX=-1;  pendingDY=0;}
            else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {pendingDX=1;   pendingDY=0;}
        }
    
        // if we have a pending move and we’re free, execute it
        if (!isMoving() && (pendingDX!=0 || pendingDY!=0)) {
            move(pendingDX, pendingDY, dungeon);
            pendingDX = pendingDY = 0;
        }
    }


    public void reload(int ammo) {
        this.ammo += ammo;
    }

    public void pickUpItem() {

    }
}
