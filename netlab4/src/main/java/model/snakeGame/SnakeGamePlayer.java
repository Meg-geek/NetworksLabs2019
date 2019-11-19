package model.snakeGame;

import model.game.Player;

public class SnakeGamePlayer implements Player {
    private int score;
    private int id;
    private String name;

    public SnakeGamePlayer(int id, String name, int score){
        this.id = id;
        this.name = name;
        this.score = score;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void increaseScore(int points) {
        if(points > 0){
            score+=points;
        }
    }
}
