package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class CategoriesList extends ArrayList<Category>
{
	private static final long serialVersionUID = 1L;
	private Connection m_conn;
	private MonitoringDB m_monitoringDB = new MonitoringDB("CategoriesList.java");
	
	public CategoriesList(Connection conn)
	{
		m_conn = conn;
	}

	public void getCategoriesByParentId(int categoryId) throws SQLException
	{
		String query = null;
		if (categoryId == 0)
		{
			query = "SELECT id, name FROM CATEGORY WHERE parent_id IS NULL";
		}
		else
		{
			query = "SELECT id, name FROM CATEGORY WHERE parent_id = " + categoryId;
		}

		long timeStart = System.currentTimeMillis();
		ResultSet rs = m_conn.prepareStatement(query).executeQuery();
		long timeStop = System.currentTimeMillis();
		m_monitoringDB.queryTime(timeStart, timeStop, query,0);
		while (rs.next())
		{
			add(new Category(new Integer(rs.getString("id")), rs.getString("name").replace("_", " "), categoryId,null));
		}		
	}
	
	public List<Integer> getCategoryIdsByName(String name) throws SQLException
	{
		List<Integer> categoryIds = new ArrayList<Integer>();
		String query = "SELECT id, parent_id FROM CATEGORY WHERE lower(name) = '" + name.toLowerCase() + "'";
		long timeStart = System.currentTimeMillis();
		ResultSet rs = m_conn.prepareStatement(query).executeQuery();
		long timeStop = System.currentTimeMillis();
		m_monitoringDB.queryTime(timeStart, timeStop, query,0);
		while (rs.next())
		{
			categoryIds.add(rs.getInt("id"));
		}
		return categoryIds;
	}
	
	public void getCategoriesByArticleIds(String articleIds, int limit)
	{
		if (articleIds != null && !articleIds.isEmpty())
		{
			try
			{
				String query = "SELECT cc.cnt, c.id, c.name, c.parent_id, c.parent_path FROM CATEGORY c,(select COUNT(category_id) as cnt, category_id from category_corpus where corpus_id IN (" + articleIds + ") group by category_id ORDER BY cnt DESC LIMIT 30 ) cc WHERE  c.id = cc.category_id ORDER BY cc.cnt DESC LIMIT " + limit;
				long timeStart = System.currentTimeMillis();
				ResultSet rs = m_conn.prepareStatement(query).executeQuery();
				long timeStop = System.currentTimeMillis();
				m_monitoringDB.queryTime(timeStart, timeStop, query,0);
				while (rs.next())
				{
					Category cat = new Category(rs.getInt("id"), rs.getString("name"),rs.getInt("parent_id"),rs.getString("parent_path"));
					this.add(cat);
				}
			}
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public void save(List<String> categories, int articleId) throws SQLException
	{
		Integer parent = null;
		int id = 0;
		
		for (String category : categories)
		{
			parent = null;
			boolean parentInDb = true;
			for (String c : category.split("/"))
			{
				id = 0;
				if (!c.equalsIgnoreCase("Top"))
				{
					if (parentInDb)
					{
						try
						{
							String cTemp = c.replace("\'", "\\'");
							String query = "SELECT id FROM CATEGORY WHERE name = '" + cTemp + "' AND parent_id " + (parent == null ? "IS NULL" : "= " + parent);
							long timeStart = System.currentTimeMillis();
							ResultSet rs = m_conn.prepareStatement(query).executeQuery();
							long timeStop = System.currentTimeMillis();
							m_monitoringDB.queryTime(timeStart, timeStop, query,10);
							while(rs.next())
							{
								id = rs.getInt("id");
							}
						}
						catch(SQLException e)
						{
							//category doesn't exist in db
						}
						if (id == 0)
						{
							parentInDb = false;
						}
						else
						{
							parent = id;
						}
					}
					if (!parentInDb)
					{
						String sql = "INSERT INTO CATEGORY (name, parent_id) VALUES (?,?) RETURNING id";
						PreparedStatement insertCategory = m_conn.prepareStatement(sql);
						insertCategory.setString(1, c);
						if (parent == null) 
						{ 
							insertCategory.setNull(2, Types.INTEGER);
						}
						else
						{
							insertCategory.setInt(2, parent);
						}
						insertCategory.execute();		
						ResultSet categoryIds = insertCategory.getResultSet();
						categoryIds.next();
						id = categoryIds.getInt(1);
						insertCategory.close();
						parent = id;
					}
				}
			}
			String query = "SELECT * FROM CATEGORY_CORPUS WHERE category_id = " + id + " AND corpus_id = " + articleId;
			long timeStart = System.currentTimeMillis();
			ResultSet rs = m_conn.prepareStatement(query).executeQuery();
			long timeStop = System.currentTimeMillis();
			m_monitoringDB.queryTime(timeStart, timeStop, query,10);
			
			if (!rs.next())
			{
				m_conn.createStatement().execute("INSERT INTO CATEGORY_CORPUS (category_id,corpus_id) VALUES ( '" + 
						id + "', '" + articleId + "')");
			}
		}
	}
} 