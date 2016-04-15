package es.ubu.lsi.server;

/**Interfaz ChatServer
 * Define la signatura de los métodos de arranque, multidifusión, 
 * eliminación de cliente y apagado.
 * @author Borja Gete
 * @author Plamen Petkov
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
	 * @param message mensaje a retransmitir
	 */
	public void broadcast(ChatMessage message);
	/**
	 * remove method
	 * @param username nick del cliente a eliminar
	 */
	public void remove(String username);
}
