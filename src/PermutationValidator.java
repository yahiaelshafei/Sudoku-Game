import java.util.List;
import java.util.Map;

public class PermutationValidator  extends Thread{

    public Map<Integer, Integer> emptyCells;
    int[] permutations;
    Game game;

    public PermutationValidator(Map<Integer, Integer> emptyCells, int[] permutations,Game game){
        this.emptyCells = emptyCells;
        this.permutations = permutations;
        this.game = game;
    }


    @Override
    public void run(){
        // Strategy-based validation
        boolean status = true;
        List<ValidationStrategy> strategies = List.of(
                new RowPermutationValidator(emptyCells, permutations),
                new ColumnPermutationValidator(emptyCells, permutations),
                new BoxPermutationValidator(emptyCells,permutations)
        );

        for (ValidationStrategy strategy : strategies) {
            if (!strategy.validate(game)) status = false;
        }

        if(status)
        {
            GameDriver.solverStatus = true;
            GameDriver.solverSolution = permutations;
        }
    }
}
