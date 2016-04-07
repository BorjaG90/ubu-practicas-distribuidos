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
		try (ServerSocket server = new ServerSocket(port);) {
			while (alive) {
				System.out.println("Listening for connections at " +
						server.getInetAddress() + ":" +
						server.getLocalPort());
				System.out.println("Connected clients so far: " + clientId);
				try {
					manageRequest(server.accept(), new Date());
				} catch (ClassNotFoundException | IOException e) {
					
				}
			}
		} catch (IOException e) {
			System.err.println("Fatal error: could not launch the server! Exiting now ...");
		}		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	// es probable que tenga que ser un metodo synchronized
	public void broadcast(ChatMessage message) {

	}

	@Override
	public void remove(int id) {
		clients.remove(id);
	}
	
	private void manageRequest(Socket client, Date timestamp) throws
		ClassNotFoundException, IOException {
		
		ChatMessage request;
		ChatMessage response;
		ServerThreadForClient clientThread;
		
		String username;
		String msg;
		String loginTime = sdf.format(timestamp);	
		
		request = (ChatMessage) new ObjectInputStream(client.getInputStream()).readObject();
		username = request.getMessage();
			
		if (clients.get(username) != null) {
			msg = "Could not connect: username " + username + " already exists!";
			response = new ChatMessage(0, MessageType.SHUTDOWN, msg);
		} else {
				try {
					clientThread = new ServerThreadForClient(username, client);
					clientThread.start();
					clients.put(username, clientThread);
					clientId++;
				} catch (ClassNotFoundException | IOException e) {
					System.err.println("Unable to create handler thread for " +
							"user " + username + "!");
				}
			
			System.out.println("[" + loginTime + "]: " + username +
					" has just connected");
			
			msg = "[" + loginTime+ "] SERVER: Welcome to Chat 1.0! " + 
					"You are connected as " + username;
			
			response = new ChatMessage(0, MessageType.MESSAGE, msg);
		}
		new ObjectOutputStream(client.getOutputStream()).writeObject(response);
	}

	class ServerThreadForClient extends Thread {

		private int id;
		private String username;
		private Socket socket;

		private ObjectInputStream in;
		private ObjectOutputStream out;

		public ServerThreadForClient(String username, Socket socket) throws
				IOException, ClassNotFoundException {
			super(username);
			this.socket = socket;
			
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
			ChatMessage msg = (ChatMessage) in.readObject();
				
			this.id = msg.getId();
			this.username = username;
		}

		@Override
		public void run() {

		}
		
		public void close() {
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
