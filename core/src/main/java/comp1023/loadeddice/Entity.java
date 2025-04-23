package comp1023.loadeddice;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
    // Texture variables
    private TextureRegion spriteRegion;

    protected TextureRegion spriteLeft;
    protected TextureRegion spriteRight;

    // Position variables
    protected float x, y;
    protected int gridX, gridY;
    protected int targetX, targetY;

    protected int currentDirection = 1; // 1 for right, -1 for left
    protected boolean isMoving = false;

    // Properties variables
    protected int width = 16;
    protected int height = 16; 

    protected int health;
    protected int maxHealth;
    protected int speed;

    protected Rectangle boundingBox;

    public Entity(float x, float y, int health, int speed, String spritePath) {
        Texture spriteTex = new Texture(Gdx.files.internal(spritePath));
        
        this.gridX = (int)Math.floor(x / 16f);
        this.gridY = (int)Math.floor(y / 16f);

        this.x = x;
        this.y = y;
        this.health = health;
        this.maxHealth = health;
        this.speed = speed;

        TextureRegion[][] spriteTex_split = TextureRegion.split(spriteTex, spriteTex.getWidth() / 2,  spriteTex.getHeight());

        this.spriteLeft = spriteTex_split[0][0];
        this.spriteRight = spriteTex_split[0][1];

        this.boundingBox = new Rectangle(x, y, width, height);
    }

    public void updateBoundingBox(float x, float y) {
        this.boundingBox.setPosition(x, y);
    }

    public void move(int dirX, int dirY, Dungeon dungeon) {
        // If already moving, return
        if (isMoving) return;

        // Next grid postion
        int nextGX = gridX + dirX;
        int nextGY = gridY + dirY;

        // build a test rectangle at next tile
        Rectangle test = new Rectangle(
            nextGX * 16,
            nextGY * 16,
            boundingBox.width,
            boundingBox.height
        );

        // if that test box overlaps any wall, return
        for (Rectangle wall : dungeon.getCollisionBoxes()) {
            if (test.overlaps(wall)) return;
        }

        gridX = nextGX;
        gridY = nextGY;

        targetX = nextGX * 16;
        targetY = nextGY * 16;

        // Update direction for sprite rendering
        if (dirX != 0) {
            currentDirection = dirX > 0 ? -1 : 1;
        }

        isMoving = true;
    }
    
    public void update(Dungeon dungeon) {
        if (!isMoving) return;
    
        float step = speed * Gdx.graphics.getDeltaTime();
    
        float dx = targetX - x;
        float dy = targetY - y;
    
        // --- X axis ---
        if (Math.abs(dx) > step) {
            x += (speed * Gdx.graphics.getDeltaTime()) * ((dx > 0) ? 1 : -1);
        } else {
            x = targetX;
        }
    
        // --- Y axis ---
        if (Math.abs(dy) > step) {
            y += (speed * Gdx.graphics.getDeltaTime()) * ((dy > 0) ? 1 : -1);
        } else {
            y = targetY;
        }
    
        // Update your hitbox AFTER both axes
        updateBoundingBox(x, y);
    
        // If we’ve reached the destination on both axes, stop
        if (x == targetX && y == targetY) {
            isMoving = false;
        }
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentTex = currentDirection > 0 ? spriteRight : spriteLeft;
        // snap to integer pixels to avoid sub‑pixel jitter
        batch.draw(currentTex, x, y, width, height);
    }

    public boolean isMoving() {
        return isMoving;
    }

    // Getters
    public float getX() { return x; }
    public float getY() { return y; }
    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getSpeed() { return speed; }
    public int getTargetX() { return targetX; }
    public int getTargetY() { return targetY; }
    public TextureRegion getSpriteLeft() { return spriteLeft; }
    public TextureRegion getSpriteRight() { return spriteRight; }
    public Rectangle getBoundingBox() { return boundingBox; }
    public int getCurrentDirection() { return currentDirection; }  

    // Setters
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}
