package es.unizar.eina.send;

/** Define la interfaz de la abstraccion */
public interface SendAbstraction {

	/** Definici�n del metodo que permite enviar la nota con el asunto (subject) y cuerpo (body) que se reciben como parametros
     * @param subject asunto
     * @param body cuerpo del mensaje
     */
	void send(String subject, String body);
}
