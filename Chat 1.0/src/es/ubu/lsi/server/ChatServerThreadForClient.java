package es.ubu.lsi.server;

public class ChatServerThreadForClient extends Thread {

	private int id;
	
	private String username;
	
	public ChatServerThreadForClient(int id, String username) {
		super(username);
		this.id = id;
	}
	
}
