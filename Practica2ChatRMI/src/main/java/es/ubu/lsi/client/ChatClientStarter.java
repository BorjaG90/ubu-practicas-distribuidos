package es.ubu.lsi.client;

import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import es.ubu.lsi.common.*;
import es.ubu.lsi.server.ChatServer;

/**
 * 
 * @author Borja Gete
 * @author Plamen Peytov
 *
 */
public class ChatClientStarter {

	private String host;
	private int id;
	private String nickname;
	private int password;
	private boolean logout;

	public ChatClientStarter(String[] args) throws Exception {

		switch (args.length) {
		case 2:
			host = "localhost";
			nickname = args[0];
			password = Integer.parseInt(args[1]);
			break;
		case 3:
			host = args[0];
			nickname = args[1];
			password = Integer.parseInt(args[2]);
			break;
		default:
			System.out.println("Invalid number of arguments!");
			printHelp();
			System.exit(1);
		}

		this.logout = false;
		// ESTO NO ESTA BIEN AQUI
		start();
	}

	public void start() throws Exception {
		ChatClient client = new ChatClientImpl(nickname, password);
		ChatServer server = (ChatServer) Naming.lookup("rmi://" + host + "/ChatServer");

		ChatMessage msg;
		String text;
		Scanner input;

		this.id = server.checkIn(client);
		client.setId(this.id);

		input = new Scanner(System.in);
		while (!logout) {
			System.out.println(">>>");
			if ((text = input.next()).equalsIgnoreCase("logout")) {
				System.out.println("Shutting down client...");
				this.logout = true;
				server.logout(client);
				input.close();
				UnicastRemoteObject.unexportObject(client, true);
			} else {
				msg = new ChatMessage(id, this.nickname, CaesarCipher.encryptText(text, this.password));
				server.publish(msg);
			}
		}
	}

	/**
	 * Método printHelp. Muestra un mensaje deayuda para el uso del programa
	 * Cliente.
	 */
	private static void printHelp() {
		System.out.println("USAGE:");
		System.out.println("\tcliente.bat <server_address> <username> <password>");
		System.out.println("\tOR");
		System.out.println("\tcliente.bat <username> <key> (default server: localhost)");
	}

}
