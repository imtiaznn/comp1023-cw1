package comp1023.loadeddice;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
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

    protected boolean isMoving = false;
    protected Direction direction;;

    // Properties variables
    protected int width = 16;
    protected int height = 16; 

    protected int health;
    protected int maxHealth;
    protected int speed;

    protected Rectangle boundingBox;

    public enum Direction {
        UP    (0,  1),
        DOWN  (0, -1),
        LEFT  (-1, 0),
        RIGHT (1,  0);
    
        private final Vector2 vec;
        Direction(float x, float y) {
            vec = new Vector2(x, y);
        }

        public Vector2 vector() {
            return vec.cpy();
        }
    }

    public Entity(float x, float y, int health, int speed, String spritePath) {
        Texture spriteTex = new Texture(Gdx.files.internal(spritePath));
        direction = Direction.DOWN;
        
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

        // Update direction for sprite rendering if (dirX == 0 && dirY == 1) direction = Direction.UP;
        if (dirX == 0 && dirY == 1) direction = Direction.UP;
        else if (dirX == 0 && dirY == -1) direction = Direction.DOWN;
        else if (dirX == -1 && dirY == 0) direction = Direction.LEFT;
        else if (dirX == 1 && dirY == 0) direction = Direction.RIGHT;

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

    public void renderHealthBar(SpriteBatch batch) {

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        Texture whitePixel = new Texture(pm);
        
        float pct = (float)health / maxHealth;
        float barW = width, barH = 4;
        float bx = x, by = y + height + 2;
        batch.setColor(Color.RED);
        batch.draw(whitePixel, bx, by, barW, barH);
        batch.setColor(Color.GREEN);
        batch.draw(whitePixel, bx, by, barW * pct, barH);
        batch.setColor(Color.WHITE);
        
        pm.dispose();
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentTex =  direction == Direction.LEFT ? spriteRight : spriteLeft;
        // snap to integer pixels to avoid sub‑pixel jitter
        batch.draw(currentTex, x, y, width, height);
    }

    public void dispose() {
        // Dispose of any resources used by the entity
        spriteLeft.getTexture().dispose();
        spriteRight.getTexture().dispose();
    }

    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health < 0) this.health = 0;
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
    public Direction getDirection() { return direction; }

    // Setters
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setGridX(int gridX) { this.gridX = gridX; }
    public void setGridY(int gridY) { this.gridY = gridY; }
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public void setHealth(int health) { this.health = health; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }
    public void setSpeed(int speed) { this.speed = speed; }
    public void setTargetX(int targetX) { this.targetX = targetX; }
    public void setTargetY(int targetY) { this.targetY = targetY; }
    public void setSpriteLeft(TextureRegion spriteLeft) { this.spriteLeft = spriteLeft; }
    public void setSpriteRight(TextureRegion spriteRight) { this.spriteRight = spriteRight; }
    public void setBoundingBox(Rectangle boundingBox) { this.boundingBox = boundingBox; }
    public void setDirection(Direction direction) { this.direction = direction; }
}
