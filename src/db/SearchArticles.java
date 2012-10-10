package db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class SearchArticles 
{
	private ArticleList articles;
	private String m_all;
	private String m_allEntities;
	private String m_person;
	private String m_organization;
	private String m_location;
	private String m_keyword;
	private String m_author;
	private String m_category;
	private Date m_date;
	private Connection m_conn;
	private MonitoringDB m_monitoringDB = new MonitoringDB("SearchArticles.java");
	
	public SearchArticles(
			String all, 
			String allEntities,
			String person,
			String organization,
			String location,
			String keyword,
			String author,
			String category,
			Date date,
			Connection conn)
	{
		m_all = (all == null || all.isEmpty()) ? null : all;
		m_allEntities = allEntities == null || allEntities.isEmpty() ? null : allEntities;
		m_person = person == null || person.isEmpty() ? null : person;
		m_organization = organization == null || organization.isEmpty() ? null : organization;
		m_location = location == null || location.isEmpty() ? null : location;
		m_keyword = keyword == null || keyword.isEmpty() ? null : keyword;
		m_author = author == null || author.isEmpty() ? null : author;
		m_category = category == null || category.isEmpty() ? null : category;
		m_date = date;
		m_conn = conn;
		articles = new ArticleList(conn);
	}
	
	public ArticleList getArticles()
	{
		return articles;
	}
	
	//idea: if entity,... not found in entity/... table, we could look with findStringInText
	public void search()
	{		
		if(m_all == null && m_allEntities == null && m_person == null && m_organization == null && 
				m_location == null && m_author == null &&
        		m_keyword == null && m_date == null && 
        		m_category == null)
        {
        	
        	return;
        }
		try {			
        	if (m_all != null)
        	{
        		findEntity(m_all,null,5);
        		findCategory(m_all, 5);

        		findKeyword(m_all, 5);
        		
        		findAuthor(m_all, 5);
        		findStringInText(m_all, 5);
        		for (String s : parseInputString(m_all))
                {
                	if (!s.equals(m_all))
                	{
                		findEntity(s,null,1);
                		findCategory(s,1);
                		findKeyword(s,1);
                		findAuthor(s,1);
        			}
                }
        	}
        	if (m_person != null)
			{
				findEntity(m_person,"person",10);
			}
        	if (m_organization != null)
			{
				findEntity(m_organization,"organization",10);
			}
        	if (m_location != null)
			{
				findEntity(m_location,"location",10);
			}
			if (m_allEntities != null)
			{
				findEntity(m_allEntities,null,10);
			}
			if (m_keyword != null)
			{
				findKeyword(m_keyword, 10);
			}
			if (m_author != null)
			{
				findAuthor(m_author, 10);
			}
			if (m_category != null)
			{
				findCategory(m_category, 10);
			}
			if (m_date != null)
			{
				findDate(m_date, 10);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		if (articles != null && !articles.isEmpty())
		{
			System.out.println("no. of found articles: " + articles.size());
			
			Collections.sort(articles, new Comparator<Article>() {
				public int compare(Article a1, Article a2) {
					return a2.getSearchScore() - a1.getSearchScore();
				}
			});
			if (articles.size() > 100)
			{
				for (int i = articles.size()-1; i > 99 ; i--)
				{
					articles.remove(i);
				}
				articles.trimToSize();
			}
			try {
				articles.getData();
			} catch (SQLException e1) {
				e1.printStackTrace();
				return;
			}
		}
	}
	
	private ArrayList<String> parseInputString(String inputString)
	{
		ArrayList<String> input = new ArrayList<String>();
		inputString = inputString.trim();
		inputString = inputString.replace(',', ' ');
		inputString = inputString.replace('.', ' ');
		inputString = inputString.replace(';', ' ');
		inputString = inputString.replace(':', ' ');
		String[] inputS = inputString.split(" ");
		
		for (String s : inputS)
		{
			s = s.trim();
			if (!s.isEmpty())
			{
				input.add(s);
			}
		}
		return input;
	}
	
	private void findStringInText(String srcString, int inc) throws SQLException
	{		
		try {
			//search text
			String tsQuery = srcString.replace(" ", " & ");
			String query = "SELECT id,ts_rank_cd(vector_text_all, query) AS rank FROM corpus, to_tsquery('" + tsQuery + "') query " +
				"WHERE query @@ vector_text_all ORDER BY rank DESC LIMIT 100";
			long timeStart = System.currentTimeMillis();
			ResultSet rs5 = m_conn.createStatement().executeQuery(query);
			long timeStop = System.currentTimeMillis();
			m_monitoringDB.queryTime(timeStart, timeStop, query,0);
			while (rs5.next())
			{
				addCorpusId(rs5.getInt("id"), rs5.getBigDecimal("rank").intValue() + inc);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private void findEntity(String srcString, String entityType, int inc)
	{
		try {
			Entity entity = new Entity(m_conn);
			entity.setName(srcString);
			ArrayList<Integer> entityIds = entity.getEntityIdByName(entityType);
			if (entityIds != null && !entityIds.isEmpty())
			{
				String entityIdsString = "";
				for (Integer i : entityIds)
				{
					entityIdsString = entityIdsString + i + ",";
				}
				entityIdsString = entityIdsString.substring(0,entityIdsString.length() - 1);
				String query = "SELECT corpus_id FROM ENTITY_CORPUS WHERE entity_id IN (" + entityIdsString + ")";
				long timeStart = System.currentTimeMillis();
				ResultSet rs2 = m_conn.createStatement().executeQuery(query);
				long timeStop = System.currentTimeMillis();
				m_monitoringDB.queryTime(timeStart, timeStop, query,0);
				while (rs2.next())
				{
					int corpusId = rs2.getInt("corpus_id");
					addCorpusId(corpusId, inc+3);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void findKeyword(String srcString, int inc)
	{
		try {
			Keyword keyword = new Keyword(m_conn);
			int keywordId = keyword.getKeywordIdByName(srcString);
			if (keywordId > 0)
			{
				String query = "SELECT corpus_id FROM KEYWORD_CORPUS WHERE keyword_id = " + keywordId;
				long timeStart = System.currentTimeMillis();
				ResultSet rs2 = m_conn.createStatement().executeQuery(query);
				long timeStop = System.currentTimeMillis();
				m_monitoringDB.queryTime(timeStart, timeStop, query,1);
				while (rs2.next())
				{
					int corpusId = rs2.getInt("corpus_id");
					addCorpusId(corpusId, inc + 2);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void findCategory(String srcString, int inc)
	{
		try {
			CategoriesList categoryList = new CategoriesList(m_conn);
			String catIds = "(";
			for (int cat : categoryList.getCategoryIdsByName(srcString))
			{
				catIds = catIds + cat + ",";
			}
			catIds = catIds.substring(0, catIds.length()-1);
			if (catIds != null && !catIds.isEmpty())
			{
				catIds = catIds + ")";
				String query = "SELECT corpus_id FROM CATEGORY_CORPUS WHERE category_id IN " + catIds; //No DISTINCT, cause we want to know how many times it was found
				long timeStart = System.currentTimeMillis();
				ResultSet rs2 = m_conn.createStatement().executeQuery(query); 
				long timeStop = System.currentTimeMillis();
				m_monitoringDB.queryTime(timeStart, timeStop, query,0);
				while (rs2.next())
				{
					int corpusId = rs2.getInt("corpus_id");
					addCorpusId(corpusId, inc + 3);
				}
			}
		}
		catch (SQLException e){
			e.printStackTrace();
		}
	}
	
	private void findAuthor(String srcString, int inc)
	{
		try {
			Author author = new Author(m_conn);
			String autIds = "";
			for (int aut : author.getAuthorIdByName(srcString))
			{
				autIds = autIds + "," + aut;
			}
			if (!autIds.isEmpty())
			{
				autIds = autIds.substring(1);
				autIds = "(" + autIds + ")";
				String query = "SELECT corpus_id FROM AUTHOR_CORPUS WHERE author_id IN " + autIds;
				long timeStart = System.currentTimeMillis();
				ResultSet rs2 = m_conn.createStatement().executeQuery(query);
				long timeStop = System.currentTimeMillis();
				m_monitoringDB.queryTime(timeStart, timeStop, query, 0);
				while (rs2.next())
				{
					int corpusId = rs2.getInt("corpus_id");
					addCorpusId(corpusId, inc);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void findDate(Date date, int inc)
	{
		try {
			String condition = "";
			if (date != null)
			{
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				int year = c.get(Calendar.YEAR);
				condition = "PUBLISH_DATE >= '" + year + "-01-01' AND PUBLISH_DATE <= '" + year + "-12-31'";
				inc = inc + 5;
			}
			String query = "SELECT id FROM CORPUS WHERE " + condition;
			long timeStart = System.currentTimeMillis();
			ResultSet rs = m_conn.createStatement().executeQuery(query);
			long timeStop = System.currentTimeMillis();
			m_monitoringDB.queryTime(timeStart, timeStop, query,0);
			int i = 0;
			while (rs.next())
			{
				if (i%100 == 0)
				{
					int corpusId = rs.getInt("id");
					addCorpusId(corpusId, inc);
				}
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void addCorpusId(int corpusId, int inc)
	{
		if (articles.contains(new Article(corpusId)))
		{
			Article a = articles.get(articles.indexOf(new Article(corpusId)));
			a.setSearchScore(a.getSearchScore() + inc);
		}
		else
		{
			Article a = new Article(corpusId);
			a.setSearchScore(inc);
			articles.add(a);
		}
	}
}
