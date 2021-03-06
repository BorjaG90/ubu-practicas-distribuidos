package es.ubu.lsi.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.ubu.lsi.client.ChatClient;
import es.ubu.lsi.common.CaesarCipher;
import es.ubu.lsi.common.ChatMessage;

/**
 * Clase ChatServerImpl
 * 
 * @author Borja Gete
 * @author Plamen Peytov
 *
 */
public class ChatServerImpl extends UnicastRemoteObject implements ChatServer {

	private static final long serialVersionUID = 1787550739624971220L;
	
	private Map<String, ChatClient> clients;
	private int clientID = 0;
	private SimpleDateFormat sdf;


	/**
	 * Constructor de la clase ChatServerImpl
	 * @throws RemoteException
	 *             Excepción remota surgida en la comunicación
	 */
	public ChatServerImpl() throws RemoteException {
		super();
		clients = new HashMap<String, ChatClient>();
		sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println("[" + sdf.format(new Date()) + "] Server started!");
	}

	/**
	 * Permite a un cliente acceder al chat
	 * 
	 * @param client
	 *            Cliente que solicita acceso
	 * @return Identificador del cliente
	 * @throws RemoteException
	 *             Excepción remota surgida en la comunicación
	 */
	public int checkIn(ChatClient client) throws RemoteException {
		String text;
		if (clients.containsKey(client.getNickName())) {
			text = "User already exists with nickname " + client.getNickName();
			client.receive(new ChatMessage(0, "Server", text));
			return 0;
		} else {
			clients.put(client.getNickName(), client);
			clientID++;

			System.out.println("[" + sdf.format(new Date()) + "] Connected client "
					+ client.getNickName() + " with ID " + clientID);
			text = "Welcome to Chat RMI 1.0! You are now connected as "
					+ client.getNickName();
			
			privatemsg(client.getNickName(), new ChatMessage(clientID, "Server", text));
			return clientID;
		}
	}

	/**
	 * Echa/Desloguea a un cliente
	 * 
	 * @param client
	 *            Cliente al cual se termina la conexion
	 * @throws RemoteException
	 *             Excepción remota surgida en la comunicación
	 */
	public void logout(ChatClient client) throws RemoteException {
		String text = "You have been disconnected!";
		ChatMessage msg = new ChatMessage(client.getId(), "Server",
				"You have been disconnected!");
		
		privatemsg(client.getNickName(), msg);
		
		clients.remove(client.getNickName());
		clientID--;
		
		text = client.getNickName() + " has logged out!";
		msg.setNickName("Server");
		msg.setMessage(text);
		publish(msg);
		
		System.out.println("[" + sdf.format(new Date()) + "] Disconnected client "
				+ client.getNickName() + " with ID " + client.getId());
	}

	/**
	 * Permite al destinatario la recepcion de un mensaje privado
	 * 
	 * @param tonickname
	 *            Cliente que recibe el mensaje privado
	 * @param msg
	 *            Mensaje privado
	 * @throws RemoteException
	 *             Excepción remota surgida en la comunicación
	 */
	public void privatemsg(String tonickname, ChatMessage msg) throws RemoteException {
		clients.get(tonickname).receive(msg);
	}

	/**
	 * Publica un mensaje recibido
	 * 
	 * @param msg
	 *            Mensaje a publicar
	 * @throws RemoteException
	 *             Excepción remota surgida en la comunicación
	 */
	public void publish(ChatMessage msg) throws RemoteException {
		
		String text = msg.getMessage();
		String nickname = msg.getNickName();
		
		if(msg.isEncrypted()) {
			ChatClient client = clients.get(nickname);
			text = CaesarCipher.decrypt(text, client.getPassword());
			System.out.println("DECRYPTED MSG: " + text);
		}
		
		for (ChatClient c : clients.values()) {
			if (!c.getNickName().equals(nickname)) {
				if(msg.isEncrypted())
					msg.setMessage(CaesarCipher.encrypt(text, c.getPassword()));
				privatemsg(c.getNickName(), msg);
			}
		}
	}

	public void shutdown(ChatClient client) throws RemoteException {
		throw new RemoteException("Unsupported operation!");
	}

}
