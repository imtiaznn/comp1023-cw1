package comp1023.loadeddice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Main extends ApplicationAdapter {
    Texture tileset;
    TextureRegion[][] tiles;
    TextureRegion[] tileRegions;
    Debug debug;
    int numOfRooms = 5;
    int mapWidth = 60;
    int mapHeight = 40;
    int minRoomSize = 12;
    int maxRoomSize = 14;
    int[][] dungeonGrid;
    OrthographicCamera camera;
    Viewport viewport;
    SpriteBatch batch;
    private Player player;
    
    @Override
    public void create() {
        debug = new Debug();
        camera = new OrthographicCamera();
        viewport = new FitViewport(mapWidth * 12, mapHeight * 12, camera);
        viewport.apply();
        batch = new SpriteBatch();
        tileset = new Texture("Tilemap/tilemap_packed.png");
        tiles = TextureRegion.split(tileset, 16, 16);
        int rows = tiles.length;
        int cols = tiles[0].length;
        tileRegions = new TextureRegion[rows * cols];
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                tileRegions[y * cols + x] = tiles[y][x];
            }
        }
        generateDungeon();
        Vector2 startPos = findStartPosition();
        player = new Player(startPos.x * 16, startPos.y * 16);
        camera.position.set(player.getX() + player.getWidth()/2, 
                          player.getY() + player.getHeight()/2, 0);
    }

    private Vector2 findStartPosition() {
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                if (dungeonGrid[x][y] == 1) {
                    return new Vector2(x, y);
                }
            }
        }
        return new Vector2(mapWidth/2, mapHeight/2);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        float halfViewportWidth = viewport.getWorldWidth() / 2;
        float halfViewportHeight = viewport.getWorldHeight() / 2;
        float minCameraX = halfViewportWidth;
        float maxCameraX = (mapWidth * 16) - halfViewportWidth;
        float minCameraY = halfViewportHeight;
        float maxCameraY = (mapHeight * 16) - halfViewportHeight;
        
        float targetX = player.getX() + player.getWidth()/2;
        float targetY = player.getY() + player.getHeight()/2;
        
        targetX = Math.max(minCameraX, Math.min(maxCameraX, targetX));
        targetY = Math.max(minCameraY, Math.min(maxCameraY, targetY));
        
        camera.position.set(targetX, targetY, 0);
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
        
        player.update(Gdx.graphics.getDeltaTime());
        player.render(batch);
        
        if (debug.renderButton(batch)) {
            generateDungeon();
            Vector2 startPos = findStartPosition();
            player.setPosition(startPos.x * 16, startPos.y * 16);
        }
        
        handleProjectileCollisions();
        
        batch.end();
    }
    
    private void handleProjectileCollisions() {
        List<Projectile> projectiles = player.getProjectiles();
        for (Projectile projectile : projectiles) {
            Rectangle projectileBounds = projectile.getBounds();
            int tileX = (int)(projectileBounds.x / 16);
            int tileY = (int)(projectileBounds.y / 16);
            
            if (tileX >= 0 && tileX < mapWidth && tileY >= 0 && tileY < mapHeight) {
                int tileId = dungeonGrid[tileX][tileY];
                if (Dungeon.Tiles.isWall(tileId)) {
                    projectile.setActive(false);
                }
            }
            
            if (projectileBounds.x < 0 || projectileBounds.x > mapWidth * 16 ||
                projectileBounds.y < 0 || projectileBounds.y > mapHeight * 16) {
                projectile.setActive(false);
            }
        }
    }
    
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        viewport.apply();
    }

    @Override
    public void dispose() {
        batch.dispose();
        tileset.dispose();
        player.dispose();
    }

    public void generateDungeon() {
        dungeonGrid = new int[mapWidth][mapHeight];
        List<Room> rooms = new ArrayList<>();
        List<Vector2> roomCentreList = new ArrayList<>();
        
        Room root = new Room(0, 0, mapWidth, mapHeight);
        root.split(minRoomSize, maxRoomSize, rooms);

        Collections.shuffle(rooms);
        List<Room> roomList = rooms.subList(0, Math.min(numOfRooms, rooms.size()));
        
        new Dungeon().generateFloors(roomList, dungeonGrid);
        dungeonGrid = new Dungeon().generateWalls(mapWidth, mapHeight, dungeonGrid);
        
        for (Room room : roomList) {
            roomCentreList.add(room.center);
        }

        roomCentreList.sort(Comparator.comparingDouble(v -> v.x));
        new Dungeon().generateHallways(roomCentreList, dungeonGrid);
    }
}