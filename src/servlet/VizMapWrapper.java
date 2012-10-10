package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.Article;
import db.ArticleList;
import db.DBConnection;

public class VizMapWrapper extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
        try {
            URL url = new URL("http://localhost:8881/vizmap");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            
            String articlesString = getArticlesString(request.getParameter("bla"),request.getRemoteAddr());
            
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write("message=" + articlesString);
            writer.close();
            
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // OK
            	BufferedReader reader = null;
            	String line;
                try 
                {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line = reader.readLine()) != null) 
                    {
                        response.getWriter().println(line);
                    }
                }
                catch (Exception e) {
                	// handle exception
                }
            } 
            else 
            {
                // Server returned HTTP error code.
            }
        } 
        catch (MalformedURLException e) {
            // ...
        } 
        catch (IOException e) {
            // ...
        } 
        catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
        catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private String getArticlesString(String msg, String remoteAddr) throws ClassNotFoundException, SQLException, IOException
	{
		DBConnection dbc = new DBConnection("VizMapWrapper", remoteAddr);
        Connection conn = dbc.getConnection();
        ArticleList aList = new ArticleList(conn);
        String id = msg.substring(msg.indexOf("=")+1);
        if (msg.startsWith("entity"))
        {
        	aList.getArticlesByEntity(id);
        }else if (msg.startsWith("author"))
        {
        	aList.getArticlesByAuthor(id);
        }else if(msg.startsWith("articles"))
        {
        	return id;
        }
		int count = 0;
		String articlesString = "";
		for (Article a : aList)
        {            
            if (count < 500)
            {
            	String articleString = "";
            	if (a.getTitle() != null && !a.getTitle().isEmpty())
            	{
            		articleString = a.getTitle();
            	}
            	if (a.getLeadParagraph() != null && !a.getLeadParagraph().isEmpty())
            	{
            		articleString = articleString + " " + a.getLeadParagraph();
            	}
            	if (a.getText() != null && !a.getText().isEmpty())
            	{
            		articleString = articleString + " " + a.getText();
            	}
            	if (articleString != null && !articleString.isEmpty())
            	{
            		articlesString = articlesString + "\n" + a.getId() + ">" + articleString;
            		count++;
            	}
            }
        }
		return articlesString;
	}
}
