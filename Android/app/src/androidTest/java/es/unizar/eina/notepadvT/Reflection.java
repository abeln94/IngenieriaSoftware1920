package es.unizar.eina.notepadvT;

import java.lang.reflect.Field;

public class Reflection {
    static public <T> T getPrivate(Object object, String value){
        Field privateField;

        try {
            // set the field to accesible
            privateField = object.getClass().getDeclaredField(value);
            privateField.setAccessible(true);
            return (T) privateField.get(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
