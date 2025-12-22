package view;

import java.io.IOException;

import controller.DifficultyEnum;
import controller.exception.InvalidGameException;
import controller.exception.NotFoundException;
import controller.exception.SolutionInvalidException;
import model.Catalog;
import model.Game;
import model.UserAction;

public interface Controllable {
    Catalog getCatalog();

    Game getGame(DifficultyEnum level) throws NotFoundException;

    void driveGames(Game source) throws SolutionInvalidException;

    String verifyGame(Game game);

    int[] solveGame(Game game) throws InvalidGameException;

    void logUserAction(UserAction action) throws IOException;

    Game loadIncompleteGame(); // <-- Added
}
