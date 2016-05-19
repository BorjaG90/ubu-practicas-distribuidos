package es.ubu.lsi.server;

import java.rmi.Naming;
import java.rmi.Remote;

public class ChatServerStarter {

	public ChatServerStarter() throws Exception {
        Naming.rebind("/ChatServer", (Remote) new ChatServerImpl());
        System.out.println("Server registered successfully!");
	}
}
