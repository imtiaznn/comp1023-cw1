package comp1023.loadeddice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class Main extends ApplicationAdapter {
    // Tilemap variables
    Texture tileset;
    TextureRegion[][] tiles;
    TextureRegion[] tileRegions;

    // Debug variables
    Debug debug;

    // Experimental Variables
    int numOfRooms = 5;
    int mapWidth = 60;
    int mapHeight = 40;
    int minRoomSize = 12;
    int maxRoomSize = 14;
    
    // Setup variables
    int[][] dungeonGrid;
    
    OrthographicCamera camera;
    SpriteBatch batch;
    
    @Override
    public void create() {
        debug = new Debug();

        // Variable initialisation
        camera = new OrthographicCamera();
        camera.setToOrtho(false, mapWidth * 16, mapHeight * 16); // Show entire map

        batch = new SpriteBatch();

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
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                int tileId = dungeonGrid[x][y];
                if (tileId >= 0 && tileId < tileRegions.length) {
                    batch.draw(tileRegions[tileId], x * 16, y * 16);
                }
            }
        }
    
        // Render debug button and regenerate if clicked
        if (debug.renderButton(batch)) {
            generateDungeon();
        }
    
        batch.end();
    }
    

    @Override
    public void dispose() {
        batch.dispose();
        tileset.dispose();
    }

    public void generateDungeon() {    
        dungeonGrid = new int[mapWidth][mapHeight];
        List<Room> rooms = new ArrayList<>();
        
        Room root = new Room(0, 0, mapWidth, mapHeight);
        root.split(minRoomSize, maxRoomSize, rooms);

        Collections.shuffle(rooms);
        List<Room> roomList = rooms.subList(0, Math.min(numOfRooms, rooms.size()));

        // Add tiles to dungeonGrid
        new Dungeon().generateFloors(roomList, dungeonGrid);
        dungeonGrid = new Dungeon().generateWalls(mapWidth, mapHeight, dungeonGrid);
    }
}