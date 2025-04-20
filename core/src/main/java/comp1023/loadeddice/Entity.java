package comp1023.loadeddice;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Entity {
    protected float x, y;
    protected float width, height;
    protected float speed;
    protected int health;
    protected int maxHealth;
    
    public Entity(float x, float y, float speed, int health) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.health = health;
        this.maxHealth = health;
    }
    
    public abstract void update(float deltaTime);
    public abstract void render(SpriteBatch batch);
    
    public void takeDamage(int damage) {
        health = Math.max(0, health - damage);
    }
    
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public void move(float dx, float dy) {
        x += dx * speed;
        y += dy * speed;
    }
    
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
} 