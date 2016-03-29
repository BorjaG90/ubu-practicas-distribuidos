package es.ubu.lsi.client;
/**Clase ChatClientImpl
 * Implementación del cliente de chat
 * @author Borja Gete & Plamen Petkov
 * @version 1.0.0
 */
import es.ubu.lsi.common.ChatMessage;

public class ChatClientImpl implements ChatClient {
	
	private String server;
	private String username;
	private int port;
	private boolean carryOn = true;
	private int id;
	
	public ChatClientImpl(String server, int port, String username) {
		this.server=server;
		this.port=port;
		this.username=username;
		
	}
	
	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void sendMessage(ChatMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}
	class ChatClientListener implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
	}
	public static void main(String[] args){
		
	}

}
