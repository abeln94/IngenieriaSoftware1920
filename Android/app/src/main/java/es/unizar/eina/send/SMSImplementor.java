package es.unizar.eina.send;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;

/**
 * Permite enviar elementos a traves de SMS
 */
public class SMSImplementor implements SendImplementor {

    /**
     * actividad desde la cual se abrirá la actividad de gestión de correo
     */
    private AppCompatActivity sourceActivity;

    /**
     * Constructor
     *
     * @param source actividad desde la cual se abrirá la actividad de gestión de correo
     */
    public SMSImplementor(AppCompatActivity source) {
        setSourceActivity(source);
    }

    /**
     * Actualiza la actividad desde la cual se abrirá la actividad de gestión de correo
     */
    public void setSourceActivity(AppCompatActivity source) {
        sourceActivity = source;
    }

    /**
     * Recupera la actividad desde la cual se abrirá la actividad de gestión de correo
     */
    public AppCompatActivity getSourceActivity() {
        return sourceActivity;
    }

    /**
     * Implementación del método send utilizando la aplicación de gestión de correo de Android
     * Solo se copia el asunto y el cuerpo
     *
     * @param subject asunto
     * @param body    cuerpo del mensaje
     */
    public void send(String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra("sms_body", subject + ": " + body);
        intent.setType("vnd.android-dir/mms-sms");
        getSourceActivity().startActivity(intent);
    }

}
