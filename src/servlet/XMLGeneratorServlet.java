package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import searchpoint.XmlGenerator;
import db.Article;


public class XMLGeneratorServlet extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	private Connection m_conn;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	response.setCharacterEncoding("UTF-8");
    	
    	//Obtain the session object, create a new session if doesn't exist
          try {
        	  System.out.println("(XMLGeneratorServlet  - " + request.getRemoteAddr() + ") creating new connection...");
        	  Class.forName("org.postgresql.Driver");
              m_conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/corpusdb", "appUser", "bla");
	          
          } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
          } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
          }
        
        
        String ids = (String)request.getParameter("ids");
        String[] articleIds = ids.split(",");
        ArrayList<Article> articles = new ArrayList<Article>();
        int id = 0;
        for (String s: articleIds)
        {
        	if(id == 0)
        	{
        		id = Integer.parseInt(s);
        	}
        	else
        	{
        		Article a = new Article(id);
        		a.setSearchScore(Integer.parseInt(s));
        		articles.add(a);
        		id = 0;
        	}
        }
        
        XmlGenerator searchPointInput = new XmlGenerator(articles, m_conn);
        String xml = "";
		try {
			xml = searchPointInput.generate();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			m_conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        response.getWriter().println(xml);
    }

}