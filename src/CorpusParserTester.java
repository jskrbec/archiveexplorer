import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import servlet.GraphData;
import servlet.SessionServlet;
import servlet.VizMapWrapper;
import servlet.XMLGeneratorServlet;


public class CorpusParserTester 
{
	
	protected static ScheduledExecutorService taskExecutor = Executors.newScheduledThreadPool(1);
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{	
//			CleanDB cleandb = new CleanDB();
//			cleandb.clean();
		  
//		taskExecutor.scheduleWithFixedDelay(new FileMonitorTask(), 0, 12, TimeUnit.HOURS);
			
			Server server = new Server();
			SelectChannelConnector connector = new SelectChannelConnector();
			connector.setPort(8880);
			server.addConnector(connector);
			
			ContextHandlerCollection chc = new ContextHandlerCollection();
			server.setHandler(chc);
			
	        ServletContextHandler context = new ServletContextHandler(chc, "/",ServletContextHandler.SESSIONS);
	        context.setContextPath("/");
	        context.setMaxFormContentSize(2500000);

	        // Server content from web
	        ServletHolder holder = context.addServlet(org.eclipse.jetty.servlet.DefaultServlet.class,"/web/*");
	        holder.setInitParameter("resourceBase","web/");
	        holder.setInitParameter("pathInfoOnly","true");
	        
	        context.addServlet(new ServletHolder(new SessionServlet()),"/setSession/*");
	        context.addServlet(new ServletHolder(new XMLGeneratorServlet()),"/getXml/*");
	        context.addServlet(new ServletHolder(new VizMapWrapper()),"/vizmap/*");
	        context.addServlet(new ServletHolder(new GraphData()),"/graph/*");
	        
	        final URL warUrl = CorpusParserTester.class.getClassLoader().getResource("jspweb/");
	        final String warUrlString = warUrl.toExternalForm();
	        WebAppContext warContext = new WebAppContext(warUrlString, "/");
	        chc.addHandler(warContext);
	        	        
			try {
				server.start();
				server.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
//	private static void createDB(Connection conn) throws SQLException
//	{
//		Statement stat = conn.createStatement();
//		
//		stat.execute("CREATE ALIAS IF NOT EXISTS FT_INIT FOR \"org.h2.fulltext.FullText.init\"");
//		stat.execute("CALL FT_INIT()");
//		
//		stat.execute("DROP TABLE AUTHOR IF EXISTS");
//		stat.execute("CREATE TABLE AUTHOR(ID INT AUTO_INCREMENT PRIMARY KEY, NAME VARCHAR)");
//		
//		stat.execute("DROP TABLE CORPUS IF EXISTS");
//		stat.execute("CREATE TABLE CORPUS(ID INT AUTO_INCREMENT PRIMARY KEY, TITLE VARCHAR, PUBLISH_DATE DATE, FILE_PATH VARCHAR, LEAD_PARAGRAPH VARCHAR, TEXT CLOB,SOURCE_ID INT)");
//		
//		stat.execute("DROP TABLE AUTHOR_CORPUS IF EXISTS");
//		stat.execute("CREATE TABLE AUTHOR_CORPUS(AUTHOR_ID INT, CORPUS_ID INT)");
//		
//		stat.execute("DROP TABLE CATEGORY IF EXISTS");
//		stat.execute("CREATE TABLE CATEGORY(ID INT AUTO_INCREMENT PRIMARY KEY, NAME VARCHAR, PARENT_ID INT)");
//		
//		stat.execute("DROP TABLE KEYWORD IF EXISTS");
//		stat.execute("CREATE TABLE KEYWORD(ID INT AUTO_INCREMENT PRIMARY KEY, NAME VARCHAR)");
//		
//		stat.execute("DROP TABLE ENTITY IF EXISTS");
//		stat.execute("CREATE TABLE ENTITY(ID INT AUTO_INCREMENT PRIMARY KEY, NAME VARCHAR)");
//		
//		stat.execute("DROP TABLE CATEGORY_CORPUS IF EXISTS");
//		stat.execute("CREATE TABLE CATEGORY_CORPUS(CATEGORY_ID INT, CORPUS_ID INT)");
//		
//		stat.execute("DROP TABLE KEYWORD_CORPUS IF EXISTS");
//		stat.execute("CREATE TABLE KEYWORD_CORPUS(KEYWORD_ID INT, CORPUS_ID INT)");
//		
//		stat.execute("DROP TABLE ENTITY_CORPUS IF EXISTS");
//		stat.execute("CREATE TABLE ENTITY_CORPUS(ENTITY_ID INT, CORPUS_ID INT)");
//		
//		stat.execute("DROP TABLE SOURCE IF EXISTS");
//		stat.execute("CREATE TABLE SOURCE(ID INT AUTO_INCREMENT PRIMARY KEY, NAME VARCHAR, URL VARCHAR, LOCATION VARCHAR)");
//	}
}
