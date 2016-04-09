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
	ServerSocket server;

	public ChatServerImpl() throws IOException {
		this(DEFAULT_PORT);
	}

	public ChatServerImpl(int port) {
		this.port = port;
		this.alive = true;
		this.clientId = 0;
		this.sdf = new SimpleDateFormat("HH:mm:ss");
		this.clients = new HashMap<String, ServerThreadForClient>();
		try {
			this.server = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Fatal error: could not launch server! Exiting now ...");
		}
	}

	public static void main(String[] args) throws IOException {

		ChatServerImpl server = new ChatServerImpl();
		server.startup();
	}
	
	@Override
	public void startup() {
		Socket client;
		System.out.println("[" + sdf.format(new Date()) + "] Server started!");
		while (alive) {
			System.out.println("Listening for connections at " +
					server.getInetAddress() + ":" +
					server.getLocalPort());
			System.out.println("Connected clients so far: " + clientId);
			try {
				client = server.accept();
				manageRequest(client, new Date());
			} catch (IOException e) {
				System.err.println("Error: could not accept new connection !");
				close();
			}
		}
		close();
	}

	@Override
	public void shutdown() {
		//No implementado
		throw new UnsupportedOperationException();
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
		ChatMessage msg = new ChatMessage(0, MessageType.LOGOUT, "You are now disconnected from server!");
		ServerThreadForClient client = clients.remove(username);
		clientId--;
		try {
			client.out.writeObject(msg);
		} catch (IOException e) {
			System.err.println("Error: could notify logout to user " + client.username);
		}
	}
	
	private void manageRequest(Socket socket, Date timestamp) {
		int key;
		String username;
		String msg;
		String loginTime = sdf.format(timestamp);
		ChatMessage request;
		ChatMessage response;
		ServerThreadForClient clientThread;
		
		try {
			request = (ChatMessage) new ObjectInputStream(socket.getInputStream()).readObject();
			key = request.getId();
			username = request.getMessage();
			
			if (clients.get(username) != null) {
				msg = "Could not connect: username " + username + " already exists!";
				response = new ChatMessage(0, MessageType.LOGOUT, msg);
			} else {
				clientThread = new ServerThreadForClient(key, username, socket);
				clientThread.start();
				clients.put(username, clientThread);
				clientId++;
		
				System.out.println("[" + loginTime + "]: " + username +
						" has just connected to the server!");
				msg = "[" + loginTime + "]SERVER: Welcome to Chat 1.0! " + 
						"You are logged in as " + username;
				response = new ChatMessage(0, MessageType.MESSAGE, msg);
			}
			
			new ObjectOutputStream(socket.getOutputStream()).writeObject(response);
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("Error: could not manage connection request!");
		}
	}
	
	private void close() {
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class ServerThreadForClient extends Thread {

		private int id;
		private String username;
		private Socket socket;

		private ObjectInputStream in;
		private ObjectOutputStream out;
		
		private boolean logout;

		public ServerThreadForClient(int id, String username, Socket socket) {
			super(username);
			this.id = id;
			this.username = username;
			this.socket = socket;
			logout = false;
			try {
				in = new ObjectInputStream(socket.getInputStream());
				out = new ObjectOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				System.err.println("Error: could not create connection handler " + 
						"thread for user " + username + "!");
				close();
			}
		}

		@Override
		public void run() {
			while(!logout) {
				try {
					listen();
				} catch (ClassNotFoundException | IOException e) {
					System.err.println("Error: could not receive message from user " +
							username + "! Shutting down connection handler for this user.");
					remove(username);
					close();
				}		
			}
			close();
		}
		
		private void listen() throws ClassNotFoundException, IOException {
			ChatMessage msg = (ChatMessage) in.readObject();			
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
		}
		
		private String decryptText(String text, int key) {
			String prefix = "encrypted#";
			if(text.startsWith(prefix))
				return CaesarCipher.decrypt(text.substring(prefix.length()), key);
			return text;
		}
		
		private void close() {
			try {
				if(in != null)
					in.close();
				if(out != null)
					out.close();
				if(socket != null)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
