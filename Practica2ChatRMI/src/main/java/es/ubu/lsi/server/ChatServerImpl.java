package es.ubu.lsi.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import es.ubu.lsi.client.ChatClient;
import es.ubu.lsi.common.ChatMessage;
/**
 * Clase ChatServerImpl
 * @author Borja Gete
 * @author Plamen Peytov
 *
 */
public class ChatServerImpl extends UnicastRemoteObject implements ChatServer {
	private List<ChatClient> permitidos = new ArrayList<ChatClient>();
	/**
	 * Constructor de la clase ChatServerImpl
	 * @throws RemoteException Excepción remota surgida en la comunicación
	 */
	public ChatServerImpl()throws RemoteException{
		super();
	}
	/**
	 * Permite a un cliente acceder al chat
	 * @param client Cliente que solicita acceso
	 * @return Identificador del cliente
	 * @throws RemoteException Excepción remota surgida en la comunicación
	 */
	public int checkIn(ChatClient client) throws RemoteException {
		if (!permitidos.contains(client)){
			permitidos.add(client);
		}
		System.out.println(client.getNickName() + " se conectó");
		return permitidos.indexOf(client);
	}
	/**
	 * Echa/Desloguea a un cliente
	 * @param client Cliente al cual se termina la conexion
	 * @throws RemoteException Excepción remota surgida en la comunicación
	 */
	public void logout(ChatClient client) throws RemoteException {
		permitidos.remove(client);
		ChatMessage msg ;
		String text = client.getNickName() + " abandonó el chat";
		for (ChatClient cli : permitidos) {
			msg = new ChatMessage(client.getId(),client.getNickName(),"Te has desconectado...");
			cli.receive(msg);
		}
		System.out.println(text);	
	}
	/**
	 * Permite al destinatario la recepcion de un mensaje privado
	 * @param tonickname Cliente que recibe el mensaje privado
	 * @param msg Mensaje privado
	 * @throws RemoteException Excepción remota surgida en la comunicación
	 */
	public void privatemsg(String tonickname, ChatMessage msg) throws RemoteException{
		for (ChatClient cli : permitidos) {
			if (cli.getNickName().equalsIgnoreCase(tonickname)){
				cli.receive(msg);
			}
		}
	}
	/**
	 * Publica un mensaje recibido
	 * @param msg Mensaje a publicar
	 * @throws RemoteException Excepción remota surgida en la comunicación
	 */
	public void publish(ChatMessage msg) throws RemoteException {
		for(ChatClient cli : permitidos ){
			if (cli.getId()!=msg.getId())
				cli.receive(msg);
		}
	}

	public void shutdown(ChatClient client) throws RemoteException {
		// TODO Auto-generated method stub
		//NO IMPLEMENTAR?
	}

}
