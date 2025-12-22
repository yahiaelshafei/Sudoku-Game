import java.util.Map;

public class BoxPermutationValidator implements ValidationStrategy{
    private Box[] boxes = new Box[9];

    public Map<Integer, Integer> emptyCells;
    int[] permutation;

    private static boolean status;

    public BoxPermutationValidator(Map<Integer, Integer> emptyCells, int[] permutations) {
        this.emptyCells = emptyCells;
        this.permutation = permutations;
        for (int i = 0; i < 9; i++) {
            boxes[i] = Box.getInstance(i);
        }
    }

    @Override
    public boolean validate(Game game) {
        Board.setGrid(game.getGrid());
        boolean status = true;
        for (int i = 0; i < 9; i++) {
            status &= boxes[i].scan(this.emptyCells,this.permutation);
        }
        return status;
    }

}
