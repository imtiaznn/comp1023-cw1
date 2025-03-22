package comp1023.loadeddice;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;

public class Room {
    public int x, y, width, height;
    public Room left, right;

    public Room(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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

    // Optional: isLeaf check
    public boolean isLeaf() {
        return left == null && right == null;
    }
}