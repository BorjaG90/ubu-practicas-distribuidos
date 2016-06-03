package es.ubu.lsi.Practica3ChatWeb;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Clase ServletMessages.
 * 
 * @author Borja Gete
 * @author Plamen Peytov
 *
 */

@WebServlet("/sendMsg")
public class ServletMessages extends HttpServlet{

	private static final long serialVersionUID = 1L;
	/**
	 * Método doGet
	 * 
	 * @param request
	 * 			Petición al servlet
	 * @param response
	 * 			Respuesta del servlet
	 * @throws ServletException 
	 * 			Excepcion de funcionamiento del servlet
	 * @throws IOException
	 * 			Excepcion de entrada/salida
	 */			 
	protected void doGet(HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		HttpSession session = request.getSession();
		if (session.getAttribute("nickname")!=null){
			String nick = (String) session.getAttribute("nickname");
			Integer key = (Integer) session.getAttribute("key");
			String msg = new String(request.getParameter("message").getBytes("ISO-8859-1"), "UTF-8");
			String isEncrypted = request.getParameter("isEncrypted");
			String message = "";
			Date date = new Date();
			DateFormat hour = new SimpleDateFormat("HH:mm:ss");
			//Añadir nuevo mensaje:
			ServletContext context = getServletContext();
			List<String> messages = (List<String>) context.getAttribute("messages");
			if(isEncrypted.equals("true")){
				msg=Cesar.descifrar(msg, key);
			}
			message = hour.format(date) +  " " + nick + " --> " + msg;
	
			messages.add(message);
		
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/chatroom.jsp");
			
			if (dispatcher == null) { 
				response.sendError(response.SC_NO_CONTENT); 
			} else {
				dispatcher.include(request, response);
			}
		}else{
			response.sendRedirect("index.html");
		}
	}
	/**
	 * Método doPost
	 @param request
	 * 			Petición al servlet
	 * @param response
	 * 			Respuesta del servlet
	 * @throws ServletException 
	 * 			Excepcion de funcionamiento del servlet
	 * @throws IOException
	 * 			Excepcion de entrada/salida
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
}
