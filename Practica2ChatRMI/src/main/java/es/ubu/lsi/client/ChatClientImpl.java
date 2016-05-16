package es.ubu.lsi.client;

import java.rmi.RemoteException;

import es.ubu.lsi.common.ChatMessage;
/**
 * Clase ChatClientImpl
 * @author Borja Gete
 * @author Plamen Peytov
 *
 */
public class ChatClientImpl implements ChatClient {
	private int id;
	private String nick;
	/**
	 * Constructor de la clase ChatClientImpl
	 * @param nick Apodo del Cliente
	 * @throws RemoteException Excepción remota surgida en la comunicación
	 */
	public ChatClientImpl(String nick) throws RemoteException {
		this.nick=nick;
	}
	/**
	 * Devuelve el identificador del Cliente
	 * @return id Identificador del Cliente
	 * @throws RemoteException Excepción remota surgida en la comunicación
	 */
	public int getId() throws RemoteException {
		return this.id;
	}
	/**
	 * Establece el identificador del Cliente
	 * @param id Identificador del Cliente
	 * @throws RemoteException Excepción remota surgida en la comunicación
	 */
	public void setId(int id) throws RemoteException {
		this.id = id;
	}
	/**
	 * Recibe y muestra un mensaje con un determinado formato:
	 * 
	 * Apodo:
	 * '-> Mensaje
	 * 
	 * @param msg Mensaje recibido
	 * @throws RemoteException Excepción remota surgida en la comunicación
	 */
	public void receive(ChatMessage msg) throws RemoteException {
		System.out.println(msg.getNickname()+":\n'-> "+msg.getMessage());
	}
	/**
	 * Devuelve el apodo del Cliente
	 * @return nick Apodo del Cliente
	 * @throws RemoteException Excepción remota surgida en la comunicación
	 */
	public String getNickName() throws RemoteException {
		return this.nick;
	}

}
