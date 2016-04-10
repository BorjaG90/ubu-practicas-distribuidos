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

	public static void main(String[] args) throws IOException, ClassNotFoundException {

		new ChatServerImpl().startup();
	}
	
	// OK
	@Override
	public void startup() {
		Socket client;
		
		System.out.println("[" + sdf.format(new Date()) + "] Server started!");
		while (alive) {
			System.out.println("Listening for connections at " + server.getInetAddress() + ":" + server.getLocalPort());
			System.out.println("Connected clients so far: " + clientId);
			
			try {
				client = server.accept();
				new ServerThreadForClient(client).start();
			} catch (IOException e) {
				System.err.println("ERROR: could not accept connection! Shutting down server ...");
			}
		}
	}

	@Override
	public void shutdown() {
		// No implementado
		throw new UnsupportedOperationException();
	}
	
	// OK
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
	
	// OK
	@Override
	public void remove(String username) {
		ChatMessage msg = new ChatMessage(0, MessageType.LOGOUT, "You are now disconnected from server!");
		ServerThreadForClient client = clients.remove(username);
		clientId--;
		try {
			client.out.writeObject(msg);
		} catch (IOException e) {
			System.err.println("ERROR: could notify logout to user " + client.username + "!");
		}
	}

	class ServerThreadForClient extends Thread {

		private int id;
		private String username;
		private Socket client;
		private boolean logout;

		private ObjectInputStream in;
		private ObjectOutputStream out;
		
		// OK
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
		
		// OK
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
		
		// OK
		private void loginUser(String username) throws IOException {
			ChatMessage response;
			String text;
			String loginTime = sdf.format(new Date());

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
		
		//OK
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
		
		//OK
		private String decryptText(String text, int key) {
			String prefix = "encrypted#";
			if (text.startsWith(prefix))
				return CaesarCipher.decrypt(text.substring(prefix.length()), key);
			return text;
		}
		
		//OK
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
