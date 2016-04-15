package es.ubu.lsi.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.ServerSocket;
import java.net.Socket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.ubu.lsi.common.*;
/**
 * Clase ChatServerImpl Implementación del servidor de Chat
 * @author Borja Gete & Plamen Petkov
 * @version 1.0.0
 */
public class ChatServerImpl implements ChatServer {

	private static final int DEFAULT_PORT = 1500;
	private static int clientId;
	private static SimpleDateFormat sdf;

	private int port;
	private boolean alive;
	//Diccionario con los nicknames y su hilo asociado
	Map<String, ServerThreadForClient> clients;
	ServerSocket server;
	/**
	 * Constructor vacío
	 * 
	 * @throws IOException
	 */
	public ChatServerImpl() {
		this(DEFAULT_PORT);
	}
	/**
	 * Constructor
	 * 
	 * @param port Puerto de conexión
	 */
	public ChatServerImpl(int port) {
		clientId = 0;
		sdf = new SimpleDateFormat("HH:mm:ss");
		this.port = port;
		this.clients = new HashMap<String, ServerThreadForClient>();
		try {
			this.server = new ServerSocket(this.port);
		} catch (IOException e) {
			System.err.println("FATAL ERROR: could not launch server! Exiting now ...");
			System.exit(1);
		}
	}
	/**
	 * main method
	 * 
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) {

		new ChatServerImpl().startup();
	}
	/**
	 * startup implementation
	 * Implementación del metodo startup
	 */
	@Override
	public void startup() {
		Socket client;
		//Inicia el servidor y comienza a escuchar peticiones
		System.out.println("[" + sdf.format(new Date()) + "] Server started!");
		while (alive) {
			System.out.println("Listening for connections at " + server.getInetAddress() + ":" + server.getLocalPort());
			System.out.println("Connected clients so far: " + clientId);
			
			try {
				//Al aceptar conexiones inicia el hilo de servidor para ese cliente
				client = server.accept();
				new ServerThreadForClient(client).start();
			} catch (IOException e) {
				System.err.println("ERROR: could not accept connection! Shutting down server ...");
			}
		}
	}
	/**
	 * shutdown implementation
	 * Implementacion del metodo shutdown
	 * No se implementa porque no se pide
	 */
	@Override
	public void shutdown() {
		// No implementado
		throw new UnsupportedOperationException();
	}
	/**
	 * broadcast implementation
	 * Implementacion del metodo broadcast
	 * Recibe un mensaje de un cliente y lo reenvia al resto de clientes conectados
	 */
	@Override
	public void broadcast(ChatMessage message) {
		String time = "[" + sdf.format(new Date()) + "]";
		for (ServerThreadForClient handler : clients.values()) {
			message.setMessage(time + handler.username + ": " + message.getMessage());
			try {
				handler.out.writeObject(message);
			} catch (IOException e) {
				System.err.println("ERROR: could not send message to " +
						handler.username + "! User is unreachable!");
				remove(handler.username);
			}
		}
	}
	/**
	 *remove implementation
	 *Implementacion del metodo remove
	 *Desconecta y elimina del diccionario al cliente especificado
	 */
	@Override
	public void remove(String username) {
		//Enviamos un mensaje de desconexion y eliminamos al cliente del diccionario
		ChatMessage msg = new ChatMessage(0, MessageType.LOGOUT, "You are now disconnected from server!");
		ServerThreadForClient client = clients.remove(username);
		clientId--;
		try {
			client.out.writeObject(msg);
		} catch (IOException e) {
			System.err.println("ERROR: could notify logout to user " + client.username + "!");
		}
	}
	/**
	 * Clase interna ServerThreadForClient
	 * Hilo que gestiona la comunicación entre el cliente y el servidor
	 * @author Borja Gete & Plamen Petkov
	 * @version 1.0.0
	 *
	 */
	class ServerThreadForClient extends Thread {

		private int id;
		private String username;
		private Socket client;
		private boolean logout;

		private ObjectInputStream in;
		private ObjectOutputStream out;
		
		/**
		 * Constructor
		 * @param socket 
		 */
		public ServerThreadForClient(Socket socket) {
			this.client = socket;
			this.logout = false;
			
			try {
				this.out = new ObjectOutputStream(socket.getOutputStream());
				this.in = new ObjectInputStream(socket.getInputStream());
				ChatMessage request = (ChatMessage) in.readObject();
				this.username = request.getMessage();
				this.id = request.getId();
			} catch (IOException | ClassNotFoundException e ) {
				System.err.println("ERROR: could not create connection handler thread!");
			}
			
			this.setName(username);
			System.out.println("Created connection handler for user " + username + "!");
		}
		/**
		 * run implementation
		 * Implementacion del metodo run
		 * Realiza las acciones para conectar y comunicar con un cliente 
		 */
		@Override
		public void run() {
			try {
				loginUser(username);
				while (!logout)				
					listen();
			} catch (ClassNotFoundException | IOException e) {
					System.err.println("ERROR: lost connection with " + username
							+ "! Shutting down connection handler for this user.");
					remove(username);
					close();
			}
		}
		
		/**
		 * method loginUser
		 * Método que responde al cliente, cuando este se conecta satisfactoriamente al servidor
		 * @param username
		 * @throws IOException
		 */
		private void loginUser(String username) throws IOException {
			ChatMessage response;
			String text;
			String loginTime = sdf.format(new Date());
			//Si existe otro cliente con el mismo nick
			if (clients.get(username) != null) {
				text = "Could not connect: username " + username + " already exists!";
				response = new ChatMessage(0, MessageType.LOGOUT, text);
			} else {
				clients.put(username, this);
				clientId++;

				text = "[" + loginTime + "]SERVER: Welcome to Chat 1.0! " + "You are logged in as " + username;
				response = new ChatMessage(0, MessageType.MESSAGE, text);

				System.out.println("[" + loginTime + "]: " + username + " has just connected to the server!");
			}
			out.writeObject(response);
		}
		
		/**
		 * method listen
		 * Método que recibe mensajes del cliente y actua en consecuencia
		 * dependiendo del tipo de mensaje recibido
		 * @throws ClassNotFoundException
		 * @throws IOException
		 */
		private void listen() throws ClassNotFoundException, IOException {
			ChatMessage msg = (ChatMessage) in.readObject();
			switch (msg.getType()) {
				case MESSAGE:
					msg.setMessage(decryptText(msg.getMessage(), id));
					broadcast(msg);
					break;
				case LOGOUT:
					System.out.println("[" + sdf.format(new Date()) + "] Disconnected user " + username);
					remove(username);
					close();
					break;
				default:
			}
		}
		
		/**
		 * decryptText method
		 * Método que desencripta texto cifrado con Cesar
		 * @param text Texto a desencriptar
		 * @param key Clave recibida para desencriptar
		 * @return Texto recibido desencriptado o no
		 */
		private String decryptText(String text, int key) {
			//Si el texto no tiene el prefijo, no esta cifrado
			String prefix = "encrypted#";
			if (text.startsWith(prefix))
				return CaesarCipher.decrypt(text.substring(prefix.length()), key);
			return text;
		}
		
		/**
		 * close method
		 * Método que cierra las conexiones con los clientes
		 */
		private void close() {
			try {
				logout = true;
				if (in != null)
					in.close();
				if (out != null)
					out.close();
				if (client != null)
					client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
