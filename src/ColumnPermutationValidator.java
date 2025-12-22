import javax.xml.validation.Validator;
import java.util.Map;

public class ColumnPermutationValidator implements ValidationStrategy{
    private Column[] columns = new Column[9];

    public Map<Integer, Integer> emptyCells;
    int[] permutation;

    private static boolean status;

    public ColumnPermutationValidator(Map<Integer, Integer> emptyCells, int[] permutations) {
        this.emptyCells = emptyCells;
        this.permutation = permutations;
        for (int i = 0; i < 9; i++) {
            columns[i] = Column.getInstance(i);
        }
    }

    @Override
    public boolean validate(Game game) {
        Board.setGrid(game.getGrid());
        boolean status = true;
        for (int i = 0; i < 9; i++) {
            status &= columns[i].scan(this.emptyCells,this.permutation);
        }
        return status;
    }

}
