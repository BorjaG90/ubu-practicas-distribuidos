package es.ubu.lsi.Practica3ChatWeb;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
/**
 * Clase Session.
 * 
 * @author Borja Gete
 * @author Plamen Peytov
 *
 */
@WebListener
public class Session implements ServletContextListener{
	/**Lista de usuarios conectados al chat*/
	private List<String> users;
	/**Lista de mensajes enviados al chat*/
	private List<String> messages;
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		users =  new ArrayList<String>();
		messages = new ArrayList<String>();
		
		ServletContext context = sce.getServletContext();
		context.setAttribute("users", users);
		context.setAttribute("messages", messages);
	}
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		users.removeAll(users);
		messages.removeAll(messages);
	}

	

}
