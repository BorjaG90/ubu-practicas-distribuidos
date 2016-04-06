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

import es.ubu.lsi.common.ChatMessage;
import es.ubu.lsi.common.MessageType;

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
		for (int id : clients.keySet())
			if (id != clientId)
				try {
					clients.get(id).out.writeObject(message);
				} catch (IOException e) {
					System.err.println("Could not send message to user " + clients.get(id).username);
				}
	}

	@Override
	public void remove(int id) {
		clients.remove(id);
	}
	
	private void manageRequest(Socket client, Date timestamp) throws IOException,
			ClassNotFoundException {
		
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
			clientThread = new ServerThreadForClient(username, client);
			clientThread.start();
			clients.put(username, clientThread);
			clientId++;
			
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

		public ServerThreadForClient(String username, Socket socket) {
			super(username);
			this.socket = socket;
			try {
				in = new ObjectInputStream(socket.getInputStream());
				out = new ObjectOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				close();
			}

			ChatMessage msg = (ChatMessage) in.readObject();
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
