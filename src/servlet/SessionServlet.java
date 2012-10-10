package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import db.Source;

public class SessionServlet extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	private Connection m_conn;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	response.setCharacterEncoding("UTF-8");
    	response.setContentType("application/json");
    	
    	//Obtain the session object, create a new session if doesn't exist
        HttpSession session = request.getSession(true);
		if (session.getAttribute("conn") == null)
        {
          try {
        	  System.out.println("(SessionServlet) creating new connection...");
        	  Class.forName("org.postgresql.Driver");
              Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/corpusdb", "appUser", "bla");
	          session.setAttribute("conn",c);
          } catch (ClassNotFoundException e) {
			// do nothing
			e.printStackTrace();
          } catch (SQLException e) {
			// do nothing
			e.printStackTrace();
          }
        }
        m_conn = (Connection)session.getAttribute("conn");
        
        String sourceName = (String)request.getParameter("sourceName");
        Source source = new Source(m_conn);
        source.getSourceByName(sourceName);
        int sourceId = source.getId();
        String result = "{\"item\":" + sourceId + "}";
        response.getWriter().println(result);
    }

}