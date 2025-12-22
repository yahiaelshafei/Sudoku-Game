package model.grid;

import java.util.Map;

public class Column extends GridElement {
    public Column(int columnNumber) {
        super(columnNumber, Type.COL);
    }

    public boolean scan() {
        for (int i = 0; i < 9; i++) {
            locations[grid[i][columnNumber]].append(Integer.toString(i + 1));
            status &= locations[grid[i][columnNumber]].length() == 1;
        }
        return status;
    }

    public boolean scan(Map<Integer, Integer> emptyCells, int[] permutation) {
        for (int i = 0; i < 9; i++) {
            int x = grid[i][columnNumber];
            if (x == 0) {
                x = permutation[emptyCells.get(i * 10 + columnNumber)];
            }
            locations[x].append(Integer.toString(i + 1));
            status &= locations[x].length() == 1;
        }
        return status;
    }

}