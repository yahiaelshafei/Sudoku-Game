//indices of the boxes
// 0 -> 00
// 1 -> 03
// 2 -> 06
// 3 -> 30
// 4 -> 33
// 5 -> 36
// 6 -> 60
// 7 -> 63
// 8 -> 66
// n -> ((n/3) * 3) ((n%3)*3)

import java.util.HashMap;
import java.util.Map;

public class Box extends GridElement {

    private static Map<Integer,Box> instances = new HashMap<Integer, Box>();

    private Box(int boxNumber) {
        super(boxNumber, Type.BOX);
        this.rowNumber = ((boxNumber) / 3) * 3;
        this.columnNumber = ((boxNumber) % 3) * 3;
    }

    public static Box getInstance(int boxNumber) {
        if(!instances.containsKey(boxNumber)) instances.put(boxNumber, new Box(boxNumber));
        return instances.get(boxNumber);
    }

    public boolean scan() {
        grid = board.getGrid();
        for (int i = 0; i < 9; i++) {
            int x = columnNumber + (i % 3);
            int y = rowNumber + (i / 3);
            locations[grid[y][x]].append(Integer.toString(i + 1));
            status &= locations[grid[y][x]].length() == 1;
        }
        return status;
    }

    public boolean scan(Map<Integer,Integer> emptyCells, int[] permutation) {
        grid = board.getGrid();
        for (int i = 0; i < 9; i++) {
            int x = columnNumber + (i % 3);
            int y = rowNumber + (i / 3);

            int z = grid[y][x];
            if(z == 0){
                z = permutation[ emptyCells.get(y * 10 + x)];
            }
            locations[z].append(Integer.toString(i + 1));
            status &= locations[z].length() == 1;
        }
        return status;
    }

}