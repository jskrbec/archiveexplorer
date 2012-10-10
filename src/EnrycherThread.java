import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import si.ijs.enrycher.doc.Annotation;
import si.ijs.enrycher.doc.Attribute;
import si.ijs.enrycher.doc.Document;
import si.ijs.enrycher.doc.EnrycherException;
import si.ijs.enrycher.exe.EnrycherWebExecuter;
import db.Article;

public class EnrycherThread implements Runnable
{
	private Article m_article;
	private AtomicInteger m_threadCount;
	
	public EnrycherThread(Article a, AtomicInteger threadCount)
	{
		m_article = a;
		m_threadCount = threadCount;
	}
	
	@Override
	public void run() 
	{
		long timeStartEnrycher = 0;
		long timeStopEnrycher = 0;
		long timeStart = System.currentTimeMillis();
		String text = m_article.getTitle() + " " +  m_article.getLeadParagraph() + " " + m_article.getText();
		try {			
			InputStream is = new ByteArrayInputStream(text.getBytes("UTF-8"));
			timeStartEnrycher = System.currentTimeMillis();
			Document doc = EnrycherWebExecuter.processSync(new URL("http://marquis.ijs.si:9080/EnrycherWeb/run-full"), is);
			timeStopEnrycher = System.currentTimeMillis();
			if (doc != null)
			{
				ArrayList<Attribute> attributes = new ArrayList<Attribute>();
				attributes.addAll(doc.getAttributes());
				ArrayList<String> categories = new ArrayList<String>();
				ArrayList<String> keywords = new ArrayList<String>();
				
				for (Attribute attr : attributes)
				{
					if (attr.getType().equals("dmoz:topic"))
					{
						categories.add(attr.getLiteral());
					}
					else if (attr.getType().equals("rdfs:label"))
					{
						keywords.add(attr.getLiteral());
					}
				}
					
				m_article.setCategories(categories);
				m_article.setKeywords(keywords);
				
				m_article.setEntities(new ArrayList<Annotation>());
				for (Annotation annot : doc.getAnnotations())
				{
					if (annot.getType().equals("enrycher:location") ||
							annot.getType().equals("enrycher:person") ||
							annot.getType().equals("enrycher:organization"))
					{

						m_article.addEntity(annot);
					}
				}
				
				Class.forName("org.postgresql.Driver");
				Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/corpusdb", "appUser", "bla");
				m_article.saveEnrychedData(conn);
				conn.close();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EnrycherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println("Problems with the database connection:");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long timeStop = System.currentTimeMillis();
		System.out.println(timeStop - timeStart + "ms: thread " + m_article.getId() + " [Enrycher: " + (timeStopEnrycher - timeStartEnrycher) + "ms]");
		m_threadCount.decrementAndGet();
	}

}
