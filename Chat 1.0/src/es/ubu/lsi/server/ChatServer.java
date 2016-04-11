package es.ubu.lsi.server;

/**Interfaz ChatServer
 * Define la signatura de los m�todos de arranque, multidifusi�n, 
 * eliminaci�n de cliente y apagado.
 * @author Borja Gete & Plamen Petkov
 * @version 1.0.0
 */
import es.ubu.lsi.common.ChatMessage;

public interface ChatServer {
	/**
	 * startup method
	 */
	public void startup();
	/**
	 * shutdown method
	 */
	public void shutdown();
	/**
	 * broadcast method
	 * @param message
	 */
	public void broadcast(ChatMessage message);
	/**
	 * remove method
	 * @param username
	 */
	public void remove(String username);
}
