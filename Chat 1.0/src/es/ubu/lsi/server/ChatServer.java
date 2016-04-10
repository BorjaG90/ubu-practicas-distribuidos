package es.ubu.lsi.server;

/**Interfaz ChatServer
 * Define la signatura de los m�todos de arranque, multidifusi�n, 
 * eliminaci�n de cliente y apagado.
 * @author Borja Gete & Plamen Petkov
 * @version 1.0.0
 */
import es.ubu.lsi.common.ChatMessage;

public interface ChatServer {
	
	public void startup();
	public void shutdown();
	public void broadcast(ChatMessage message);
	public void remove(String username);
}
