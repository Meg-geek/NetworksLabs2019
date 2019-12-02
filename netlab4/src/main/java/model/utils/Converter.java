package model.utils;

public interface Converter<F, T> {
    T convert(F val) throws ConvertionExeption;
    F inverseConvert(T val) throws ConvertionExeption;
}
