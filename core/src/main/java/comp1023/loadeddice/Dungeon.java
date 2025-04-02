package comp1023.loadeddice;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;

public class Dungeon {
    enum Tiles {
        // Void or non-void
        VOID(0),
        FLOOR(1),

        // Corners
        UL(3), UR(4), LL(5), LR(6),
        
        // Walls and hallways
        UP(8), DOWN(8),
        LEFT(7), RIGHT(7),
        
        HALL(9);

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
    public void generateFloors(List<Room> roomsList, int[][] dungeonGrid) {
        for (Room room : roomsList) {
            for (int x = room.x + 1; x < room.x + room.width - 1; x++) {
                for (int y = room.y + 1; y < room.y + room.height - 1; y++) {
                    dungeonGrid[x][y] = Tiles.FLOOR.getTile(); // floor
                }
            }
        }
    }

    // Change floors into walls, doors, spawns, etc.
    public int[][] generateWalls(int mapWidth, int mapHeight, int[][] dungeonGrid) {
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
                    else newGrid[x][y] = 0; // void
                }
            }
        }

        return newGrid;
    }

    // Find paths between 2 centers of a room
    public void generateHallways(List<Vector2> centers, int[][] dungeonGrid) {
        Random rand = new Random();
        for (int i = 0; i < centers.size() - 1; i++) {
            Vector2 a = adjustCenterInside(centers.get(i), dungeonGrid);
            Vector2 b = adjustCenterInside(centers.get(i + 1), dungeonGrid);
    
            int ax = (int) a.x, ay = (int) a.y;
            int bx = (int) b.x, by = (int) b.y;
    
            if (rand.nextBoolean()) {
                carveHorizontal(ax, bx, ay, dungeonGrid);
                carveVertical(ay, by, bx, dungeonGrid);
            } else {
                carveVertical(ay, by, ax, dungeonGrid);
                carveHorizontal(ax, bx, by, dungeonGrid);
            }
        }
    }
    
    
    // Carve horizontal hallways
    private void carveHorizontal(int x1, int x2, int y, int[][] grid) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            grid[x][y] = Tiles.FLOOR.getTile();
        }
    }
    
    // Carve vertical hallways
    private void carveVertical(int y1, int y2, int x, int[][] grid) {
        for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
            grid[x][y] = Tiles.FLOOR.getTile();
        }
    }

    private Vector2 adjustCenterInside(Vector2 center, int[][] grid) {
        int x = (int) center.x;
        int y = (int) center.y;
    
        if (Tiles.isWall(grid[x][y])) {
            if (x + 1 < grid.length && !Tiles.isWall(grid[x + 1][y])) return new Vector2(x + 1, y);
            if (x - 1 >= 0 && !Tiles.isWall(grid[x - 1][y])) return new Vector2(x - 1, y);
            if (y + 1 < grid[0].length && !Tiles.isWall(grid[x][y + 1])) return new Vector2(x, y + 1);
            if (y - 1 >= 0 && !Tiles.isWall(grid[x][y - 1])) return new Vector2(x, y - 1);
        }
    
        return center;
    }
    
}