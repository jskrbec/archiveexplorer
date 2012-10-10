package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ArticleList extends ArrayList<Article>
{
	private static final long serialVersionUID = 1L;
	private Connection m_conn;
	private MonitoringDB m_monitoringDB;

	public ArticleList(Connection conn)
	{
		m_conn = conn;
		m_monitoringDB = new MonitoringDB("ArticleList.java");
	}
	
	public void getArticlesByEntity(String id)
	{
		getArticlesByX("entity", id);
	}
	
	public void getArticlesByKeyword(String id)
	{
		getArticlesByX("keyword", id);
	}
	
	public void getArticlesByCategory(String id)
	{
		getArticlesByX("category", id);
	}
	public void getArticlesByAuthor(String id)
	{
		getArticlesByX("author", id);
	}
	
	private void getArticlesByX(String x, String id)
	{
		try {
			String query = "select q.cnt,c.id, c.title,c.publish_date,c.lead_paragraph,c.text  from corpus c,(select COUNT(corpus_id) as cnt, corpus_id 	from " + x + "_corpus where " + x + "_id = " + id + " group by corpus_id ORDER BY cnt DESC LIMIT 500 ) q where c.id = q.corpus_id order by q.cnt desc";
//				query = "SELECT COUNT(c.id) as cnt, c.id, c.title,c.publish_date FROM CORPUS c, " + x + "_CORPUS cc WHERE  c.id = cc.corpus_id AND cc." + x +"_id = " + id + " GROUP BY c.id, c.title,c.publish_date ORDER BY cnt DESC LIMIT 100";
			long timeStart = System.currentTimeMillis();
			ResultSet rs = m_conn.prepareStatement(query).executeQuery();
			long timeStop = System.currentTimeMillis();
			m_monitoringDB.queryTime(timeStart, timeStop, query,2);
			while (rs.next())
			{
				Article a = new Article(rs.getInt("id"), rs.getString("title"),rs.getDate("publish_date"),rs.getString("lead_paragraph"));
				a.setText(rs.getString("text"));
				a.setSearchScore(rs.getInt("cnt"));
				this.add(a);
			}
			rs.close();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public HashMap<Integer, Article> getArticlesByYear(int minYear, int maxYear)
	{
		HashMap<Integer, Article> articleMap = new HashMap<Integer, Article>();
		try {
			String query;
			if (maxYear > 0)
            {
				query = "SELECT id, title,publish_date FROM CORPUS WHERE  PUBLISH_DATE < '" + (maxYear+1) + "-01-01' AND PUBLISH_DATE > '" + (minYear-1) + "-12-31' LIMIT 100";
            }
            else
            {
            	query = "SELECT c.id, c.title,c.publish_date,c.lead_paragraph, c.text,q.cnt " +
            			"FROM CORPUS c, (SELECT count(ec.entity_id) as cnt, ec.corpus_id FROM entity_corpus ec WHERE  corpus_id in (select id from corpus where PUBLISH_DATE > '" + (minYear-1) + "-12-31' AND PUBLISH_DATE < '" + (minYear+1) + "-01-01') group by ec.corpus_id order by cnt desc limit 200) q " +
            			"WHERE  c.id = q.corpus_id";
            }
			
			long timeStart = System.currentTimeMillis();
			ResultSet rs = m_conn.prepareStatement(query).executeQuery();
			long timeStop = System.currentTimeMillis();
			m_monitoringDB.queryTime(timeStart, timeStop, query,0);
			while (rs.next())
			{
				Article a = new Article(rs.getInt("id"), rs.getString("title"),rs.getDate("publish_date"), rs.getString("lead_paragraph"));
				a.setText(rs.getString("text"));
				a.setSearchScore(rs.getInt("cnt"));
				articleMap.put(rs.getInt("id"),a);
			}
			rs.close();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		return articleMap;
	}
	
	public void getArticlesForEnrycher() throws SQLException
	{
		String query = "SELECT id,title,publish_date,lead_paragraph, text FROM CORPUS WHERE enryched = 'false' LIMIT 200";
		long timeStart = System.currentTimeMillis();
		ResultSet rs = m_conn.prepareStatement(query).executeQuery();
		long timeStop = System.currentTimeMillis();
		m_monitoringDB.queryTime(timeStart, timeStop, query,0);
		while (rs.next())
		{
			Article a = new Article(rs.getInt("id"), rs.getString("title"), rs.getDate("publish_date"), rs.getString("lead_paragraph"));
			a.setText(rs.getString("text"));
			this.add(a);
		}
		rs.close();
	}
	
	public void setEnryched(String ids) throws SQLException
	{
		String query = "update corpus set enryched = 'true' where id in (" + ids + ")";
		long timeStart = System.currentTimeMillis();
		m_conn.createStatement().executeUpdate(query);
		long timeStop = System.currentTimeMillis();
		m_monitoringDB.queryTime(timeStart, timeStop, query,0);
	}
	
	public void getData() throws SQLException
	{
		String ids = "";
		HashMap<Integer, Integer> scores = new HashMap<Integer, Integer>();
		for (Article a : this)
		{
			ids = ids + "," + a.getId();
			scores.put(a.getId(), a.getSearchScore());
		}
		ids = ids.substring(1);
		String query = "SELECT id,title,publish_date,lead_paragraph FROM CORPUS WHERE id IN (" + ids + ")";
		long timeStart = System.currentTimeMillis();
		ResultSet rs = m_conn.prepareStatement(query).executeQuery();
		long timeStop = System.currentTimeMillis();
		m_monitoringDB.queryTime(timeStart, timeStop, query,0);
		this.clear();
		while (rs.next())
		{
			Article a = new Article(rs.getInt("id"), rs.getString("title"), rs.getDate("publish_date"), rs.getString("lead_paragraph"));
			a.setSearchScore(scores.get(a.getId()));
			this.add(a);
		}
		rs.close();
	}
	
	public void getByIds(String ids) throws SQLException
	{
		String query = "SELECT id,title,publish_date,lead_paragraph FROM CORPUS WHERE id IN (" + ids + ")";
		long timeStart = System.currentTimeMillis();
		ResultSet rs = m_conn.prepareStatement(query).executeQuery();
		long timeStop = System.currentTimeMillis();
		m_monitoringDB.queryTime(timeStart, timeStop, query,0);
		while (rs.next())
		{
			Article a = new Article(rs.getInt("id"), rs.getString("title"), rs.getDate("publish_date"), rs.getString("lead_paragraph"));
			this.add(a);
		}
		rs.close();
	}
	
	//for showing a year(or a couple of months) timegraph - articles by month
	public LinkedHashMap<String,ArrayList<Integer>> getMonthYearArticles(int yearMin, int yearMax) throws SQLException
	{
		LinkedHashMap<String,ArrayList<Integer>> yearMonthArticles = new LinkedHashMap<String,ArrayList<Integer>>();
		String query = "SELECT EXTRACT(YEAR FROM PUBLISH_DATE) as year, EXTRACT(MONTH FROM PUBLISH_DATE)as month, id FROM CORPUS " +
				"WHERE PUBLISH_DATE <= '" + yearMax + "-12-31' AND PUBLISH_DATE >= '" + yearMin + "-01-01' GROUP BY year,month,id ORDER BY year, month, id";
		long timeStart = System.currentTimeMillis();
		ResultSet rs = m_conn.prepareStatement(query).executeQuery();
		long timeStop = System.currentTimeMillis();
		m_monitoringDB.queryTime(timeStart, timeStop, query,0);
		while (rs.next())
		{
			String monthYear = rs.getInt("month") + "." + rs.getInt("year");
			if (yearMonthArticles.keySet().contains(monthYear))
			{
				yearMonthArticles.get(monthYear).add(rs.getInt("id"));
			}
			else
			{
				yearMonthArticles.put(monthYear, new ArrayList<Integer>());
				yearMonthArticles.get(monthYear).add(rs.getInt("id"));
			}
		}
		rs.close();
		return yearMonthArticles;
	}
	
	public ArrayList<Integer> getAllYears()
	{
		ArrayList<Integer> years = new ArrayList<Integer>();
		for (Article a : this)
		{
			Calendar c = Calendar.getInstance();
			c.setTime(a.getPublishDate());
			Integer year = c.get(Calendar.YEAR);
			if (!years.contains(year))
			{
				years.add(year);
			}
		}
		Collections.sort(years);
		return years;
	}
}
