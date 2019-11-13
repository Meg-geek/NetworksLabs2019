package model.snakeGame;

import model.game.Coordinates;
import model.game.PointType;

public class Point implements Coordinates {
    private int x;
    private int y;
    private PointType pointType;

    Point(int x, int y, PointType pointType){
        this.x = x;
        this.y = y;
        this.pointType = pointType;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public PointType getPointType() {
        return pointType;
    }
}
