package comp1023.loadeddice;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Room {
    private int x, y, width, height;
    private Vector2 center;
    private List<Vector2> spawns;
    private Room left, right;

    public Room(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.center = new Vector2(x + width / 2f, y + height / 2f);

        this.spawns = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            int spawnX = MathUtils.random(x + 1, x + width - 2);
            int spawnY = MathUtils.random(y + 1, y + height - 2);
            this.spawns.add(new Vector2(spawnX, spawnY));
        }
    }

    public void split(int minSize, int maxSize, List<Room> roomList) {
        // Base condition
        if ((width <= maxSize && height <= maxSize) || (width < 2 * minSize && height < 2 * minSize)) {
            roomList.add(this);
            return;
        }

        int gap = 1;
        boolean splitHorizontally = width < height;

        if (splitHorizontally) {
            int maxSplitHeight = height - minSize - gap;
            if (maxSplitHeight < minSize) return;

            int split = MathUtils.random(minSize, height - minSize - gap);

            left = new Room(x, y, width, split);
            right = new Room(x, y + split + gap, width, height - split - gap);
        } else {
            int maxSplitWidth = width - minSize - gap;
            if (maxSplitWidth < minSize) return;

            int split = MathUtils.random(minSize, width - minSize - gap);

            left = new Room(x, y, split, height);
            right = new Room(x + split + gap, y, width - split - gap, height);
        }

        if (left != null) left.split(minSize, maxSize, roomList);
        if (right != null) right.split(minSize, maxSize, roomList);
    }

    // check if the room is a leaf node
    public boolean isLeaf() {
        return left == null && right == null;
    }

    // Getters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Vector2 getCenter() {
        return center;
    }

    public List<Vector2> getSpawns() {
        return spawns;
    }

    public Room getLeft() {
        return left;
    }

    public Room getRight() {
        return right;
    }
}