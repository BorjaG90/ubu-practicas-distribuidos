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
	private String text;

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
		start();
	}

	public void start() {
		try {
			// Comprobar exportacion del cliente
			ChatClient client = new ChatClientImpl(nickname, password);
			ChatServer server = (ChatServer) Naming.lookup("rmi://" + host + "/ChatServer");
			ChatMessage msg;
			
			Scanner input = new Scanner(System.in);
			
			this.id = server.checkIn(client);
			client.setId(this.id); // se puede hacer en el servidor
			
			while(!logout) {
				System.out.println(">>>");
				if((text = input.next()).equalsIgnoreCase("logout")) {
					this.logout = true;
					System.out.println("Shutting down client now...");
					server.logout(client);
					UnicastRemoteObject.unexportObject(client, true);
					input.close();
					// VER SI HACE FALTA PONER UN MENSAJE PARA NOTIFICAR A OTROS USUARIOS EL LOGOUT
				} else {
					msg = new ChatMessage(id, this.nickname, encryptText(text, this.password));
					server.publish(msg);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			// TODO GESTION DE ERRORES
		}
	}
	
	/**
	 * Método encryptText. Encripta un texto con el prefijo "encrypted#"
	 * utilizando el algoritmo de cifrado de Cesar.
	 * Si el texto no comienza con el prefijo, se devuelve sin modificar.
	 * 
	 * @param text texto a cifrar
	 * @param key clave usada para cifrar
	 * @return el texto recibido cifrado o sin cifrar
	 */
	private String encryptText(String text, int key) {
		String result = "encrypted#";
		if (text.startsWith(result)) //Si comienza con el prefijo indicado se cifra
			result = result + CaesarCipher.encrypt(text.substring(result.length()), key);
		return result;
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
