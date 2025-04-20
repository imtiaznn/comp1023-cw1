package comp1023.loadeddice;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player extends Entity {
    private final Texture playerTexture;
    private final TextureRegion[][] walkFrames;
    private final Rectangle bounds;
    private static final float MOVE_SPEED = 200f;
    private static final int PLAYER_SIZE = 16;
    private Direction currentDirection = Direction.DOWN;
    private float stateTime = 0;
    private int currentFrame = 0;
    private final Dice dice;
    private final List<Projectile> projectiles;
    private static final float ATTACK_COOLDOWN = 0.5f;
    private float timeSinceLastAttack = 0;
    private static final float PROJECTILE_SPEED = 300f;
    private static final float BASE_DAMAGE = 10f;
    
    private enum Direction {
        DOWN(0), LEFT(1), RIGHT(2), UP(3);
        private final int row;
        Direction(int row) { this.row = row; }
        public int getRow() { return row; }
    }
    
    public Player(float x, float y) {
        super(x, y, MOVE_SPEED, 100);
        this.width = PLAYER_SIZE;
        this.height = PLAYER_SIZE;
        this.bounds = new Rectangle(x, y, width, height);
        this.dice = new Dice();
        this.projectiles = new ArrayList<>();
        this.playerTexture = new Texture("wizard_walking.png");
        this.walkFrames = TextureRegion.split(playerTexture, 
            playerTexture.getWidth() / 3,
            playerTexture.getHeight() / 4
        );
    }
    
    @Override
    public void update(float deltaTime) {
        stateTime += deltaTime;
        timeSinceLastAttack += deltaTime;
        
        float moveX = 0;
        float moveY = 0;
        
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W)) {
            moveY = 1;
            currentDirection = Direction.UP;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S)) {
            moveY = -1;
            currentDirection = Direction.DOWN;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A)) {
            moveX = -1;
            currentDirection = Direction.LEFT;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) {
            moveX = 1;
            currentDirection = Direction.RIGHT;
        }
        
        if (Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT) && timeSinceLastAttack >= ATTACK_COOLDOWN) {
            attack();
        }
        
        if (moveX != 0 && moveY != 0) {
            moveX *= 0.7071f;
            moveY *= 0.7071f;
        }
        
        move(moveX * deltaTime, moveY * deltaTime);
        bounds.setPosition(x, y);
        
        if (moveX != 0 || moveY != 0) {
            currentFrame = (int)(stateTime * 8) % 3;
        } else {
            currentFrame = 0;
        }
        
        updateProjectiles(deltaTime);
    }
    
    public void attack() {
        if (timeSinceLastAttack >= ATTACK_COOLDOWN) {
            float playerCenterX = x + PLAYER_SIZE/2;
            float playerCenterY = y + PLAYER_SIZE/2;
            
            Vector2 direction = new Vector2();
            switch (currentDirection) {
                case UP: direction.set(0, 1); break;
                case DOWN: direction.set(0, -1); break;
                case LEFT: direction.set(-1, 0); break;
                case RIGHT: direction.set(1, 0); break;
            }
            
            int numProjectiles = dice.roll();
            
            for (int i = 0; i < numProjectiles; i++) {
                Vector2 spreadDirection = new Vector2(direction);
                if (numProjectiles > 1) {
                    float spreadAngle = (i - (numProjectiles - 1) / 2f) * 15f;
                    spreadDirection.rotateDeg(spreadAngle);
                }
                
                Projectile projectile = new Projectile(
                    playerCenterX, 
                    playerCenterY,
                    spreadDirection,
                    PROJECTILE_SPEED,
                    BASE_DAMAGE,
                    dice.roll()
                );
                projectiles.add(projectile);
            }
            
            timeSinceLastAttack = 0;
        }
    }
    
    private void updateProjectiles(float deltaTime) {
        List<Projectile> projectilesToRemove = new ArrayList<>();
        
        for (Projectile projectile : projectiles) {
            projectile.update(deltaTime);
            if (!projectile.isActive()) {
                projectilesToRemove.add(projectile);
            }
        }
        
        projectiles.removeAll(projectilesToRemove);
        for (Projectile projectile : projectilesToRemove) {
            projectile.dispose();
        }
    }
    
    @Override
    public void render(SpriteBatch batch) {
        TextureRegion currentTexture = walkFrames[currentDirection.getRow()][currentFrame];
        batch.draw(currentTexture, x, y, width, height);
        
        for (Projectile projectile : projectiles) {
            projectile.render(batch);
        }
    }
    
    public Rectangle getBounds() {
        return bounds;
    }
    
    public List<Projectile> getProjectiles() {
        return projectiles;
    }
    
    public void dispose() {
        playerTexture.dispose();
        for (Projectile projectile : projectiles) {
            projectile.dispose();
        }
    }
} 