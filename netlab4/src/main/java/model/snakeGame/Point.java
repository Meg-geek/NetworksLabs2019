package model.snakeGame;

import model.game.Coordinates;
import model.game.PointType;

public class Point implements Coordinates {
    private int x;
    private int y;
    private PointType pointType;

    public Point(int x, int y){
        this.x = x;
        this.y = y;
        pointType = null;
    }

    public Point(int x, int y, PointType pointType){
        this(x, y);
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

    @Override
    public void setPointType(PointType pointType) {
        this.pointType = pointType;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == this){
            return true;
        }
        if(!(obj instanceof Point)){
            return false;
        }
        Coordinates point = (Coordinates) obj;
        return this.x == point.getX() && this.y == point.getY();
    }
}
