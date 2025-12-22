package controller;

import java.io.IOException;

import controller.exception.InvalidGameException;
import controller.exception.NotFoundException;
import controller.exception.SolutionInvalidException;
import model.Catalog;
import model.Game;

public interface Viewable {
    Catalog getCatalog();

    Game getGame(DifficultyEnum level) throws NotFoundException;

    void driveGames(Game source) throws SolutionInvalidException;

    String verifyGame(Game game);

    int[] solveGame(Game game) throws InvalidGameException;

    void logUserAction(String userAction) throws IOException;
}
