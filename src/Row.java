import java.util.HashMap;
import java.util.Map;

public class Row extends GridElement {
    private static Map<Integer,Row> instances = new HashMap<Integer, Row>();

    private Row(int rowNumber) {
        super(rowNumber, Type.ROW);
    }

    public static Row getInstance(int rowNumber) {
        if(!instances.containsKey(rowNumber)) instances.put(rowNumber, new Row(rowNumber));
        return instances.get(rowNumber);
    }



    public boolean scan() {
        grid = board.getGrid();
        for (int i = 0; i < 9; i++) {
            locations[grid[rowNumber][i]].append(Integer.toString(i + 1));
            status &= locations[grid[rowNumber][i]].length() == 1;
        }
        return status;
    }
    public boolean scan(Map<Integer,Integer> emptyCells, int[] permutation) {
        grid = board.getGrid();
        for (int i = 0; i < 9; i++) {
            int x = grid[rowNumber][i];
            if(x == 0){
                x = permutation[ emptyCells.get(rowNumber * 10 + i)];
            }
            locations[x].append(Integer.toString(i + 1));
            status &= locations[x].length() == 1;
        }
        return status;
    }

}