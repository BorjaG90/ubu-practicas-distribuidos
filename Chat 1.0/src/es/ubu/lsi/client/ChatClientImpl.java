package es.ubu.lsi.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import es.ubu.lsi.common.*;

/**
 * Clase ChatClientImpl Implementación del cliente de chat.
 * 
 * @author Borja Gete
 * @author Plamen Petkov
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
	 * Constructor. Construye una instancia de ChatClientImpl.
	 * 
	 * @param server IP del servidor al que se conecta el cliente.
	 * @param port Puerto del servidor al que envía las peticiones
	 * @param username nombre de usuario con el que se conecta.
	 * @param key clave del usuario con la que se encriptan los mensajes.
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
	 * Método main. Metodo principal de ejecución del cliente.
	 * 
	 * @param args argumentos de entrada del programa cliente.
	 */
	public static void main(String[] args) {

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
	 * Método start. Inicia el cliente y conecta este cliente con el servidor.
	 * @return true, si no ha habido error, false en caso contrario.
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
	 * Método sendMessage. Permite enviar un mensaje al servidor.
	 * @param msg el mensaje a enviar.
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
	 * Método disconnect. Desconecta el cliente del servidor.
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
	 * Método connect. Este método envía una petición de login al servidor
	 * y se queda a la espera de recibir respuesta. Si la respuesta del servidor
	 * es LOGOUT, significa que hay un usuario con el nombre de usuario conectado y
	 * se para la ejecución del cliente. Sino, crea un hilo de escucha de
	 * mensajes procedenes del servidor.
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
	 * Método encryptText. Encripta un texto con el prefijo "encrypted#"
	 * utilizando el algoritmo de cifrado de Cesar.
	 * Si el texto no comienza con el prefijo, se devuelve sin modificar.
	 * 
	 * @param text texto a cifrar
	 * @param key clave usada para cifrar
	 * @return el texto recibido cifrado o sin cifrar
	 */
	private String encryptText(String text, int key) {
		String prefix = "encrypted#";
		if (text.startsWith(prefix)) //Si comienza con el prefijo indicado se cifra
			text = prefix + CaesarCipher.encrypt(text.substring(prefix.length()), key);
		return text;
	}
	
	/**
	 * Método printHelp.
	 * Muestra un mensaje deayuda para el uso del programa Cliente.
	 */
	private static void printHelp() {
		System.out.println("USAGE:");
		System.out.println("\tjava ChatClientImpl <server_address> <username> <key>");
		System.out.println("\tOR");
		System.out.println("\tjava ChatClientImpl <username> <key> (default server: localhost)");
	}
	
	/**
	 * Clase interna ChatClientListener
	 * Crea un hilo de escucha para losmensajes procedentes del servidor.
	 * 
	 * @author Borja Gete
	 * @author Plamen Petyov
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
