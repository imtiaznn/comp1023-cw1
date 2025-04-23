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

    // Debug variables
    Debug debug;

    // Experimental variables
    int numOfRooms = 5;
    int mapWidth = 60;
    int mapHeight = 40;
    int minRoomSize = 8;
    int maxRoomSize = 10;

    // Game variables
    Dungeon dungeon = new Dungeon(1, mapWidth, mapHeight);
    Player player;
    Array<Enemy> enemies;
    
    // Setup variables
    OrthographicCamera camera;
    Viewport viewport;
    SpriteBatch batch;
    
    @Override
    public void create() {
        // Debug initialisation
        debug = new Debug();

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

        generateDungeon();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    
        batch.begin();

        // Render debug button and regenerate if clicked
        if (debug.renderButton(batch)) {
            generateDungeon();
        }

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

        // Update enemies
        for (Enemy enemy : enemies) {
            enemy.update(dungeon);
            enemy.render(batch);
        }

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
    }

    public void generateDungeon() {
        // Variable initialisation
        dungeon = new Dungeon(1, mapWidth, mapHeight);
        List<Room> rooms = dungeon.getRoomsList();
        List<Vector2> roomCentreList = new ArrayList<>();
        Array<Rectangle> spawns = dungeon.getSpawns();
        
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
        player = new Player(spawn.x, spawn.y, 100, 175, 100, 0, 10, 5, 10);
        spawns.removeIndex(randomIndex);

        enemies = new Array<>();

        // Enemy spawns
        int level = dungeon.getFloorNumber();
        int totalEnemies = (level + 1) * 2;

        for (int i = 0; i < totalEnemies; i++) {
            // Randomly select a spawn point for the enemy
            randomIndex = MathUtils.random(0, spawns.size - 1);
            spawn = spawns.get(randomIndex);
            // Remove the spawn point from the list
            spawns.removeIndex(randomIndex);
            // Create a new enemy
            Enemy enemy = new Enemy(spawn.x, spawn.y, 100, 50);
            // Add the enemy to the list
            enemies.add(enemy);
        }
    }
}