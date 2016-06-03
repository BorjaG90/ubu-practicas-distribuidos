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
	/**
	 * Método contextInitialized
	 * 		Inizializamos el contexto
	 * @param
	 * 		sce Evento del servlet
	 */
	public void contextInitialized(ServletContextEvent sce) {
		messages = new ArrayList<String>();
		users = new ArrayList<String>();
		ServletContext context = sce.getServletContext();
		context.setAttribute("messages", messages);
		context.setAttribute("users", users);
	}
	@Override
	/**
	 * Método contextDestroyed
	 * 		Destruimos el contexto
	 * @param arg0
	 * 		Evento del servlet
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		messages.removeAll(messages);
		users.removeAll(users);
	}

	

}
