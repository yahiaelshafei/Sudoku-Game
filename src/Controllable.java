import java.io.IOException;

public interface Controllable {
    Catalog getCatalog();
    Game getGame(DifficultyEnum level) throws NotFoundException;
    void driveGames(Game source) throws SolutionInvalidException;
    String verifyGame(Game game);
    int[] solveGame(Game game) throws InvalidGameException;
    void logUserAction(UserAction action) throws IOException;
    Game loadIncompleteGame(); // <-- Added
}
