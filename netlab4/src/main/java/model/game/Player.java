package model.game;

public interface Player {
    int getID();
    String getName();
    int getScore();
    void increaseScore(int points);
}
