package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AuthorList extends ArrayList<Author>
{
	private static final long serialVersionUID = 1L;

	private Connection m_conn;
	private MonitoringDB m_monitoringDB = new MonitoringDB("AuthorList.java");
	
	public AuthorList(Connection conn)
	{
		m_conn = conn;
	}	
	
	public void getByIds(String ids)
	{
		String query = "SELECT a.id, a.name FROM AUTHOR a WHERE  a.id  IN (" + ids + ")";
		try {
			long timeStart = System.currentTimeMillis();
			ResultSet rs = m_conn.prepareStatement(query).executeQuery();
			long timeStop = System.currentTimeMillis();
			m_monitoringDB.queryTime(timeStart, timeStop, query,0);
			while (rs.next())
			{
				Author a = new Author(rs.getInt("id"),rs.getString("name"));
				this.add(a);
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void getAuthorsByArticleIds(String articleIds, String authorIds)
	{
		if (articleIds != null && !articleIds.isEmpty())
		{
			try
			{
				String query;
				if (authorIds == null || authorIds.isEmpty())
				{
					query = "SELECT COUNT(au.id) as cnt, au.id, au.name FROM AUTHOR au, AUTHOR_CORPUS ac WHERE au.id = ac.AUTHOR_ID AND ac.CORPUS_ID IN (" + articleIds + ") GROUP BY au.id,au.name ORDER BY cnt DESC LIMIT 10";
				}
				else
				{
					query = "SELECT COUNT(au.id) as cnt, au.id, au.name FROM AUTHOR au, AUTHOR_CORPUS ac WHERE au.id = ac.AUTHOR_ID AND au.id in (" + authorIds + ") AND ac.CORPUS_ID IN (" + articleIds + ") GROUP BY au.id,au.name ORDER BY cnt DESC LIMIT 10";
				}
				long timeStart = System.currentTimeMillis();
				ResultSet rs = m_conn.prepareStatement(query).executeQuery();
				long timeStop = System.currentTimeMillis();
				m_monitoringDB.queryTime(timeStart, timeStop, query,1);
				while (rs.next())
				{
					this.add(new Author(rs.getInt("id"), rs.getString("name")));
				}
				rs.close();
			}catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	public void getAuthorsStringByArticleId(int articleId) throws SQLException
	{
		String query = "SELECT au.id, au.name FROM AUTHOR au, AUTHOR_CORPUS ac WHERE au.id = ac.AUTHOR_ID AND ac.CORPUS_ID IN (" + articleId + ")";
		long timeStart = System.currentTimeMillis();
		ResultSet rs = m_conn.prepareStatement(query).executeQuery();
		long timeStop = System.currentTimeMillis();
		m_monitoringDB.queryTime(timeStart, timeStop, query,10);
		while (rs.next())
		{
			this.add(new Author(rs.getInt("id"), rs.getString("name")));
		}
		rs.close();
	}
}
