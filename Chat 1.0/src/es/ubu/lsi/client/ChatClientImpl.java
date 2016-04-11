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
	//Booleano que indica el permiso a leer del canal
	private boolean carryOn;
	private Socket clientSocket;
	ObjectOutputStream out;

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
		// Booleano para que siga leyendo
		this.carryOn = true;

		try {
			//Instanciamos el socket y el canal de salida
			this.clientSocket = new Socket(this.server, this.port);
			out = new ObjectOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			System.err.println("FATAL ERROR: could not launch client! Exiting now ...");
			System.exit(1);
		}
	}

	/**
	 * main method
	 * 
	 * @param args Pueden ser la clave, usuario y servidor
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws ClassNotFoundException, IOException {

		int key = 3;
		int port = 1500;
		String username = null;
		String server = "localhost";

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

		new ChatClientImpl(server, port, username, key).start();
	}
	/**
	 * start method
	 * Implementacion del metodo start
	 * Conecta al cliente con el servidor y gestiona el envio de mensajes
	 */
	@Override
	public boolean start() {

		String text;
		ChatMessage msg;
		
		connect();
		
		input = new Scanner(System.in);
		while (carryOn) {
			System.out.print(">>>");
			if ((text = input.next()).equalsIgnoreCase("logout")) {
				// Si el cliente pide desconectarse
				msg = new ChatMessage(key, MessageType.LOGOUT, text);
				disconnect();
			} else {
				// Si el cliente escribe un mensaje
				msg = new ChatMessage(key, MessageType.MESSAGE, encryptText(text, key));
			}
			sendMessage(msg);
		}
		return true;
	}
	/**
	 * sendMessage implementation
	 * Implementacion del metodo sendMessage
	 * Metodo que envia un mensaje por el canal al servidor
	 */
	@Override
	public void sendMessage(ChatMessage msg) {
		try {
			//Envia el mensaje por el canal de salida
			out.writeObject(msg);
		} catch (IOException e) {
			System.err.println("ERROR: could not send message to server!");
		}
	}
	/**
	 * disconnect implementation
	 * Implementacion del metodo disconnect
	 * Metodo que evita la lectura del canal de entrada y cierra conexiones al servidor
	 */
	@Override
	public void disconnect() {
		carryOn = false; //Dejamos de leer del canal
		try {
			if (input != null)
				input.close();
			if (out != null)
				out.close();
			if (clientSocket != null)
				clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * connect method
	 * Método que conecta un cliente al servidor.
	 * La conexion se realiza enviando un mensaje con solicitud de login
	 * y espera a recibir la respuesta del servidor
	 * Si el servidor envia un mensaje de tipo logout 
	 * significa que ya hay un usuario con ese nick
	 */
	private void connect() {
		ChatMessage msg = new ChatMessage(key, MessageType.MESSAGE, username);
		ObjectInputStream in;
		
		try {
			in = new ObjectInputStream(clientSocket.getInputStream());
			sendMessage(msg);		
			msg = (ChatMessage) in.readObject();
			
			System.out.println(msg.getMessage());
			if (msg.getType() == MessageType.LOGOUT) {
				System.out.println("Shutting down client now...");
				disconnect();
				System.exit(0);
			}
			new Thread(new ChatClientListener(in)).start();
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("ERROR: could not get response from server!");
			disconnect();
			System.exit(1);
		}
	}
	/**
	 * encryptText method
	 * Método que encripta texto mediante cifrado Cesar
	 * si se le especifica al inicio del texto
	 * @param text Texto a cifrar
	 * @param key Clave usada para cifrar
	 * @return Devuelve el texto recibido cifrado o sin cifrar
	 */
	private String encryptText(String text, int key) {
		String prefix = "encrypted#";
		if (text.startsWith(prefix)) //Si comienza con el prefijo indicado se cifra
			text = prefix + CaesarCipher.encrypt(text.substring(prefix.length()), key);
		return text;
	}
	/**
	 * printHelp method
	 * Método que muestra ayuda para el uso del modulo ChatClientImpl
	 */
	private static void printHelp() {
		System.out.println("USAGE:");
		System.out.println("\tjava ChatClientImpl <server_address> <username> <key>");
		System.out.println("\tOR");
		System.out.println("\tjava ChatClientImpl <username> <key> (default server: localhost)");
	}
	/**
	 * Clase interna ChatClientListener
	 * Crea un hilo de escucha que recibe lo que emite el servidor
	 * @author Borja Gete & Plamen Petyov
	 *
	 */
	class ChatClientListener implements Runnable {

		ObjectInputStream in;
		/**
		 * Constructor
		 * @param in Canal de entrada
		 */
		public ChatClientListener(ObjectInputStream in) {
			this.in = in;
		}
		/**
		 * run implementation
		 * Implementacion del metodo run
		 * Escucha en el canal de entrada los mensajes que provienen del servidor
		 */
		@Override
		public void run() {
			while (carryOn) {//Mientras pueda leer del canal de entrada
				try {
					String mensaje = ((ChatMessage) in.readObject()).getMessage();
					System.out.println(mensaje);
				} catch (IOException | ClassNotFoundException e) {
					System.err.println("ERROR: could not receive message! Server is unavailable.");
					System.out.println("Shutting down client now ...");
					disconnect();
				}
			}
		}
	}
}
