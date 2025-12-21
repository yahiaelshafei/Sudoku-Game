public class RowValidation implements ValidationStrategy {

    @Override
    public boolean validate(Game game) {
        Board.setGrid(game.getGrid());
        RowManager manager = new RowManager();
        manager.run();
        return RowManager.getStatus();
    }
}
