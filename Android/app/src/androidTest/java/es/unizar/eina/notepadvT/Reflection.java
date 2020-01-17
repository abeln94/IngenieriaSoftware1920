package es.unizar.eina.notepadvT;

import java.lang.reflect.Field;

/**
 * For testing purposes
 */
public class Reflection {
    static public <T> T getPrivate(Object object, String value){

        try {
            // set the field to accesible
            Field privateField = object.getClass().getDeclaredField(value);
            privateField.setAccessible(true);
            return (T) privateField.get(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
