package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

public class Entity
{
	private Connection m_conn;
	private int m_id;
	private String m_name;
	private String m_dbrLink;
	private String m_uri;
	private String m_type;
	private String m_abstract;
	private String m_picLink;
	private int m_count;
	private ArrayList<Integer> m_articleIds = new ArrayList<Integer>();
	private MonitoringDB m_monitoringDB = new MonitoringDB("Entity.java");
	
	public Entity(Connection conn)
	{
		m_conn = conn;
	}
	
	public Entity (int id, String name)
	{
		m_id = id;
		m_name = name;
	}
	public Entity(String name, String type)
	{
		m_name = name;
		m_type = type;
	}
	
	public ArrayList<Integer> getEntityIdByName(String type) throws Exception
	{
		String nameVariant = null;
		m_name = m_name.trim();
		//this is only for two words, TODO for more words
		if (m_name.contains(" "))
		{
			String[] temp = m_name.split(" ");
			nameVariant = temp[1].trim() + " " + temp[0].trim();
		}
		ArrayList<Integer> ids = new ArrayList<Integer>();
		String query = "SELECT id,type FROM ENTITY " +
					"WHERE lower(display_name) = '" + m_name.toLowerCase() + "' or lower(dbr_link) = '" + m_name.toLowerCase().replace(" ", "_") + "'";
		if (nameVariant != null)
		{
			query = query + " OR lower(display_name) = '" + nameVariant.toLowerCase() + "' or lower(dbr_link) = '" + nameVariant.toLowerCase().replace(" ", "_") + "'";
		}
		if (type != null)
		{
			query = query + " AND type = '" + type.toLowerCase() + "'";
		}
		long timeStart = System.currentTimeMillis();
		ResultSet rs = m_conn.createStatement().executeQuery(query);
		long timeStop = System.currentTimeMillis();
		m_monitoringDB.queryTime(timeStart, timeStop, query,0);
		while (rs.next())
		{
			if (type == null || type.equalsIgnoreCase(rs.getString("type")))
			{
				ids.add(rs.getInt("id"));
			}
		}
		if (ids.isEmpty())
		{
			query = "SELECT id FROM ENTITY " +
						"WHERE lower(display_name) LIKE '" + m_name.toLowerCase() + " %' OR lower(display_name) LIKE '% " + m_name.toLowerCase() + "' or lower(dbr_link) like '" + m_name.toLowerCase().replace(" ", "_") + "%'";
			timeStart = System.currentTimeMillis();
			rs = m_conn.createStatement().executeQuery(query);
			timeStop = System.currentTimeMillis();
			m_monitoringDB.queryTime(timeStart, timeStop, query,0);
			while (rs.next())
			{
				ids.add(rs.getInt("id"));
			}
		}
		return ids;
	}
	
	public void read(String id)
	{
		try{
			m_id = (new Integer(id)).intValue();
			String query = "SELECT dbr_link,display_name,type,abstract,pic_link FROM ENTITY WHERE id = '" + m_id + "'";
			long timeStart = System.currentTimeMillis();
			ResultSet rs = m_conn.createStatement().executeQuery(query);
			long timeStop = System.currentTimeMillis();
			m_monitoringDB.queryTime(timeStart, timeStop, query,0);
			while(rs.next())
			{
				m_dbrLink = rs.getString("dbr_link");
				m_name = rs.getString("display_name");
				m_type = rs.getString("type");
				m_abstract = rs.getString("abstract");
				m_picLink = rs.getString("pic_link");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void save() throws SQLException
	{
		String sql = "INSERT INTO ENTITY (name,uri,type) VALUES (?,?,?) RETURNING id";
		PreparedStatement insertEntity = m_conn.prepareStatement(sql);
		insertEntity.setString(1, getName());
		if(getUri() == null || getUri().isEmpty())
		{
			insertEntity.setNull(2, Types.NULL);
		}
		else
		{
			insertEntity.setString(2, getUri());
		}
		insertEntity.setString(3, getType());
		insertEntity.execute();		
		ResultSet entityIds = insertEntity.getResultSet();
		entityIds.next();
		m_id = entityIds.getInt(1);
		insertEntity.close();
	}
	public void save(Connection conn) throws SQLException
	{
		m_conn = conn;
		save();
	}
	
	public String getName()
	{
		return m_name;
	}
	public void setName(String name)
	{
		m_name = name;
	}
	public int getId()
	{
		return m_id;
	}
	public void setCount(int count)
	{
		m_count = count;
	}
	public int getCount()
	{
		return m_count;
	}
	public void setUri(String uri)
	{
		m_uri = uri;
	}
	public String getUri()
	{
		return m_uri;
	}
	public void addArticleId(Integer articleId)
	{
		m_articleIds.add(articleId);
	}
	public void setArticleIds(ArrayList<Integer> artIds)
	{
		m_articleIds.addAll(artIds);
	}
	public ArrayList<Integer> getArticleIds()
	{
		return m_articleIds;
	}
	public String getType()
	{
		return m_type;
	}
	public void setType(String type)
	{
		type = type.toLowerCase().trim();
		m_type = type;
	}
	public String getAbstract()
	{
		return m_abstract;
	}
	public String getPicLink()
	{
		return m_picLink;
	}
	public String getDbrLink()
	{
		return m_dbrLink;
	}
	
	@Override
	public boolean equals(Object e)
	{
		return ((Entity) e).getId() == m_id;
	}
}
