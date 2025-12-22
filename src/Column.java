import java.util.HashMap;
import java.util.Map;

public class Column extends GridElement {
    private static Map<Integer,Column> instances = new HashMap<Integer, Column>();

    private Column(int columnNumber) {
        super(columnNumber, Type.COL);
    }

    public static Column getInstance(int columnNumber) {
        if(!instances.containsKey(columnNumber)) instances.put(columnNumber, new Column(columnNumber));
        return instances.get(columnNumber);
    }

    public boolean scan() {
        grid = board.getGrid();
        for (int i = 0; i < 9; i++) {
            locations[grid[i][columnNumber]].append(Integer.toString(i + 1));
            status &= locations[grid[i][columnNumber]].length() == 1;
        }
        return status;
    }
    public boolean scan(Map<Integer,Integer> emptyCells, int[] permutation) {
        grid = board.getGrid();
        for (int i = 0; i < 9; i++) {
            int x = grid[i][columnNumber];
            if(x == 0){
                x = permutation[ emptyCells.get(i * 10 + columnNumber)];
            }
            locations[x].append(Integer.toString(i + 1));
            status &= locations[x].length() == 1;
        }
        return status;
    }

}