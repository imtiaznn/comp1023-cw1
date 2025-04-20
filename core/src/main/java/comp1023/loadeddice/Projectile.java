package comp1023.loadeddice;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Projectile {
    private final Vector2 position;
    private final Vector2 direction;
    private final float speed;
    private final float damage;
    private final int diceEffect;
    private final Rectangle bounds;
    private final Texture texture;
    private boolean active;
    
    public Projectile(float x, float y, Vector2 direction, float speed, float damage, int diceEffect) {
        this.position = new Vector2(x, y);
        this.direction = direction.nor();
        this.speed = speed;
        this.damage = damage;
        this.diceEffect = diceEffect;
        this.bounds = new Rectangle(x, y, 8, 8);
        this.active = true;
        this.texture = new Texture("ricochet_bullet.png");
    }
    
    public void update(float deltaTime) {
        if (!active) return;
        
        position.x += direction.x * speed * deltaTime;
        position.y += direction.y * speed * deltaTime;
        
        bounds.setPosition(position.x, position.y);
    }
    
    public void render(SpriteBatch batch) {
        if (!active) return;
        float rotation = (float) Math.toDegrees(Math.atan2(direction.y, direction.x));
        batch.draw(texture, 
                  position.x, position.y, 
                  bounds.width/2, bounds.height/2,
                  bounds.width, bounds.height,
                  1, 1,
                  rotation,
                  0, 0,
                  texture.getWidth(), texture.getHeight(),
                  false, false);
    }
    
    public Rectangle getBounds() {
        return bounds;
    }
    
    public float getDamage() {
        return damage;
    }
    
    public int getDiceEffect() {
        return diceEffect;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public void dispose() {
        texture.dispose();
    }
} 