package es.ubu.lsi.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import es.ubu.lsi.common.*;

/**
 * Clase ChatClientImpl Implementación del cliente de chat
 * 
 * @author Borja Gete & Plamen Petkov
 * @version 1.0.0
 */

public class ChatClientImpl implements ChatClient {
	
	private String server;
	private String username;
	private int key;
	private int port;
	private boolean carryOn = true;
	private Socket clientSocket;
	private ObjectOutputStream out;
	private Scanner input;

	/**
	 * Constructor
	 * 
	 * @param server
	 * @param port
	 * @param username
	 */
	public ChatClientImpl(String server, int port, String username, int key) {
		this.server = server;
		this.port = port;
		this.username = username;
		this.key = key;
		
		try {
			this.clientSocket = new Socket(this.server, this.port);
			this.out = new ObjectOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			System.err.println("Fatal error: could not launch client! Exiting now ...");
		}
	}

	/**
	 * main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		int key = -1;
		int port = 1500;
		String username = null;
		String server = "localhost";
		ChatClientImpl client;
		
		if (args.length == 2) {
			username = args[0];
			key = Integer.parseInt(args[1]);
		} else if (args.length == 3) {
			server = args[0];
			username = args[1];
			key = Integer.parseInt(args[2]);
		} else {
			System.out.println("Invalid number of arguments!");
			printHelp();
			System.exit(1);
		}

		client = new ChatClientImpl(server, port, username, key);
		client.start();
	}

	@Override
	public boolean start() {
		
		ChatMessage msg;
		Thread listener;
		String text;
		
		// Crear objeto mensaje de conexion
		msg = new ChatMessage(key, MessageType.MESSAGE, username);
		sendMessage(msg);
		try {
			msg = (ChatMessage) new ObjectInputStream(clientSocket.getInputStream()).readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("Error: could not get response from server!");
		}
			
		System.out.println(msg.getMessage());
		if (msg.getType() == MessageType.LOGOUT) {
			System.out.println("Shutting down client now...");
			disconnect();
			System.exit(0);
		}
			
		try {
			listener = new Thread(
					new ChatClientListener(
					new ObjectInputStream(clientSocket.getInputStream())));
			listener.start();
		} catch (IOException e) {
			System.err.println("Error: could not create listener thread!");
			disconnect();
		}
					
		input = new Scanner(System.in);
		while (carryOn) {
			System.out.print("-->");
			if ((text = input.next()).equalsIgnoreCase("logout")) {
				msg = new ChatMessage(key, MessageType.LOGOUT, text);
				carryOn = false;
			} else {
				msg = new ChatMessage(key, MessageType.MESSAGE, encryptText(text, key));
			}
			sendMessage(msg);
		}
		disconnect();
		return true;
	}

	@Override
	public void sendMessage(ChatMessage msg) {
		try {
			out.writeObject(msg);
		} catch (IOException e) {
			System.err.println("Error: could not send message!");
		}
	}

	@Override
	public void disconnect() {
		carryOn = false;
		try {
			if(input != null)
				input.close();
			if(out != null)
				out.close();
			if(clientSocket != null)
				clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método encryptText Encripta un mensaje mediante cifrado Cesar
	 * 
	 * @param text
	 *            Mensaje a encriptar
	 * @param key
	 *            Clave usada para encriptar
	 * @return Devuelve el mensaje encriptado
	 */
	private String encryptText(String text, int key) {
		String prefix = "encrypted#";
		if (text.startsWith(prefix))
			text = prefix + CaesarCipher.encrypt(text.substring(prefix.length()), key);
		return text;
	}
	
	private static void printHelp() {
		System.out.println("USAGE: java ChatClientImpl <server_address> <username> <key>");
		System.out.println("Note: uses localhost by default if server address is omitted");
	}

	/**
	 * Clase ChatClientListener Implementa la interfaz Runnable, por lo tanto,
	 * redefine el método run para ejecutar el hilo de escucha de mensajes del
	 * servidor (flujo de entrada) y mostrar los mensajes entrantes.
	 * 
	 * @author Borja Gete & Plamen Petkov
	 *
	 */
	class ChatClientListener implements Runnable {
		
		ObjectInputStream in;

		/**
		 * Constructor
		 * 
		 * @param clientSocket
		 */
		ChatClientListener(ObjectInputStream in) {
			this.in = in;
		}

		@Override
		public void run() {
			while (carryOn) {
				try {
					String mensaje = ((ChatMessage) in.readObject()).getMessage();
					System.out.println(mensaje);
					System.out.print("-->");
				} catch (IOException | ClassNotFoundException e) {

				}
			}
		}
	}// -ChatClientListener
}// -ChatClientImpl
