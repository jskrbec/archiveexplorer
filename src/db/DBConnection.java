package db;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class DBConnection 
{
	private Connection m_conn;
	private String m_file;
	
	public DBConnection(String fileCreating, String remoteAddr) throws ClassNotFoundException, SQLException, IOException
	{
		m_file = fileCreating;
		Class.forName("org.postgresql.Driver");
		Properties props = new Properties();
	    props.put("user","appUser");
	    props.put("password","bla");
//	    props.put("defaultRowPrefetch","1000");
//	    props.put("defaultBatchValue","10");
        m_conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/corpusdb", props);
        Date d = new Date(System.currentTimeMillis());
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
        String dateFormatted = formatter.format(d);
        System.out.println(dateFormatted + " (" + m_file + "  - " + remoteAddr + ") creating new connection...");
        PrintWriter output = new PrintWriter(new FileWriter("conn_log.txt", true));
        output.println(dateFormatted + " - " + remoteAddr);
        output.close();
	}
	
	public Connection getConnection()
	{
		return m_conn;
	}
}
