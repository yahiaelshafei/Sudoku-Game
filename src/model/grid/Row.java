package model.grid;

import java.util.*;

public class Row extends GridElement {
    public Row(int rowNumber) {
        super(rowNumber, Type.ROW);
    }

    public boolean scan() {
        for (int i = 0; i < 9; i++) {
            locations[grid[rowNumber][i]].append(Integer.toString(i + 1));
            status &= locations[grid[rowNumber][i]].length() == 1;
        }
        return status;
    }

    public boolean scan(Map<Integer, Integer> emptyCells, int[] permutation) {
        for (int i = 0; i < 9; i++) {
            int x = grid[rowNumber][i];
            if (x == 0) {
                x = permutation[emptyCells.get(rowNumber * 10 + i)];
            }
            locations[x].append(Integer.toString(i + 1));
            status &= locations[x].length() == 1;
        }
        return status;
    }
}