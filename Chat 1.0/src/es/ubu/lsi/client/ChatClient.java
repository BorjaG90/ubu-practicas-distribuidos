package es.ubu.lsi.client;

/**Interfaz ChatClient
 * Define la signatura de los métodos de envío de mensaje, 
 * desconexión y arranque
 * @author Borja Gete & Plamen Petkov
 * @version 1.0.0
 */
import es.ubu.lsi.common.ChatMessage;

public interface ChatClient {
	/**
	 * start method
	 * @return 
	 */
	public boolean start();
	/**
	 * sendMessage method
	 * @param msg
	 */
	public void sendMessage(ChatMessage msg);
	/**
	 * disconnect method
	 */
	public void disconnect();
}
