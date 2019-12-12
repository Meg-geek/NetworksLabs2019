package model.snakeGame;

import model.game.*;

import java.util.*;

public class SnakeGameFieldManager implements FieldManager, FieldHelper {
    private GameSettings gameSettings;
    private Map<Integer, SnakeI> idSnakeMap = new HashMap<>();
    private List<Coordinates> foodList = null;
    private int maxX, maxY;
    private boolean[][] field;
    private static final int NEED_FREE = 5;
    private static final int NOT_FOUND = -5;
    private boolean joinable = true;
    private final int CRASH_POINTS = 1;
    private final int SNAKE_NOT_FOUND = -4;
    private List<ProblemPoint> problemPointList = new ArrayList<>();

    SnakeGameFieldManager(GameSettings gameSettings){
        this.gameSettings = gameSettings;
        maxX = gameSettings.getWidth() + (FIRST_COORD -1);
        maxY = gameSettings.getHeight() + (FIRST_COORD - 1);
        field = new boolean[gameSettings.getWidth()][gameSettings.getHeight()];
    }

    @Override
    public Coordinates getNextCell(Coordinates cell, Direction direction, int playerId) {
        Coordinates nextCell = getNextCell(cell, direction);
        if(nextCell.getPointType() == PointType.SNAKE_BODY){
            SnakeI snake = idSnakeMap.get(getSnakeId(nextCell));
            if(snake != null){
                snake.increaseScore(CRASH_POINTS);
            }
        }
        if(nextCell.getPointType() == PointType.SNAKE_TAIL){
            problemPointList.add(new ProblemPoint(playerId, getSnakeId(nextCell), nextCell));
        }
        return nextCell;
    }

    private int getSnakeId(Coordinates snakePoint){
        for(SnakeI snake : idSnakeMap.values()){
            if(snake.getCoordinatesList().contains(snakePoint)){
                return snake.getPlayerID();
            }
        }
        return SNAKE_NOT_FOUND;
    }

    private Coordinates getNextCell(Coordinates cell, Direction direction){
        int x = FIRST_COORD, y = FIRST_COORD;
        switch (direction){
            case DOWN:
                if(cell.getY() < maxY) {
                    y = cell.getY() + 1;
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
                }
                y = cell.getY();
                break;
        }
        Coordinates nextCell = new Point(x, y);
        nextCell.setPointType(getPointType(nextCell));
        return nextCell;
    }

    @Override
    public List<Coordinates> getNewSnakeCoords(int playerID) {
        if(idSnakeMap.containsKey(playerID)){
            return null;
        }
        List<Coordinates> snakeBody = findSnakeCoordinates();
        if(snakeBody == null){
            joinable = false;
            return null;
        }
        updateFoodList();
        //playerSnakeCoordsMap.put(playerID, snakeBody);
        return snakeBody;
    }

    private void updateFoodList(){
        if(foodList == null){
            setFood();
        }
        int needFoodAmount = gameSettings.getFoodStatic() +
                (int)gameSettings.getFoodPerPlayer() * idSnakeMap.size();
        while(foodList.size() < needFoodAmount){
            Coordinates coordinates = getEmptyCell();
            coordinates.setPointType(PointType.FOOD);
            foodList.add(coordinates);
        }
    }

    @Override
    public boolean isJoinable() {
        return joinable;
    }

    @Override
    public List<Coordinates> getFoodList() {
        return foodList;
    }

    @Override
    public void setFoodList(List<Coordinates> foodList) {
        this.foodList = foodList;
    }

    @Override
    public void checkSnakes() {
        for(ProblemPoint problemPoint : problemPointList){
            SnakeI snake = idSnakeMap.get(problemPoint.getIdTail());
            if(snake != null){
                if(snake.getCoordinatesList().contains(problemPoint.getSnakePoint())){
                    SnakeI snakeDead = idSnakeMap.get(problemPoint.getIdNewHead());
                    if(snakeDead != null){
                        snakeDead.setDead();
                    }
                }
            }
        }
        problemPointList.clear();
    }

    @Override
    public void addSnake(SnakeI snake) {
        SnakeI prevSnake = idSnakeMap.put(snake.getPlayerID(), snake);
        //for debug only
        if(prevSnake != null){
            System.out.println("Illegal snake adding to field manager");
        }
    }

    //if we start new game

    private void setFood() {
        if(foodList == null){
            foodList = new ArrayList<>();
            int foodAmount = gameSettings.getFoodStatic() + (int)gameSettings.getFoodPerPlayer();
            for(int i = 0; i < foodAmount; i++){
                Coordinates coord = getEmptyCell();
                coord.setPointType(PointType.FOOD);
                foodList.add(coord);
            }
        }
    }

    private Coordinates getEmptyCell(){
        int x = FIRST_COORD + (int) (Math.random() * (maxX - FIRST_COORD));
        int y = FIRST_COORD + (int) (Math.random() * (maxY - FIRST_COORD));
        Coordinates coordinates = new Point(x, y);
        if(getPointType(coordinates) == PointType.EMPTY_CELL){
            coordinates.setPointType(PointType.EMPTY_CELL);
            return coordinates;
        }
        return getEmptyCell();
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
                snakesBody.add(new Point(freeRowsBegin.getX() + 2 + getRandSign(),
                        freeRowsBegin.getY() - 2 + getRandSign(),
                        PointType.SNAKE_BODY));
            }
        }
        return snakesBody;
    }

    private int getRandSign(){
        if(new Date().getTime() % 2 == 0) {
            return 1;
        }
        return -1;
    }

    private Coordinates findFreeRows(int rowNumb){
        Coordinates coordinates = null;
        int base = field[rowNumb].length;
        int firstX = NOT_FOUND;
        for(int i = 0; i < base + NEED_FREE && coordinates == null; i++){
            int x = getNextInMathRing(i, base);
            if(field[rowNumb][x]){
                int j;
                for(j = 1; j <= NEED_FREE; j++){
                    if(!field[getNextInMathRing(rowNumb + j, base)][x]){
                        j = NOT_FOUND;
                        break;
                    }
                }
                if(j == NOT_FOUND){
                    firstX = NOT_FOUND;
                } else {
                    if(firstX == NOT_FOUND){
                        firstX = x;
                    }
                }
            }
            if(firstX != NOT_FOUND && Math.abs(x - firstX) == NEED_FREE - 1){
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
        for(Map.Entry<Integer, SnakeI> idSnake : idSnakeMap.entrySet()){
            for(Coordinates coord : idSnake.getValue().getCoordinatesList()){
                field[coord.getX()][coord.getY()] = false;
            }
        }
        for(Coordinates coordinates : foodList){
            field[coordinates.getX()][coordinates.getY()] = false;
        }
    }

    private PointType getPointType(Coordinates cell){
        if(foodList.contains(cell)){
            return PointType.FOOD;
        }
        for(SnakeI snake : idSnakeMap.values()){
            List<Coordinates> snakeBodyCoordinatesList = snake.getCoordinatesList();
            if(snakeBodyCoordinatesList.contains(cell)){
                if(snakeBodyCoordinatesList.get(snakeBodyCoordinatesList.size() - 1).equals(cell)){
                    return PointType.SNAKE_TAIL;
                }
                return PointType.SNAKE_BODY;
            }
        }
        return PointType.EMPTY_CELL;
    }
}

class ProblemPoint{
    private int idTail, idNewHead;
    private Coordinates snakePoint;

    ProblemPoint(int idNewHead, int idTail, Coordinates snakePoint){
        this.idNewHead = idNewHead;
        this.idTail = idTail;
        this.snakePoint = snakePoint;
    }

    int getIdNewHead() {
        return idNewHead;
    }

    int getIdTail() {
        return idTail;
    }

    Coordinates getSnakePoint(){
        return snakePoint;
    }
}
