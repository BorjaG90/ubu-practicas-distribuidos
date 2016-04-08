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
 * Clase ChatServerImpl Implementacion del servidor del chat
 * 
 * @author Borja Gete & Plamen Petkov
 * @version 1.0.0
 */
public class ChatServerImpl implements ChatServer {

	private static final int DEFAULT_PORT = 1500;
	private static int clientId;
	private static SimpleDateFormat sdf;

	private int port;
	private boolean alive;

	Map<String, ServerThreadForClient> clients;

	public ChatServerImpl() throws IOException {
		this(DEFAULT_PORT);
	}

	public ChatServerImpl(int port) throws IOException {
		this.port = port;
		alive = true;
		clientId = 0;
		sdf = new SimpleDateFormat("HH:mm:ss");
		clients = new HashMap<String, ServerThreadForClient>();
	}

	public static void main(String[] args) throws IOException {

		ChatServerImpl server = new ChatServerImpl();
		server.startup();
	}
	
	@Override
	public void startup() {
		System.out.println("[" + sdf.format(new Date()) + "] Server started!");
		try (ServerSocket server = new ServerSocket(port);) {
			while (alive) {
				System.out.println("Listening for connections at " +
						server.getInetAddress() + ":" +
						server.getLocalPort());
				System.out.println("Connected clients so far: " + clientId);
				try {
					manageRequest(server.accept(), new Date());
				} catch (ClassNotFoundException | IOException e) {
					System.err.println("Unable to manage request!");
				}
			}
		} catch (IOException e) {
			System.err.println("Fatal error: could not launch the server! Exiting now ...");
		}		
	}

	@Override
	public void shutdown() {
		System.out.println("Shutting down server ...");
	}

	@Override
	public void broadcast(ChatMessage message) {
		String time = "[" + sdf.format(new Date()) + "]";
		for(ServerThreadForClient handler : clients.values()) {
			message.setMessage(time + handler.username + ": " + message.getMessage());
			try {
				handler.out.writeObject(message);
			} catch (IOException e) {
				System.err.println("Error sending message: " +
					handler.username + " is unreachable!");
				remove(handler.username);
			}
		}
	}

	@Override
	public void remove(String username) {
		clients.remove(username);
		clientId--;
	}
	
	private void manageRequest(Socket socket, Date timestamp) throws
		ClassNotFoundException, IOException {
		
		int id;
		String username;
		String msg;
		String loginTime = sdf.format(timestamp);
		ChatMessage request;
		ChatMessage response;
		ServerThreadForClient clientThread;
		
		request = (ChatMessage) new ObjectInputStream(socket.getInputStream()).readObject();
		id = request.getId();
		username = request.getMessage();
			
		if (clients.get(username) != null) {
			msg = "Could not connect: username " + username + " already exists!";
			response = new ChatMessage(0, MessageType.SHUTDOWN, msg);
		} else {
			try {
				clientThread = new ServerThreadForClient(id, username, socket);
				clientThread.start();
				clients.put(username, clientThread);
				clientId++;
			} catch (IOException e) {
				System.err.println("Unable to create connection handler thread for " +
					"user " + username + "!");
			}
			System.out.println("[" + loginTime + "]: " + username +
					" has just connected to the server!");
			msg = "[" + loginTime + "]SERVER: Welcome to Chat 1.0! " + 
					"You are logged in as " + username;
			response = new ChatMessage(0, MessageType.MESSAGE, msg);
		}
		new ObjectOutputStream(socket.getOutputStream()).writeObject(response);
	}

	class ServerThreadForClient extends Thread {

		private int id;
		private String username;
		private Socket socket;

		private ObjectInputStream in;
		private ObjectOutputStream out;

		public ServerThreadForClient(int id, String username, Socket socket) throws
				IOException{
			super(username);
			this.id = id;
			this.username = username;
			this.socket = socket;
			
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());			
		}

		@Override
		public void run() {
			
			ChatMessage msg;
			boolean logout = false;
			
			while(!logout) {
				try {
					msg = (ChatMessage) in.readObject();
					switch(msg.getType()) {
						case MESSAGE:
							msg.setMessage(decryptText(msg.getMessage(), id));
							broadcast(msg);
							break;
						case LOGOUT:
							logout = true;
							remove(username);
							System.out.println("[" + sdf.format(new Date()) + 
									"] Disconnected user " + username);
							break;							
						default:
					}
				} catch (ClassNotFoundException | IOException e) {
					System.err.println("Unable to receive messages from user " + username + 
							"! Shutting down connection handler for this user.");
					close();
				}		
			}
			close();
		}
		
		private String decryptText(String text, int key) {
			String prefix = "encrypted#";
			if(text.startsWith(prefix))
				return CaesarCipher.decrypt(text.substring(prefix.length()), key);
			return text;
		}
		
		private void close() {
			try {
				in.close();
				out.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
