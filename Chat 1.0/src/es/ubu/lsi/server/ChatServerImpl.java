package es.ubu.lsi.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.ServerSocket;
import java.net.Socket;

import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.Map;

import es.ubu.lsi.common.ChatMessage;

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

	ServerSocket serverSocket;
	Map<Integer, ChatServerThreadForClient> clients;

	public ChatServerImpl() throws IOException {
		this(DEFAULT_PORT);
	}

	public ChatServerImpl(int port) throws IOException {
		alive = true;
		this.port = port;
		serverSocket = new ServerSocket(port);
		clients = new HashMap<Integer, ChatServerThreadForClient>();
	}

	@Override
	public void startup() {

		int id;
		String username;
		ChatMessage request;
		Socket clientSocket;
		ChatServerThreadForClient clientThread;

		try {
			while (alive) {

				clientSocket = serverSocket.accept();

				request = (ChatMessage) new ObjectInputStream(clientSocket.getInputStream()).readObject();

				id = request.getId();
				username = request.getMessage();

				if (clients.get(id) == null) {
					clientThread = new ChatServerThreadForClient(id, username, clientSocket);
					clients.put(id, clientThread);
					clientThread.start();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	class ChatServerThreadForClient extends Thread {

		private int id;
		private String username;
		private Socket socket;

		private ObjectInputStream in;
		private ObjectOutputStream out;

		public ChatServerThreadForClient(int id, String username, Socket socket) throws IOException {
			super(username);
			this.id = id;
			this.socket = socket;
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
		}

		@Override
		public void run() {

		}
	}

	public static void main(String[] args) throws IOException {

		ChatServerImpl server = new ChatServerImpl();
		server.startup();
	}

}
