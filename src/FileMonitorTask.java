import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import parsers.CorpusParser;
import parsers.CorpusParserList;
import db.Article;
import db.ArticleList;

public class FileMonitorTask implements Runnable 
{
	Connection m_conn = null;
	AtomicInteger threadCount;

	@Override
	public void run() {
		threadCount = new AtomicInteger(0);
		String path = "D:/ArchiveExplorer/import/";
//		String path = "G:/Users/jasna.MARQUIS/projects/fileimport/";
//		String path = "D:/Users/jasna/projects/fileimport";
		Calendar now = Calendar.getInstance();
		File errFile = new File(path + "FileImport_" + now.get(Calendar.YEAR) + now.get(Calendar.MONTH)+now.get(Calendar.DATE)+ "_" + now.get(Calendar.HOUR_OF_DAY) + now.get(Calendar.MINUTE) + ".err");
		try {
			errFile.createNewFile();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		FileOutputStream fosErr = null;
		try {
			fosErr = new FileOutputStream(errFile);
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		DataOutputStream dosErr = new DataOutputStream(fosErr);
		
		try {	
			System.out.println("******Running fileMonitorTask...");
			// Import files
			
			File dir = new File(path);

			if (dir.isDirectory())
			{
				FilenameFilter filter = new FilenameFilter() {
					public boolean accept(File directory, String name) {
						return !name.endsWith(".done") && !name.endsWith(".err");
					}
				};

				String[] children = dir.list(filter); 
				if (children == null) {
					return;
				} 
				else 
				{					
					try {
						System.out.println("(FileMonitorTask) creating new connection...");
						Class.forName("org.postgresql.Driver");
						m_conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/corpusdb", "appUser", "bla");
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
						dosErr.writeChars(e1.getMessage());
						System.exit(1);
					} catch (SQLException e) {
						e.printStackTrace();
						dosErr.writeChars(e.getMessage());
						System.exit(1);
					}
					for (String filename : children) {
						// Get filename of file or directory
						importAllSubDirs(filename, path, dosErr);
						System.out.println(path + " " + filename + " imported to db.");
					}
					
					System.out.println("Sending to enrycher....");
					boolean articlesInDb = true;
					while (articlesInDb)
					{
						ArticleList savedArticles = new ArticleList(m_conn);
						savedArticles.getArticlesForEnrycher();
						if (savedArticles.isEmpty())
						{
							System.out.println("No articles to send.");
							break;
						}

						String enrychedIds = "";
						for (Article a : savedArticles)
						{
//							enrychSavedArticle(a);
							String text = a.getTitle() + " " +  a.getLeadParagraph() + " " + a.getText();

							if (text != null && !text.isEmpty())
							{
								while (true)
								{
									if (threadCount.intValue() < 9)
									{
										threadCount.incrementAndGet();
										EnrycherThread enrycherThread = new EnrycherThread(a,threadCount);
										Thread thread = new Thread(enrycherThread);
										thread.start();
										enrychedIds = enrychedIds + ", " + a.getId();
										break;
									}
									else
									{
										TimeUnit.MILLISECONDS.sleep(100);
									}
								}
							}
						}

						System.out.println("Send to enrycher: " + enrychedIds.substring(1));
						enrychedIds = enrychedIds.substring(1);
						savedArticles.setEnryched(enrychedIds);
						savedArticles.clear();
					}
					m_conn.close();
				}
			}
		} catch (Exception e) {
			System.out.println("Exception while running task " + e.getMessage());
			e.printStackTrace();
		}
		try {
			dosErr.close();
			fosErr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("******FileMonitorTask ENDED.");
	}
	
	private void importAllSubDirs(String filename, String path, DataOutputStream dosErr) throws IOException
	{
		File file = new File(path + filename);
		if (file.isFile())
		{
			try{			
				CorpusParserList cpl = new CorpusParserList(path + filename);
				cpl.parseList();

				for (CorpusParser cp : cpl)
				{
					try 
					{
						cp.save(m_conn);
					} 
					catch (SQLException e) 
					{
						dosErr.writeChars(e.getMessage());
						e.printStackTrace();
					}
				}
				file.renameTo(new File(path + filename + ".done"));
			}
			catch (Exception e)
			{
				file.renameTo(new File(path + filename + ".err"));
				dosErr.writeChars("************" + path + filename + ": " + e.getMessage());
			}
			
		}
		else if (file.isDirectory())
		{
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File directory, String name) {
					return !name.endsWith(".done");
				}
			};
			String[] children = file.list(filter); 
			for (String child : children)
			{
				importAllSubDirs(child, path + filename + "/",dosErr);
			}
			System.out.println(path + filename + " imported: " + children.length + " files/folders saved" );
		}
	}
}
