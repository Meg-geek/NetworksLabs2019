package model.snakeGame;

import model.game.*;

import java.util.*;

public class SnakeGameFieldManager implements FieldManager {
    private GameSettings gameSettings;
   // private List<SnakeI> snakesList;
    private Map<Integer, List<Coordinates>> playerSnakeCoordsMap = new HashMap<>();
    private List<Coordinates> foodList;
    private int maxX, maxY;
    private boolean[][] field;
    private static final int NEED_FREE = 5;

    SnakeGameFieldManager(GameSettings gameSettings){
        this.gameSettings = gameSettings;
        maxX = gameSettings.getWidth() + (FIRST_COORD -1);
        maxY = gameSettings.getHeight() + (FIRST_COORD - 1);
        field = new boolean[gameSettings.getWidth()][gameSettings.getHeight()];
    }

    @Override
    public Coordinates getNextCell(Coordinates cell, Direction direction) {
        int x = 0, y = 0;
        switch (direction){
            case DOWN:
                if(cell.getY() < maxY) {
                    y = cell.getY() + 1;
                } else {
                    y = FIRST_COORD;
                }
                x = cell.getX();
                break;
            case UP:
                if(cell.getY() == FIRST_COORD) {
                    y = gameSettings.getHeight();
                } else {
                    y = cell.getY() + 1;
                }
                x = cell.getX();
                break;
            case LEFT:
                if(cell.getX() == FIRST_COORD) {
                    x = gameSettings.getWidth();
                } else {
                    x = cell.getX() + 1;
                }
                y = cell.getY();
                break;
            case RIGHT:
                if(cell.getY() < maxX) {
                    x = cell.getX() + 1;
                } else {
                    x = FIRST_COORD;
                }
                y = cell.getY();
                break;
        }
        Coordinates nextCell = new Point(x, y);
        setPointType(nextCell);
        return nextCell;
    }

    @Override
    public List<Coordinates> getNewSnakeCoords(int playerID) {
        if(playerSnakeCoordsMap.containsKey(playerID)){
            return null;
        }
        List<Coordinates> snakeBody = findSnakeCoordinates();
        if(snakeBody == null){
            return null;
        }
        playerSnakeCoordsMap.put(playerID, snakeBody);
        return snakeBody;
    }

    private List<Coordinates> findSnakeCoordinates(){
        List<Coordinates> snakesBody = null;
        updateField();
        for(int i = 0; i < field.length && snakesBody == null; i++){
            Coordinates freeRowsBegin = findFreeRows(i);
            if(freeRowsBegin != null){
                snakesBody = new ArrayList<>();
                snakesBody.add(new Point(freeRowsBegin.getX() + 2,
                                        freeRowsBegin.getY() - 2,
                                            PointType.SNAKE_BODY));
                snakesBody.add(new Point(freeRowsBegin.getX() + 2 + getRand(),
                        freeRowsBegin.getY() - 2 + getRand(),
                        PointType.SNAKE_BODY));
            }
        }
        return snakesBody;
    }

    private int getRand(){
        if(new Date().getTime() % 2 == 0) {
            return 1;
        }
        return -1;
    }

    private Coordinates findFreeRows(int rowNumb){
        Coordinates coordinates = null;
        int base = field[rowNumb].length;
        int firstX = -1;
        for(int i = 0; i < base + NEED_FREE && coordinates == null; i++){
            int x = getNextInMathRing(i, base);
            if(field[rowNumb][x]){
                int j;
                for(j = 1; j <= NEED_FREE; j++){
                    if(!field[getNextInMathRing(rowNumb + j, base)][x]){
                        break;
                    }
                }
                if(j != NEED_FREE){
                    firstX = -1;
                } else {
                    if(firstX == -1){
                        firstX = x;
                    }
                }
            }
            if(firstX != -1 && Math.abs(x - firstX) == 4){
                coordinates = new Point(firstX, rowNumb);
            }
        }
        return coordinates;
    }

    private int getNextInMathRing(int index, int base){
        if(index < base){
            return index;
        }
        if(index == base){
            return 0;
        }
        return index % base;
    }

    private void updateField(){
        for(boolean[] row : field){
            Arrays.fill(row, true);
        }
        for(Map.Entry<Integer, List<Coordinates>> idCoord : playerSnakeCoordsMap.entrySet()){
            for(Coordinates coord : idCoord.getValue()){
                field[coord.getX()][coord.getY()] = false;
            }
        }
        for(Coordinates coordinates : foodList){
            field[coordinates.getX()][coordinates.getY()] = false;
        }
    }

    private void setPointType(Coordinates cell){
        if(foodList.contains(cell)){
            cell.setPointType(PointType.FOOD);
        }

    }
}
