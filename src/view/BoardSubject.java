package view;

public interface BoardSubject {
    void addObserver(BoardObserver observer);

    void removeObserver(BoardObserver observer);

    void notifyObservers();
}
