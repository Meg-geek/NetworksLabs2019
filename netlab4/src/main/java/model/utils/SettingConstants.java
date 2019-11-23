package model.utils;

public class SettingConstants<T> {
    private T minValue, defaultValue, maxValue;

    public SettingConstants(T minValue, T defaultValue, T maxValue){
        this.minValue = minValue;
        this.defaultValue = defaultValue;
        this.maxValue = maxValue;
    }

    public T getMinValue(){
        return minValue;
    }

    public T getDefaultValue(){
        return defaultValue;
    }

    public T getMaxValue(){
        return maxValue;
    }
}
