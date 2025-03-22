package comp1023.loadeddice;

import java.util.ArrayList;
import java.util.List;

public class Dungeon {
    // Mark leaf nodes as floors
    public void generateFloors(List<Room> roomsList, int[][] dungeonGrid) {
        for (Room room : roomsList) {
            for (int x = room.x + 1; x < room.x + room.width - 1; x++) {
                for (int y = room.y + 1; y < room.y + room.height - 1; y++) {
                    dungeonGrid[x][y] = 1; // floor
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
                    newGrid[x][y] = 48; // Change to proper floor tile
                } else {
                    //Checks whether tiles in surrounding 4 directions are floor or not
                    boolean up = y < mapHeight - 1 && dungeonGrid[x][y + 1] == 1;
                    boolean down = y > 0 && dungeonGrid[x][y - 1] == 1;
                    boolean left = x > 0 && dungeonGrid[x - 1][y] == 1;
                    boolean right = x < mapWidth - 1 && dungeonGrid[x + 1][y] == 1;

                    // Corners
                    if ((!up && !down && !left && !right) && y>0 && x<mapWidth-1 && dungeonGrid[x+1][y-1] == 1) newGrid[x][y] = 1; // upper-left
                    else if ((!up && !down && !left && !right) && y>0 && x>0 && dungeonGrid[x-1][y-1] == 1) newGrid[x][y] = 3; // upper-right
                    else if ((!up && !down && !left && !right) && y<mapHeight-1 && x<mapWidth-1 && dungeonGrid[x+1][y+1] == 1) newGrid[x][y] = 25; // lower-left
                    else if ((!up && !down && !left && !right) && y<mapHeight-1 && x>0 && dungeonGrid[x-1][y+1] == 1) newGrid[x][y] = 27; // lower-right

                    // Walls
                    else if (up) newGrid[x][y] = 26;
                    else if (down) newGrid[x][y] = 2;
                    else if (left) newGrid[x][y] = 15;
                    else if (right) newGrid[x][y] = 13;
                    else newGrid[x][y] = 0; // void
                }
            }
        }

        return newGrid;
    }
}
