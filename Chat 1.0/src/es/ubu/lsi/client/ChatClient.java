package es.ubu.lsi.client;

/**Interfaz ChatClient
 * Define la signatura de los m�todos de env�o de mensaje, 
 * desconexi�n y arranque
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
