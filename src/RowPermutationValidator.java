import java.util.Map;

public class RowPermutationValidator implements ValidationStrategy{
    private Row[] rows = new Row[9];

    public Map<Integer, Integer> emptyCells;
    int[] permutation;

    private static boolean status;

    public RowPermutationValidator(Map<Integer, Integer> emptyCells, int[] permutations) {
        this.emptyCells = emptyCells;
        this.permutation = permutations;
        for (int i = 0; i < 9; i++) {
            rows[i] = Row.getInstance(i);
        }
    }

    @Override
    public boolean validate(Game game) {
        Board.setGrid(game.getGrid());
        boolean status = true;
        for (int i = 0; i < 9; i++) {
            status &= rows[i].scan(this.emptyCells,this.permutation);
        }
        return status;
    }

}
