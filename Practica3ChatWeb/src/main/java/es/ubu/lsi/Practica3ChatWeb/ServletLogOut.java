package es.ubu.lsi.Practica3ChatWeb;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/**
 * Clase ServletLogOut.
 * 
 * @author Borja Gete
 * @author Plamen Peytov
 *
 */

@WebServlet("/logoutUser")
public class ServletLogOut extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		String name = (String) session.getAttribute("nickname");
		session.invalidate();
	
		ServletContext context = getServletContext();
		List<String> users = (List<String>) context.getAttribute("users");
		
		users.remove(name);
		
		List<String> messages = (List<String>) context.getAttribute("messages");
		String last = name + " is now disconnected";
		messages.add(last);

	    response.sendRedirect("index.html");
	}
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
}
