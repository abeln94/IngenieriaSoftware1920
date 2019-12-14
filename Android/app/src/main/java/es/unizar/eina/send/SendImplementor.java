package es.unizar.eina.send;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

/**
 * Define la interfaz para las clases de la implementacion.
 * La interfaz no se tiene que corresponder directamente con la interfaz de la abstraccion.
 */
public interface SendImplementor {

    /**
     * Actualiza la actividad desde la cual se abrira la actividad de envío de notas
     */
    void setSourceActivity(AppCompatActivity source);

    /**
     * Recupera la actividad desde la cual se abrira la actividad de envio de notas
     */
    AppCompatActivity getSourceActivity();

    /**
     * Permite lanzar la actividad encargada de gestionar el envio de notas
     */
    void send(String subject, String body);

}
