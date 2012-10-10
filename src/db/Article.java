package db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import si.ijs.enrycher.doc.Annotation;

public class Article
{
	private Integer m_id;
	private String m_title;
	private Date m_publishDate;
	private String m_leadParagraph;
	private String m_text;
	private AuthorList m_authors;
	private int m_searchScore;
	private Connection m_conn;
	
	private List<String> categories;
	private List<String> keywords;
	private List<Annotation> annotations;
	private MonitoringDB m_monitoringDB;
	
	public Article(int id)
	{
		m_id = id;
		m_monitoringDB = new MonitoringDB("Article.java");
	}
	
	public Article(int id, String title,Date publishDate, String leadP)
	{
		m_id = id;
		m_title = title;
		m_publishDate = publishDate;
		m_leadParagraph = leadP;
		m_monitoringDB = new MonitoringDB("Article.java");
	}
	
	public Article(Connection conn, Integer id)
	{
		m_conn = conn;
		m_id = id;
		m_monitoringDB = new MonitoringDB("Article.java");
	}
	
	public int getId() 
	{
		return m_id;
	}
	public String getTitle() 
	{
		return m_title;
	}
	public void setTitle(String title) 
	{
		this.m_title = title;
	}
	public Date getPublishDate() 
	{
		return m_publishDate;
	}
	public void setPublishDate(Date publishDate) 
	{
		this.m_publishDate = publishDate;
	}
	public String getLeadParagraph() 
	{
		return m_leadParagraph;
	}
	public void setLeadParagraph(String leadParagraph) 
	{
		this.m_leadParagraph = leadParagraph;
	}
	public void setText(String text)
	{
		m_text = text;
	}
	public String getText()
	{
		return m_text;
	}
	public AuthorList getAuthors() 
	{
		return m_authors;
	}
	 
	public String getAuthorString()
	{
		String authorsString = "";
        for (Author author : getAuthors())
        {
            authorsString = authorsString + ", " + author.getName();
        }
        authorsString = authorsString.isEmpty() ? authorsString : authorsString.substring(1);
        return authorsString;
	}
	 
	public AuthorList getAuthors(Connection conn)
	{
		m_authors = new AuthorList(conn);
		try
		{
			m_authors.getAuthorsStringByArticleId(m_id);
			return m_authors;
		}catch (SQLException e) {
			// do nothing
		}
		return null;
	}
	
	public int getSearchScore()
	{
		return m_searchScore;
	}
	public void setSearchScore(int score)
	{
		m_searchScore = score;
	}
	
	public void readArticleFromDB()
	{
		if (m_id != null)
		{
			ResultSet rs;
			try {
				String query = "SELECT title, publish_date, lead_paragraph, text FROM CORPUS WHERE id = " + m_id;
				long timeStart = System.currentTimeMillis();
				rs = m_conn.createStatement().executeQuery(query);
				long timeStop = System.currentTimeMillis();
				m_monitoringDB.queryTime(timeStart, timeStop, query,0);

				while (rs.next())
				{        		
					m_text = rs.getString("text");
					m_title = rs.getString("title");
					m_title = m_title == null ? "" : m_title;
					m_publishDate = rs.getDate("publish_date");
					m_leadParagraph = rs.getString("lead_paragraph");
					m_leadParagraph = m_leadParagraph == null ? "" : m_leadParagraph;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setCategories(List<String> cat) 
	{
		categories = cat;
	}
	public void setKeywords(List<String> kws) 
	{
		keywords = kws;
	}
	public void setEntities(List<Annotation> annot) 
	{
		annotations = annot;
	}
	public void addEntity(Annotation entity)
	{
		annotations.add(entity);
	}
	
	public void saveEnrychedData(Connection conn) throws SQLException
	{		
			CategoriesList catList = new CategoriesList(conn);
			catList.save(categories, m_id);
			
			saveKeywords(keywords, conn);
			
			saveEntities(annotations, conn);
	}
	
	private void saveKeywords(List<String> keywords, Connection conn) throws SQLException
	{
		String kwNames = "";
		for (String keyword : keywords)
		{
			keyword = replaceChars(keyword);
			kwNames = kwNames + ",'" + keyword.toLowerCase() + "'";
		}
		kwNames = kwNames.substring(1);	
		String query = "SELECT id,name FROM KEYWORD WHERE lower(name) IN (" + kwNames + ")";
		long timeStart = System.currentTimeMillis();
		ResultSet rs = conn.createStatement().executeQuery(query);
		long timeStop = System.currentTimeMillis();
		m_monitoringDB.queryTime(timeStart, timeStop, query,10);

		ArrayList<Integer> kwIds = new ArrayList<Integer>();
		ArrayList<String> kwNamesDb = new ArrayList<String>();
		while (rs.next())
		{
			kwIds.add(rs.getInt("id"));
			kwNamesDb.add(rs.getString("name").toLowerCase());
		}
		for (String keyword : keywords)
		{
			if (!kwNamesDb.contains(keyword.toLowerCase()))
			{
				String sql = "INSERT INTO KEYWORD (name) VALUES (?) RETURNING id";
				PreparedStatement insertKeyword = conn.prepareStatement(sql);
				insertKeyword.setString(1, keyword);
				insertKeyword.execute();		
				ResultSet keywordIds = insertKeyword.getResultSet();
				keywordIds.next();
				kwIds.add(keywordIds.getInt(1));
				insertKeyword.close();
			}
		}
		String insertIds = "";
		for (Integer kwId : kwIds)
		{
			insertIds = insertIds + ",('" + kwId + "','" + m_id + "')";
		}
		insertIds = insertIds.substring(1);
		conn.createStatement().execute("INSERT INTO KEYWORD_CORPUS (keyword_id,corpus_id) VALUES " + insertIds);
			
	}
	
	private void saveEntities(List<Annotation> annotations, Connection conn) throws SQLException
	{	
		ArrayList<Integer> enIds = new ArrayList<Integer>();
		ArrayList<String>	enNamesDb = new ArrayList<String>();
		
		String entityNames = "";
		for (Annotation annotation : annotations)
		{
			entityNames = entityNames + ",'" + replaceChars(annotation.getDisplayName().toLowerCase()) + "'";
		}
		if (!entityNames.isEmpty())
		{
			entityNames = entityNames.substring(1);
			String query = "SELECT id,name FROM ENTITY WHERE lower(name) IN (" + entityNames + ")";
			long timeStart = System.currentTimeMillis();
			ResultSet rs = conn.createStatement().executeQuery(query);
			long timeStop = System.currentTimeMillis();
			m_monitoringDB.queryTime(timeStart, timeStop, query,10);
			while (rs.next())
			{
				enIds.add(rs.getInt("id"));
				enNamesDb.add(rs.getString("name").toLowerCase());
			}
		}
		for (Annotation annotation : annotations)
		{
			if (!enNamesDb.contains(replaceChars(annotation.getDisplayName().toLowerCase())))
			{
				Entity entity = new Entity(conn);
				entity.setName(replaceChars(annotation.getDisplayName()));				
				entity.setUri(null);
				entity.setType(annotation.getType());
				entity.save();
				Integer entityId = entity.getId();
				enIds.add(entityId);
				
				Entity_Type entityType = new Entity_Type(conn);
				entityType.setComment(annotation.getType());
				entityType.setPredicate(annotation.getType());
				entityType.setType(null);
				entityType.setValue(annotation.getDisplayName());
				entityType.save();
				Integer typeId = entityType.getId();
				conn.createStatement().execute("INSERT INTO ENTITY_ENTITY_TYPE (entity_id,entity_type_id) VALUES ( '" + 
						entityId + "', '" + typeId + "')");
			}
		}
		String insertIds = "";
		for (Integer enId : enIds)
		{
			insertIds = insertIds + ",('" + enId + "','" + m_id + "')";
		}
		if (!insertIds.isEmpty())
		{
			insertIds = insertIds.substring(1);
			conn.createStatement().execute("INSERT INTO ENTITY_CORPUS (entity_id,corpus_id) VALUES " + insertIds);
		}
	}
	
	private String replaceChars(String s)
	{
		if (s != null && !s.isEmpty())
		{
			s = s.replaceAll("&apos;", "&#39;");
			s = s.replaceAll("'", "&#39;");
			s = s.replaceAll("\"", "&quot;");
		}
		return s;
	}
	
	@Override
	public boolean equals(Object a)
	{
		return ((Article) a).getId() == m_id;
	}
}
