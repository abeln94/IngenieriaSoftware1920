package es.unizar.eina.send;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

/**
 * Implementa la interfaz de la abstraccion utilizando (delegando a) una referencia a un objeto de tipo implementor
 */
public class SendAbstractionImpl implements SendAbstraction {

    /**
     * objeto delegado que facilita la implementacion del metodo send
     */
    private SendImplementor implementor;

    /**
     * Tipos de envíos disponibles, para el constructor
     */
    public enum TYPES {SMS, EMAIL}

    /**
     * Constructor de la clase. Inicializa el objeto delegado
     *
     * @param sourceActivity actividad desde la cual se abrira la actividad encargada de enviar la nota
     * @param method        parametro potencialmente utilizable para instanciar el objeto delegado
     */
    public SendAbstractionImpl(AppCompatActivity sourceActivity, TYPES method) {
        switch (method) {
            case SMS:
                implementor = new SMSImplementor(sourceActivity);
                break;
            case EMAIL:
                implementor = new MailImplementor(sourceActivity);
                break;
        }
    }

    /**
     * Envia la correo con el asunto (subject) y cuerpo (body) que se reciben como parametros a traves de un objeto delegado
     *
     * @param subject asunto
     * @param body    cuerpo del mensaje
     */
    public void send(String subject, String body) {
        implementor.send(subject, body);
    }
}
