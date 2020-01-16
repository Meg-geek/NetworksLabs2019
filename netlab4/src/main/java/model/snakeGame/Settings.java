package model.snakeGame;

import model.game.GameSettings;
import model.networkUtils.GameNetworkSettings;

public class Settings implements GameNetworkSettings, GameSettings {
    private int width, height, foodStatic, stateDelayMS;
    private float deadFoodProb, foodPerPlayer;
    private int pingDelayMS, nodeTimeoutMS;

    public Settings(){
        width = fieldWidth.getDefaultValue();
        height = fieldHeight.getDefaultValue();
        foodStatic = fieldFoodStatic.getDefaultValue();
        stateDelayMS = fieldStateDelayMS.getDefaultValue();
        deadFoodProb = fieldFoodProb.getDefaultValue();
        pingDelayMS = pingDelayMSConst.getDefaultValue();
        nodeTimeoutMS = nodeTimeoutMSConst.getDefaultValue();
        foodPerPlayer = fieldFoodPerPlayer.getDefaultValue();
    }

    public Settings(GameSettings gameSettings, GameNetworkSettings gameNetworkSettings){
        this.width = gameSettings.getWidth();
        this.height = gameSettings.getHeight();
        this.foodPerPlayer = gameSettings.getFoodPerPlayer();
        this.deadFoodProb = gameSettings.getDeadFoodProb();
        this.stateDelayMS = gameSettings.getStateDelayMS();
        this.pingDelayMS = gameNetworkSettings.getPingDelayMs();
        this.nodeTimeoutMS = gameNetworkSettings.getNodeTimeoutMs();
        this.foodStatic = gameSettings.getFoodStatic();
    }

    @Override
    public void setWidth(int width) {
        if(width >= fieldWidth.getMinValue() && width <= fieldWidth.getMaxValue()){
            this.width = width;
        }
    }

    @Override
    public void setHeight(int height) {
        if(height >= fieldHeight.getMinValue() && height <= fieldHeight.getMaxValue()){
            this.height = height;
        }
    }

    @Override
    public void setFoodStatic(int foodStatic) {
        if(foodStatic <= fieldFoodStatic.getMaxValue() && foodStatic >= fieldFoodStatic.getMinValue()){
            this.foodStatic = foodStatic;
        }
    }

    @Override
    public void setDeadFoodProb(float deadFoodProb) {
        if(deadFoodProb <= fieldFoodProb.getMaxValue() && deadFoodProb >= fieldFoodProb.getMinValue()){
            this.deadFoodProb = deadFoodProb;
        }
    }

    @Override
    public void setStateDelayMs(int stateDelayMs) {
        if(stateDelayMs <= fieldStateDelayMS.getMaxValue() && stateDelayMs >= fieldStateDelayMS.getMinValue()){
            this.stateDelayMS = stateDelayMs;
        }
    }

    @Override
    public void setFoodPerPlayer(float foodPerPlayer) {
        if(foodPerPlayer <= fieldFoodProb.getMaxValue() && foodPerPlayer >= fieldFoodPerPlayer.getMinValue()){
            this.foodPerPlayer = foodPerPlayer;
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getFoodStatic() {
        return foodStatic;
    }

    @Override
    public float getDeadFoodProb() {
        return deadFoodProb;
    }

    @Override
    public int getStateDelayMS() {
        return stateDelayMS;
    }

    @Override
    public float getFoodPerPlayer() {
        return foodPerPlayer;
    }

    @Override
    public boolean equals(GameSettings gameSettings) {
        return gameSettings.getFoodStatic() == this.foodStatic
                && gameSettings.getStateDelayMS() == this.stateDelayMS
                && gameSettings.getDeadFoodProb() == this.deadFoodProb
                && gameSettings.getHeight() == this.height
                && gameSettings.getWidth() == this.width;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == this){
            return true;
        }
        if(obj instanceof Settings){
            return equals((GameSettings)obj)&&equals((GameNetworkSettings)obj);
        }
        if(obj instanceof GameSettings){
            return equals((GameSettings)obj);
        }
        if(obj instanceof GameNetworkSettings){
            return equals((GameNetworkSettings)obj);
        }
        return super.equals(obj);
    }

    @Override
    public int getPingDelayMs() {
        return pingDelayMS;
    }

    @Override
    public int getNodeTimeoutMs() {
        return nodeTimeoutMS;
    }

    @Override
    public void setPingDelayMs(int pingDelayMs) {
        if(pingDelayMs <= pingDelayMSConst.getMaxValue() && pingDelayMs >= pingDelayMSConst.getMinValue()){
            this.pingDelayMS = pingDelayMs;
        }
    }

    @Override
    public void setNodeTimeoutMs(int nodeTimeoutMs) {
        if(nodeTimeoutMs <= nodeTimeoutMSConst.getMaxValue() && nodeTimeoutMs >= nodeTimeoutMSConst.getMinValue()){
            this.nodeTimeoutMS = nodeTimeoutMs;
        }
    }

    @Override
    public boolean equals(GameNetworkSettings networkSettings) {
        return networkSettings.getPingDelayMs() == this.pingDelayMS &&
                networkSettings.getNodeTimeoutMs() == this.nodeTimeoutMS;
    }
}
