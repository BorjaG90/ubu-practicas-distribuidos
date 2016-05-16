package es.ubu.lsi.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;

import es.ubu.lsi.common.*;
import es.ubu.lsi.server.ChatServer;

/**
 * 
 * @author Borja Gete
 * @author Plamen Peytov
 *
 */
public class ChatClientStarter {
	private static String nick;
	private static String host;
	private static int key;
	private static boolean salir = false;
	private static String text;
	public ChatClientStarter(String[] args) throws Exception {
		if (args.length <= 1) {
			System.err.println("Error en los argumentos. Apodo + clave+ host ");
			System.exit(1);
		}
		if (args.length == 2) {
			nick = args[0];
			key = Integer.parseInt(args[1]);
			host = "localhost";
		} else {
			nick = args[0];
			key = Integer.parseInt(args[1]);
			host = args[2];
		}
		start();
	}
	public void start() throws Exception {
		try {
			ChatClient cliente = new ChatClientImpl(nick);
			ChatServer servidor = (ChatServer) Naming.lookup("rmi://" + host
					+ "/ChatServer");
			int id;
			id = servidor.checkIn(cliente);
			cliente.setId(id);

			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
			String entrada;
			while (!salir && (entrada = stdin.readLine()) != null) {
				text = entrada;
				System.out.println(" ");
				if (text.equalsIgnoreCase("logout")) {
					salir = true;
					System.out.println("Shutting down client now...");
					servidor.logout(cliente);
					UnicastRemoteObject.unexportObject(cliente, true);
				}
				else if (text.equalsIgnoreCase("encrypted")){
					ChatMessage msg = new ChatMessage(cliente.getId(),cliente.getNickName(), CaesarCipher.encrypt(text, key));
					servidor.publish(msg);
				}else{
					ChatMessage msg = new ChatMessage(cliente.getId(),cliente.getNickName(), text);
					servidor.publish(msg);
				}
					
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
