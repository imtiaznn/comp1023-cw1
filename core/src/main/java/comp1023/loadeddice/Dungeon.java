package comp1023.loadeddice;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Dungeon {
    private int floorNumber;
    private int[][] dungeonGrid;
    private List<Room> roomsList;
    private Array<Rectangle> collisionBoxes;
    private Array<Rectangle> spawns;
    private Rectangle stair;

    // Constructor
    public Dungeon(int floorNumber, int width, int height) {
        this.floorNumber = floorNumber;
        this.dungeonGrid = new int[width][height];
        this.roomsList = new ArrayList<>();
        this.collisionBoxes = new Array<>();
        this.spawns = new Array<>();
    }

    enum Tiles {
        // Void or non-void
        VOID(0),
        FLOOR(1),

        // Corners
        UL(3), UR(4), LL(5), LR(6),
        
        // Walls and hallways
        UP(8), DOWN(8),
        LEFT(7), RIGHT(7),
        
        STAIR(9);

        private final int value;

        Tiles(int value) {
            this.value = value;
        }

        public int getTile() {
            return value;
        }

        // Check tile is a wall
        public static boolean isWall(int value) {
            return value == UP.getTile() || 
            value == DOWN.getTile() || 
            value == RIGHT.getTile() || 
            value == LEFT.getTile();
        }
    }

    // Mark leaf nodes as floors
    public void generateFloors(List<Room> roomsList) {
        for (Room room : roomsList) {
            for (int x = room.getX() + 1; x < room.getX() + room.getWidth() - 1; x++) {
                for (int y = room.getY() + 1; y < room.getY() + room.getHeight() - 1; y++) {
                    dungeonGrid[x][y] = Tiles.FLOOR.getTile(); // floor
                }
            }
        }
    }

    // Change floors into walls, doors, spawns, etc.
    public int[][] generateWalls(int mapWidth, int mapHeight) {
        int[][] newGrid = new int[mapWidth][mapHeight];

        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                if (dungeonGrid[x][y] == 1) {
                    newGrid[x][y] = Tiles.FLOOR.getTile(); // Change to proper floor tile
                } else {
                    //Checks whether tiles in surrounding 4 directions are floor or not
                    boolean up = y < mapHeight - 1 && dungeonGrid[x][y + 1] == Tiles.FLOOR.getTile();
                    boolean down = y > 0 && dungeonGrid[x][y - 1] == Tiles.FLOOR.getTile();
                    boolean left = x > 0 && dungeonGrid[x - 1][y] == Tiles.FLOOR.getTile();
                    boolean right = x < mapWidth - 1 && dungeonGrid[x + 1][y] == Tiles.FLOOR.getTile();

                    // Corners
                    if ((!up && !down && !left && !right) && y>0 && x<mapWidth-1 && dungeonGrid[x+1][y-1] == 1) newGrid[x][y] = Tiles.UL.getTile(); // upper-left
                    else if ((!up && !down && !left && !right) && y>0 && x>0 && dungeonGrid[x-1][y-1] == 1) newGrid[x][y] = Tiles.UR.getTile(); // upper-right
                    else if ((!up && !down && !left && !right) && y<mapHeight-1 && x<mapWidth-1 && dungeonGrid[x+1][y+1] == 1) newGrid[x][y] = Tiles.LL.getTile(); // lower-left
                    else if ((!up && !down && !left && !right) && y<mapHeight-1 && x>0 && dungeonGrid[x-1][y+1] == 1) newGrid[x][y] = Tiles.LR.getTile(); // lower-right

                    // Walls
                    else if (up) newGrid[x][y] = Tiles.UP.getTile();
                    else if (down) newGrid[x][y] = Tiles.DOWN.getTile();
                    else if (left) newGrid[x][y] = Tiles.LEFT.getTile();
                    else if (right) newGrid[x][y] = Tiles.RIGHT.getTile();

                    // Void
                    else newGrid[x][y] = 0; 
                }
            }
        }

        return newGrid;
    }

    // Find paths between 2 centers of a room
    public void generateHallways(List<Vector2> centers) {
        Random rand = new Random();
        for (int i = 0; i < centers.size() - 1; i++) {
            Vector2 a = adjustCenterInside(centers.get(i));
            Vector2 b = adjustCenterInside(centers.get(i + 1));
    
            int ax = (int) a.x, ay = (int) a.y;
            int bx = (int) b.x, by = (int) b.y;
    
            if (rand.nextBoolean()) {
                carveHorizontal(ax, bx, ay);
                carveVertical(ay, by, bx);
            } else {
                carveVertical(ay, by, ax);
                carveHorizontal(ax, bx, by);
            }
        }
    }

    public void generateSpawns() {
        Array<Vector2> floors = new Array<>();

        for (int x = 0; x < dungeonGrid.length; x++) {
            for (int y = 0; y < dungeonGrid[0].length; y++) {
                if (dungeonGrid[x][y] == Tiles.FLOOR.getTile()) {
                    floors.add(new Vector2(x, y));
                }
            }
        }

        // Shuffle to randomize
        floors.shuffle();

        // Choose up to 5 random floor tiles
        int spawnCount = (floorNumber + 1) * 2;
        for (int i = 0; i < spawnCount; i++) {
            Vector2 pos = floors.get(i);
            Rectangle spawn = new Rectangle(pos.x * 16, pos.y * 16, 16, 16);
            spawns.add(spawn);
        }
    }

    // Add collision to whole dungeon
    public void addCollision() {
        for (int x = 0; x < dungeonGrid.length; x++) {
            for (int y = 0; y < dungeonGrid[0].length; y++) {
                if (Tiles.isWall(dungeonGrid[x][y]) || dungeonGrid[x][y] == 0) {
                    Rectangle boundingBox = new Rectangle(x * 16, y * 16, 16, 16);
                    collisionBoxes.add(boundingBox);
                }
            }
        }
    }

    // Add stairs to the dungeon
    public void addStairs() {
        if (spawns.size == 0) return;
        Rectangle spawn = spawns.random();

        dungeonGrid[(int)(spawn.x/16)][(int)(spawn.y/16)] = Tiles.STAIR.getTile();
        stair = new Rectangle(spawn.x, spawn.y, 16, 16);
    }

    // -- Helper methods --
    // Carve horizontal hallways
    private void carveHorizontal(int x1, int x2, int y) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            dungeonGrid[x][y] = Tiles.FLOOR.getTile();
        }
    }
    
    // Carve vertical hallways
    private void carveVertical(int y1, int y2, int x) {
        for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
            dungeonGrid[x][y] = Tiles.FLOOR.getTile();
        }
    }

    private Vector2 adjustCenterInside(Vector2 center) {
        int x = (int) center.x;
        int y = (int) center.y;
    
        if (Tiles.isWall(dungeonGrid[x][y])) {
            if (x + 1 < dungeonGrid.length && !Tiles.isWall(dungeonGrid[x + 1][y])) return new Vector2(x + 1, y);
            if (x - 1 >= 0 && !Tiles.isWall(dungeonGrid[x - 1][y])) return new Vector2(x - 1, y);
            if (y + 1 < dungeonGrid[0].length && !Tiles.isWall(dungeonGrid[x][y + 1])) return new Vector2(x, y + 1);
            if (y - 1 >= 0 && !Tiles.isWall(dungeonGrid[x][y - 1])) return new Vector2(x, y - 1);
        }
    
        return center;
    }
    
    //Getters
    public int getFloorNumber() {
        return floorNumber;
    }

    public List<Room> getRoomsList() {
        return roomsList;
    }

    public int[][] getDungeonGrid() {
        return dungeonGrid;
    }

    public Array<Rectangle> getCollisionBoxes() {
        return collisionBoxes;
    }

    public Array<Rectangle> getSpawns() {
        return spawns;
    }

    public Rectangle getStair() {
        return stair;
    }

    //Setters
    public void setDungeonGrid(int[][] dungeonGrid) {
        this.dungeonGrid = dungeonGrid;
    }
}