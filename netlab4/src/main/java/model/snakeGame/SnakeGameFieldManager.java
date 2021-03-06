package model.snakeGame;

import model.game.*;

import java.util.*;

public class SnakeGameFieldManager implements FieldManager, FieldHelper {
    private GameSettings gameSettings;
    private Map<Integer, SnakeI> idSnakeMap = new HashMap<>();
    private List<Coordinates> foodList = new ArrayList<>();
    private int maxX, maxY;
    private boolean[][] field;
    private static final int NEED_FREE = 5;
    private static final int NOT_FOUND = -5;
    private boolean joinable = true;
    private final int CRASH_POINTS = 1;
    private final int FOOD_POINTS = 1;
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
        if(nextCell.getPointType() == PointType.FOOD){
            foodList.remove(nextCell);
            SnakeI snake = idSnakeMap.get(playerId);
            if(snake != null){
                snake.increaseScore(FOOD_POINTS);
            }
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
        int x = cell.getX(), y = cell.getY();
        switch (direction){
            case DOWN:
                y = getNextInMathRing(y+1, gameSettings.getHeight());
                x = cell.getX();
                break;
            case UP:
                y = getNextInMathRing(y-1, gameSettings.getHeight());
                x = cell.getX();
                break;
            case LEFT:
                x = getNextInMathRing(x-1, gameSettings.getWidth());
                y = cell.getY();
                break;
            case RIGHT:
                x = getNextInMathRing(x+1, gameSettings.getWidth());
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
        int needFoodAmount = gameSettings.getFoodStatic() +
                (int)gameSettings.getFoodPerPlayer() * idSnakeMap.size();
        while(foodList.size() < needFoodAmount &&
                foodList.size() < gameSettings.getHeight()*gameSettings.getWidth() - NEED_FREE*NEED_FREE){
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
        updateFoodList();
    }

    @Override
    public void addSnake(SnakeI snake) {
        SnakeI prevSnake = idSnakeMap.put(snake.getPlayerID(), snake);
        //for debug only
        if(prevSnake != null){
            System.out.println("Illegal snake adding to field manager");
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
                snakesBody.add(new Point(getNextInMathRing(freeRowsBegin.getX() + 2, field[0].length),
                                        getNextInMathRing(freeRowsBegin.getY() - 2, field[0].length),
                                            PointType.SNAKE_BODY));
                int[] coefArray = getRandDirection();
                snakesBody.add(new Point(getNextInMathRing(freeRowsBegin.getX() + 2 + coefArray[0], field[0].length),
                                getNextInMathRing(freeRowsBegin.getY() - 2 + coefArray[1], field[0].length),
                        PointType.SNAKE_BODY));
            }
        }
        return snakesBody;
    }

    private int[] getRandDirection(){
        int[] coefArray = new int[2];
        int randInt = new Random().nextInt();
        if(randInt % 4 == 0) {
            coefArray[0] = 1;
        } else if (randInt % 3 == 0){
            coefArray[0] = -1;
        } else if(randInt % 2 == 0){
            coefArray[1] = 1;
        } else {
            coefArray[1] = -1;
        }
        return coefArray;
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
        if(index < 0){
            return getNextInMathRing(index + base, base);
        }
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
