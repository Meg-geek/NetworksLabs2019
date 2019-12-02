package model.snakeGameNetwork.converters;

import me.ippolitov.fit.snakes.SnakesProto;
import model.snakeGame.Settings;
import model.utils.Converter;

public class GameConfigConverter implements Converter<Settings, SnakesProto.GameConfig> {
    @Override
    public SnakesProto.GameConfig convert(Settings settings){
        return null;
    }

    @Override
    public Settings inverseConvert(SnakesProto.GameConfig config){
        Settings settings = new Settings();
        if(config.hasDeadFoodProb()){
            settings.setDeadFoodProb(config.getDeadFoodProb());
        }
        if(config.hasFoodPerPlayer()){
            settings.setFoodPerPlayer(config.getFoodPerPlayer());
        }
        if(config.hasFoodStatic()){
            settings.setFoodStatic(config.getFoodStatic());
        }
        if(config.hasHeight()){
            settings.setHeight(config.getHeight());
        }
        if(config.hasWidth()){
            settings.setWidth(config.getWidth());
        }
        if(config.hasNodeTimeoutMs()){
            settings.setNodeTimeoutMs(config.getNodeTimeoutMs());
        }
        if(config.hasPingDelayMs()){
            settings.setPingDelayMs(config.getPingDelayMs());
        }
        if(config.hasStateDelayMs()){
            settings.setStateDelayMs(config.getStateDelayMs());
        }
        return settings;
    }
}
