package comp1023.loadeddice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Main extends ApplicationAdapter {
    // Tilemap variables
    Texture tileset;
    TextureRegion[][] tiles;
    TextureRegion[] tileRegions;

    // Experimental variables
    int numOfRooms = 5;
    int mapWidth = 60;
    int mapHeight = 40;
    int minRoomSize = 8;
    int maxRoomSize = 10;

    // Game variables
    int floorNumber = 1;
    Dungeon dungeon = new Dungeon(1, mapWidth, mapHeight);
    Player player;
    Array<Enemy> enemies = new Array<>();
    Array<Projectile> projectiles = new Array<>();
    
    // Setup variables
    OrthographicCamera camera;
    Viewport viewport;
    SpriteBatch batch;

    private BitmapFont font;
    private Texture uiPixel;
    private com.badlogic.gdx.graphics.g2d.GlyphLayout layout;
    
    @Override
    public void create() {
        // Variable initialisation
        int camWidth = mapWidth * 4;
        int camHeight = mapHeight * 4;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, camWidth, camHeight);

        viewport = new FitViewport(camWidth, camHeight, camera);

        batch = new SpriteBatch();

        // Tilemap initialisation
        tileset = new Texture("Tilemap/tilemap_packed.png");
        tiles = TextureRegion.split(tileset, 16, 16); // Split tilemap into 2D array

        int rows = tiles.length;
        int cols = tiles[0].length;

        // Flatten tiles into 1D array tileRegions   
        tileRegions = new TextureRegion[rows * cols];
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                tileRegions[y * cols + x] = tiles[y][x];
            }
        }

        font = new BitmapFont();
        font.getData().setScale(0.5f, 0.5f);
        font.setColor(Color.WHITE);

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        uiPixel = new Texture(pm);
        pm.dispose();

        layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();

        generateDungeon();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    
        batch.begin();

        // Render dungeon grid on button press
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                int tileId = dungeon.getDungeonGrid()[x][y];
                if (tileId >= 0 && tileId < tileRegions.length) {
                    batch.draw(tileRegions[tileId], x * 16, y * 16);
                }
            }
        }

        // Update player
        player.update(dungeon);
        player.render(batch);
        player.renderHealthBar(batch);

        // Next floor logic
        if(Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.E)) {
            if (player.getBoundingBox().overlaps(dungeon.getStair())) {
                floorNumber++;
                generateDungeon();
            }
        }

        // Update enemies
        for (Enemy enemy : enemies) {
            enemy.update(dungeon);
            enemy.render(batch);

            enemy.renderHealthBar(batch);
        }

        // Handle collisions
        handleCollisions();

        String text = "Floor: " + floorNumber;
        layout.setText(font, text);
        float textW = layout.width;
        float textH = layout.height;
        
        float hudX = camera.position.x - viewport.getWorldWidth()/2 + 10;
        float hudY = camera.position.y + viewport.getWorldHeight()/2 - 10;
        
        float pad = 4f;
        // dark‐gray background
        batch.setColor(Color.DARK_GRAY);
        batch.draw(uiPixel,
            hudX - pad,
            hudY - textH - pad,
            textW + pad * 2,
            textH + pad * 2
        );
        // white text
        batch.setColor(Color.WHITE);
        font.draw(batch, text, hudX, hudY);


        // Update camera position based on player position
        float camX = player.getX() + player.getWidth()/2;
        float camY = player.getY() + player.getHeight()/2;
        
        camX = Math.max(
            viewport.getWorldWidth() / 2,
            Math.min((mapWidth * 16) - viewport.getWorldWidth() / 2, camX)
            );
        camY = Math.max(
            viewport.getWorldHeight() / 2, 
            Math.min((mapHeight *16) - viewport.getWorldHeight() / 2, camY)
            );
        
        camera.position.set(Math.round(camX), Math.round(camY), 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    
        batch.end();
    }
    
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void dispose() {
        batch.dispose();
        tileset.dispose();
        font.dispose();
        uiPixel.dispose();
    }

    public void generateDungeon() {
        // Variable initialisation
        dungeon = new Dungeon(floorNumber, mapWidth, mapHeight);
        List<Room> rooms = dungeon.getRoomsList();
        List<Vector2> roomCentreList = new ArrayList<>();
        
        // Procedural dungeon generation
        Room root = new Room(0, 0, mapWidth, mapHeight);
        root.split(minRoomSize, maxRoomSize, rooms);

        // Select rooms for dungeon
        Collections.shuffle(rooms);
        List<Room> roomList = rooms.subList(0, Math.min(numOfRooms, rooms.size()));
        
        // Assign tileIDs to dungeonGrid
        dungeon.generateFloors(roomList);
        int[][] newGrid = dungeon.generateWalls(mapWidth, mapHeight);
        dungeon.setDungeonGrid(newGrid);
        
        // Get the centres of selected rooms and sort them
        for (Room room : roomList) {
            roomCentreList.add(room.getCenter());
        }

        // Generate spawns
        dungeon.generateSpawns();
        Array<Rectangle> spawns = dungeon.getSpawns();

        // Generate hallways between rooms
        roomCentreList.sort(Comparator.comparingDouble(v -> v.x));
        dungeon.generateHallways(roomCentreList);

        // Add collision boxes dungeon
        dungeon.addCollision();

        // Randomly select a spawn point for the player
        int randomIndex = MathUtils.random(0, spawns.size - 1);
        Rectangle spawn = spawns.get(randomIndex);

        // --- Spawner initialisations ---
        // Stair spawns 
        dungeon.addStairs();

        // Player spawns
        player = new Player(spawn.x, spawn.y, 100, 125, 100, 10, 6);
        spawns.removeIndex(randomIndex);
        
        // Enemy spawns
        enemies = new Array<>();

        int totalEnemies = Math.min((floorNumber+1)*2, spawns.size);

        for (int i = 0; i < totalEnemies; i++) {
            // Randomly select a spawn point for the enemy
            randomIndex = MathUtils.random(0, spawns.size - 1);
            spawn = spawns.get(randomIndex);
            // Remove the spawn point from the list
            spawns.removeIndex(randomIndex);
            // Create a new enemy
            Enemy enemy = new Enemy(spawn.x, spawn.y, 90, 50);
            // Add the enemy to the list
            enemies.add(enemy);
        }
    }

    public void handleCollisions() {
        Array<Projectile>   projList = player.getProjectiles();
        Array<Enemy>        enemyList = enemies;
        Rectangle playerBox = player.getBoundingBox();
    
        // Projectile collision detection
        for (int i = projList.size - 1; i >= 0; i--) {
            Projectile p = projList.get(i);
    
            // Skip ones that already hit something
            if (!p.isActive()) continue;
    
            Rectangle pBox = p.getBoundingBox();
    
            // Projectile collision
            for (int j = enemyList.size - 1; j >= 0; j--) {
                Enemy e = enemyList.get(j);
                if (pBox.overlaps(e.getBoundingBox())) {
                    e.takeDamage(p.getDamage());

                    if (e.getHealth() <= 0) {
                        enemyList.removeIndex(j);
                    }
    
                    p.setActive(false);
                    projList.removeIndex(i);
                    break;
                }
            }

            // Wall collision
            Array<Rectangle> walls = dungeon.getCollisionBoxes();
            for (Rectangle wall : walls) {
                if (pBox.overlaps(wall)) {
                    p.setActive(false);
                    projList.removeIndex(i);
                    break;
                }
            }

        }

        // Player collision dtection
        for (Enemy e : enemies) {
            if (playerBox.overlaps(e.getBoundingBox())) {
                if (player.getImmunity() <= 0) {
                    player.takeDamage(e.getDamage());
                    player.setImmunity(0.5f);
                }
            }
        }

    }
    

}