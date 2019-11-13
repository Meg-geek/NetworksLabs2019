package model.game;

public interface GameSettings {
    int DEFAULT_FIELD_WIDTH = 40;
    int DEFAULT_FIELD_HEIGHT = 30;
    int DEFAULT_FOOD_STATIC = 1;
    float DEFAULT_DEAD_FOOD_PROB = 0.1F;

    //need setters?
    void setWidth(int width);
    void setHeight(int height);
    void setFoodStatic(int foodStatic);
    void setDeadFoodProb(int deadFoodProb);

    int getWidth();
    int getHeight();
    int getFoodStatic();
    int getDeadFoodProb();
}
