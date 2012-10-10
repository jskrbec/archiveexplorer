package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;


public class Category 
{
	private String m_name;
	private int m_id;
	private int m_parentId;
	private String m_parent_path;
	private CategoriesList m_parents;
	private CategoriesList m_children;
	private CategoriesList m_relatedCategories;
	private Connection m_conn;
	private MonitoringDB m_monitoringDB = new MonitoringDB("Category.java");
	
	public Category(int id, String name, int parentId, String parent_path)
	{
		m_name = name;
		m_id = id;
		m_parentId = parentId;
		m_parent_path = parent_path;
	}
	public Category(int id,Connection conn)
	{
		m_id = id;
		m_conn = conn;
	}
	
	public void setName(String name)
	{
		m_name = name;
	}
	public String getName()
	{
		return m_name;
	}
	public void setId(int id)
	{
		m_id = id;
	}
	public int getId()
	{
		return m_id;
	}
	public void setParentId(int parentId)
	{
		m_parentId = parentId;
	}
	public int getParentId()
	{
		return m_parentId;
	}
	public String getParentPath()
	{
		return m_parent_path;
	}
	public CategoriesList getParents()
	{
		return m_parents;
	}
	public CategoriesList getChildren()
	{
		return m_children;
	}
	public CategoriesList getRelatedCategories()
	{
		return m_relatedCategories;
	}
	
	public void findParentsForCategory(Connection conn)
	{
		try
		{
			m_parents = new CategoriesList(conn);
			int parentId = getParentId();
			while (parentId != 0)
			{
				String query = "SELECT c.name, c.parent_id,c.parent_path FROM CATEGORY c WHERE  c.id = " + parentId;
				long timeStart = System.currentTimeMillis();
				ResultSet parentRS = conn.createStatement().executeQuery(query);
				long timeStop = System.currentTimeMillis();
				m_monitoringDB.queryTime(timeStart, timeStop, query,0);
				parentRS.next();
				m_parents.add(new Category(parentId,parentRS.getString("name"),parentRS.getInt("parent_id"),parentRS.getString("parent_path")));
				parentId = parentRS.getInt("parent_id");
			}
			Collections.reverse(m_parents);
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	public void findChildren()
	{
		m_children = new CategoriesList(m_conn);
		try {
			String query = "SELECT c.id, c.name,c.parent_path FROM CATEGORY c WHERE  c.parent_id = " + m_id;
			long timeStart = System.currentTimeMillis();
			ResultSet rs = m_conn.createStatement().executeQuery(query);
			long timeStop = System.currentTimeMillis();
			m_monitoringDB.queryTime(timeStart, timeStop, query,0);
			while (rs.next())
	        {
	          m_children.add(new Category(rs.getInt("id"),rs.getString("name"),m_id,rs.getString("parent_path")));
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void findRelatedCategories()
	{
		m_relatedCategories = new CategoriesList(m_conn);
		try {
			String query = "SELECT c.id, c.name,c.parent_path FROM CATEGORY c WHERE  c.parent_id = " + m_parentId + " AND c.id NOT IN (" + m_id + ")";
			long timeStart = System.currentTimeMillis();
			ResultSet rs = m_conn.createStatement().executeQuery(query);
			long timeStop = System.currentTimeMillis();
			m_monitoringDB.queryTime(timeStart, timeStop, query,0);
			while (rs.next())
	        {
	          m_relatedCategories.add(new Category(rs.getInt("id"),rs.getString("name"),m_parentId,rs.getString("parent_path")));
	        }
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void getCategory() throws SQLException
	{
		String query = "SELECT c.name, c.parent_id FROM CATEGORY c WHERE  c.id = " + m_id;
		long timeStart = System.currentTimeMillis();
		ResultSet rs = m_conn.createStatement().executeQuery(query);
		long timeStop = System.currentTimeMillis();
		m_monitoringDB.queryTime(timeStart, timeStop, query,0);
		while (rs.next())
        {
      	  m_name = rs.getString("name");
      	  m_parentId = rs.getInt("parent_id");
        }
	}
	@Override
	public boolean equals(Object c)
	{
		return ((Category) c).getId() == m_id;
	}
}
