import java.io.IOException;
public interface Viewable {
Catalog getCatalog();

    Game getGame(DifficultyEnum level) throws NotFoundException;

    void driveGames(Game source) throws SolutionInvalidException;

    String verifyGame(Game game);

    int[] solveGame(Game game) throws InvalidGameException;

    void logUserAction(String userAction) throws IOException; 
}
