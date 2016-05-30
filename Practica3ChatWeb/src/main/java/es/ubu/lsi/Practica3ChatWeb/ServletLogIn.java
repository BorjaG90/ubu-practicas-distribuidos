package es.ubu.lsi.Practica3ChatWeb;

import java.io.IOException;
import java.io.PrintWriter;
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
 * Clase ServletLogIn.
 * 
 * @author Borja Gete
 * @author Plamen Peytov
 *
 */
@WebServlet("/loginUser")
public class ServletLogIn extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		if (request.getParameter("nickname")!=null){
			String nick = new String(request.getParameter("nickname").getBytes("ISO-8859-1"), "UTF-8");
			Integer key = new Integer(request.getParameter("key"));
			ServletContext context = getServletContext();
			
			//Vinculo la sesion a su usuario
			if(session.getAttribute("nickname")  == null){
				session.setAttribute("nickname", nick);
				session.setAttribute("key", key);
				
				List<String> messages = (List<String>) context.getAttribute("messages");
				String first = nick + " is connected";
				messages.add(first);
				List<String> users = (List<String>) context.getAttribute("users");
				users.add(nick);
			}
			
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
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
}
