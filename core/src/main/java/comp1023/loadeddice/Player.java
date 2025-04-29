package comp1023.loadeddice;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player extends Entity {
    private int maxHealth;
    private int score;
    private int attack;
    private int ammo;

    private float attackCooldown = 0.5f;
    private int pendingDX, pendingDY;
    
    private float immunity = 0;
    private Array<Projectile> projectiles = new Array<>();

    public Player(float x, float y, int health, int speed, int score, int attack, int ammo) {
        super(x, y, health, speed, "player.png");
        this.score = score;
        this.attack = attack;
        this.ammo = ammo;
    }

    @Override
    public void update(Dungeon dungeon) {
        super.update(dungeon);

        if (attackCooldown > 0) { attackCooldown -= Gdx.graphics.getDeltaTime(); }
    
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

        if (attackCooldown <= 0 && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            fire();
            attackCooldown = 0.2f;
        }

        if (getHealth() <= 0) {
            Gdx.app.exit();
        }

        if (immunity > 0) {
            immunity = Math.max(0f, immunity - Gdx.graphics.getDeltaTime());
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        if (projectiles != null) {
            for (Projectile projectile : projectiles) {
                projectile.update();
                projectile.render(batch);
            }
        }
    }

    public void fire() {
        if (ammo > 0) {
            // Fire a projectile
            projectiles.add(new Projectile(x + height/4, y + width/4, direction.vector(), 1, 200));
            // ammo--;
        } else {
            System.out.println("Out of ammo!");
        }
    }

    public void reload(int ammo) {
        this.ammo += ammo;
    }

    // Getters and Setters
    public Array<Projectile> getProjectiles() { return projectiles; }

    public float getImmunity() {
        return immunity;
    }

    public void setImmunity(float immunity) {
        this.immunity = immunity;
    }
}