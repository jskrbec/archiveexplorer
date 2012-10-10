package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class KeywordList extends ArrayList<Keyword>
{
	private static final long serialVersionUID = 1L;

	private Connection m_conn;
	private MonitoringDB m_monitoringDB = new MonitoringDB("KeywordList.java");
	
	public KeywordList(Connection conn)
	{
		m_conn = conn;
	}
	
	public void getByIds(String ids)
	{
		String query = "SELECT k.id, k.name FROM KEYWORD k WHERE  k.id  IN (" + ids + ")";
		try {
			long timeStart = System.currentTimeMillis();
			ResultSet rs = m_conn.prepareStatement(query).executeQuery();
			long timeStop = System.currentTimeMillis();
			m_monitoringDB.queryTime(timeStart, timeStop, query,10);
			while (rs.next())
			{
				Keyword kw = new Keyword(rs.getInt("id"),rs.getString("name"));
				this.add(kw);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getKeywordsByArticleIds(String articleIds, int limit)
	{
		try {
			String query;
			if (articleIds == null || articleIds.isEmpty())
			{
				query = "SELECT k.id, k.name FROM KEYWORD k LIMIT " + limit;
			}
			else
			{
            	query = "SELECT COUNT(k.id) as cnt, k.id, k.name FROM KEYWORD k, KEYWORD_CORPUS kc WHERE  k.id = kc.keyword_id AND kc.corpus_id IN (" + articleIds + ") GROUP BY k.id,k.name ORDER BY cnt DESC LIMIT " + limit;
			}
			long timeStart = System.currentTimeMillis();
			ResultSet rs = m_conn.prepareStatement(query).executeQuery();
			long timeStop = System.currentTimeMillis();
			m_monitoringDB.queryTime(timeStart, timeStop, query,0);
			while (rs.next())
			{
				Keyword kw = new Keyword(rs.getInt("id"),rs.getString("name"));
				this.add(kw);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
