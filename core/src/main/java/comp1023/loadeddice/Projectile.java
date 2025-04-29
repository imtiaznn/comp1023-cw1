package comp1023.loadeddice;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;

public class Projectile {
    private boolean active = true;
    private float x, y;
    private Vector2 direction;
    private int projectileType;
    private int damage = 30;
    private int speed;
    private Rectangle boundingBox;
    private Texture texture;

    // Constructor 
    public Projectile(float x, float y, Vector2 direction, int projectileType, int speed) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.projectileType = projectileType;
        this.speed = speed;
        this.boundingBox = new Rectangle(x, y, 8, 8); // Assuming a small size for the projectile
        this.texture = new Texture("projectile.png"); // Load your projectile texture here
    }

    // Update the projectile's position
    public void update() {
        if (!active) return;
        // Move the projectile in the direction it was fired
        x += direction.x * speed * com.badlogic.gdx.Gdx.graphics.getDeltaTime();
        y += direction.y * speed * com.badlogic.gdx.Gdx.graphics.getDeltaTime();
        
        this.boundingBox.setPosition(x, y);
    }

    public void render(SpriteBatch batch) {
        if (!active) return;
        // float rotation = (float) Math.toDegrees(Math.atan2(direction.y, direction.x));
        batch.draw(texture,
                  x, y,
                  boundingBox.width/2, boundingBox.height/2,
                  boundingBox.width, boundingBox.height,
                  1, 1,
                  0,
                  0, 0,
                  texture.getWidth(), texture.getHeight(),
                  false, false);
    }

    public boolean isActive() { return active; }

    // Getters
    public float getX() { return x; }
    public float getY() { return y; }
    public int getDamage() { return damage; }
    public Rectangle getBoundingBox() { return boundingBox; }

    // Setters
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setActive(boolean active) { this.active = active; }
}