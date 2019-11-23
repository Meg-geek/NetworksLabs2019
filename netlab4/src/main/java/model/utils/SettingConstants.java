package model.utils;

public class SettingConstants<T> {
    private T minValue, defaultValue, maxValue;

    public SettingConstants(T minValue, T defaultValue, T maxValue){
        this.minValue = minValue;
        this.defaultValue = defaultValue;
        this.maxValue = maxValue;
    }

    T getMinValue(){
        return minValue;
    }

    T getDefaultValue(){
        return defaultValue;
    }

    T getMaxValue(){
        return maxValue;
    }
}
