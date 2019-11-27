package model.game;

import model.utils.SettingConstants;

public interface GameSettings {
    /*
    int DEFAULT_FIELD_WIDTH = 40;
    int MIN_FIELD_WIDTH = 10;
    int MAX_FIELD_WIDTH = 100;
    int DEFAULT_FIELD_HEIGHT = 30;
    int DEFAULT_FOOD_STATIC = 1;
    int DEFAULT_STATE_DELAY_MS = 1000;
    float DEFAULT_DEAD_FOOD_PROB = 0.1F;
     */
    SettingConstants<Integer> fieldWidth = new SettingConstants<>(10, 40, 100);
    SettingConstants<Integer> fieldHeight = new SettingConstants<>(10, 30, 100);
    SettingConstants<Integer> fieldFoodStatic = new SettingConstants<>(0, 1, 100);
    SettingConstants<Integer> fieldStateDelayMS = new SettingConstants<>(1, 1000, 1000);
    SettingConstants<Float> fieldFoodProb = new SettingConstants<>(0F, 0.1F, 1F);

    void setWidth(int width);
    void setHeight(int height);
    void setFoodStatic(int foodStatic);
    void setDeadFoodProb(float deadFoodProb);
    void setStateDelayMs(int stateDelayMs);

    int getWidth();
    int getHeight();
    int getFoodStatic();
    float getDeadFoodProb();
    int getStateDelayMS();

    boolean equals(GameSettings gameSettings);
}
