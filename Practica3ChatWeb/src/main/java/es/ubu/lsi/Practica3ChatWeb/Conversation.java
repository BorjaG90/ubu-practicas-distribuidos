package es.ubu.lsi.Practica3ChatWeb;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/**
 * Clase ShowMsg.
 * 
 * @author Borja Gete
 * @author Plamen Peytov
 *
 */
@WebServlet("/conversation")
public class Conversation extends HttpServlet{

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
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		ServletContext context = getServletContext();
		List<String> messages = (List<String>) context.getAttribute("messages");
		Iterator<String> msgIt = messages.iterator();
		String msg ="";
		
		out.println("<html>");
		out.println("<head><style>");
		out.println("body {background-color: lightblue; font-size: x-small; ");
		out.println("font-family: Verdana,Serif,Arial,Calibri;}");
		out.println("</style></head>");
		out.println("<body>");
		while(msgIt.hasNext()){
			msg = msgIt.next();
			out.println(msg);
			out.println("<br>");
		}
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	/**
	 * Método doPost
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
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
}
