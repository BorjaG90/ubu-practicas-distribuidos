package es.ubu.lsi.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.ubu.lsi.common.CaesarCipher;
import es.ubu.lsi.common.ChatMessage;

/**
 * Clase ChatClientImpl
 * 
 * @author Borja Gete
 * @author Plamen Peytov
 *
 */
public class ChatClientImpl extends UnicastRemoteObject implements ChatClient {

	private static final long serialVersionUID = -677605033768763775L;
	
	private int id;
	private String nickname;
	private final int password;
	private SimpleDateFormat sdf;

	/**
	 * Constructor de la clase ChatClientImpl
	 * 
	 * @param nickname
	 *            Apodo del Cliente
	 * @param password
	 *            contraseña para encriptar los mensajes
	 * @throws RemoteException
	 *             Excepción remota surgida en la comunicación
	 */
	public ChatClientImpl(String nickname, int password) throws RemoteException {
		super();
		this.nickname = nickname;
		this.password = password;
		sdf = new SimpleDateFormat("HH:mm:ss");
	}

	/**
	 * Devuelve el identificador del Cliente
	 * 
	 * @return id Identificador del Cliente
	 * @throws RemoteException
	 *             Excepción remota surgida en la comunicación
	 */
	public int getId() throws RemoteException {
		return this.id;
	}

	/**
	 * Establece el identificador del Cliente
	 * 
	 * @param id
	 *            Identificador del Cliente
	 * @throws RemoteException
	 *             Excepción remota surgida en la comunicación
	 */
	public void setId(int id) throws RemoteException {
		this.id = id;
	}

	/**
	 * Recibe y muestra un mensaje con un determinado formato:
	 * 
	 * @param msg
	 *            Mensaje recibido
	 * @throws RemoteException
	 *             Excepción remota surgida en la comunicación
	 */
	public void receive(ChatMessage msg) throws RemoteException {
		
		String text = msg.getMessage();
		if(msg.isEncrypted())
			text = CaesarCipher.decrypt(text, this.password);
		
		System.out.println("[" + sdf.format(new Date()) + "]"
				+ msg.getNickName() + ": " + text);
		System.out.print(">>>");
	}

	/**
	 * Devuelve el apodo del Cliente
	 * 
	 * @return nick Apodo del Cliente
	 * @throws RemoteException
	 *             Excepción remota surgida en la comunicación
	 */
	public String getNickName() throws RemoteException {
		return this.nickname;
	}

	public int getPassword() throws RemoteException {
		return this.password;
	}
	
}
