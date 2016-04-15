package es.ubu.lsi.client;

/**Interfaz ChatClient
 * Define la signatura de los métodos de envío de mensaje, 
 * desconexión y arranque
 * @author Borja Gete
 * @author Plamen Petkov
 * @version 1.0.0
 */
import es.ubu.lsi.common.ChatMessage;

public interface ChatClient {
	
	/**
	 * Método start. Inicia el cliente.
	 * @return true si no ha habido error, false en caso contrario.
	 */
	public boolean start();
	
	/**
	 * Método sendMessage. Permite enviar un mensaje al servidor.
	 * @param msg el mensaje a enviar.
	 */
	public void sendMessage(ChatMessage msg);
	
	/**
	 * Método disconnect. Desconecta el cliente del servidor.
	 */
	public void disconnect();
}
