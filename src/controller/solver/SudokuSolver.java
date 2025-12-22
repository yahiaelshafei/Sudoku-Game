package controller.solver;

import model.*;
import java.util.*;
import controller.exception.*;

public class SudokuSolver {

    private final BoardVerifier verifier;
    private static final int MAX_EMPTY_CELLS = 5;

    public SudokuSolver() {
        this.verifier = new BoardVerifier();
    }

    public int[] solve(Game game) throws InvalidGameException {
        int emptyCount = game.countEmptyCells();

        if (emptyCount == 0) {
            throw new InvalidGameException("Puzzle is already complete");
        }

        if (emptyCount > MAX_EMPTY_CELLS) {
            throw new InvalidGameException(
                    "Solver only works with up to " + MAX_EMPTY_CELLS + 
                    " empty cells. Found: " + emptyCount);
        }

        Map<Integer, Integer> emptyCells = game.getEmptyCells();
        Iterator permutationIterator = new PermutationGenerator(emptyCount);

        while (permutationIterator.hasNext()) {
            int[] permutation = (int[]) permutationIterator.next();

            if (verifier.verify(game.getGrid(), emptyCells, permutation)) {
                return buildSolution(emptyCells, permutation);
            }
        }

        throw new InvalidGameException(
                "No valid solution found");
    }


    private int[] buildSolution(Map<Integer, Integer> emptyCells, int[] permutation) {
        int[] solution = new int[emptyCells.size() * 3];

        for (int i = 0; i < permutation.length; i++) {
            for (Map.Entry<Integer, Integer> entry : emptyCells.entrySet()) {
                if (entry.getValue() == i) {
                    int key = entry.getKey();
                    solution[i * 3] = key / 10;
                    solution[i * 3 + 1] = key % 10;
                    solution[i * 3 + 2] = permutation[i];
                    break;
                }
            }
        }

        return solution;
    }

    public boolean isSolvable(Game game) {
        try {
            solve(game);
            return true;
        } catch (InvalidGameException e) {
            return false;
        }
    }
}