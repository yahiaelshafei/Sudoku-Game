
import java.io.IOException;
public interface Controllable {
    Catalog getCatalog();

    int[][] getGame(char level) throws NotFoundException;

    void driveGames(int[][] source) throws SolutionInvalidException;

    boolean[][] verifyGame(int[][] game);

    int[][] solveGame(int[][] game) throws InvalidGameException;

    void logUserAction(UserAction action) throws IOException;
}

