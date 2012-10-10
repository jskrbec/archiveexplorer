package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class EntityList extends ArrayList<Entity>
{
	private static final long serialVersionUID = 1L;

	private Connection m_conn;
	private MonitoringDB m_monitoringDB = new MonitoringDB("EntityList.java");
	
	public EntityList(Connection conn)
	{
		m_conn = conn;
	}
	
	public HashMap<Integer, String> getByIds(String ids)
	{
		HashMap<Integer, String> entityMap = new HashMap<Integer, String>();
		String query = "SELECT e.id, e.display_name FROM ENTITY e WHERE  e.id  IN (" + ids + ")";
		try {
			long timeStart = System.currentTimeMillis();
			ResultSet rs = m_conn.prepareStatement(query).executeQuery();
			long timeStop = System.currentTimeMillis();
			m_monitoringDB.queryTime(timeStart, timeStop, query,20);
		while (rs.next())
		{
			String displayName = replaceChars(rs.getString("display_name")); 
			entityMap.put(rs.getInt("id"), displayName);
		}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return entityMap;
	}
	
	public void getEntitiesByArticleIds(String artIds, String type, int limit)
	{
		try {
			String query;
			if (artIds == null || artIds.isEmpty())
			{
				query = "SELECT e.id, e.display_name FROM ENTITY e LIMIT 30";
			}
			else if (type != null)
			{
				query = "SELECT COUNT(e.id) as cnt, e.id, e.display_name FROM ENTITY e, ENTITY_CORPUS ec WHERE  e.id = ec.entity_id AND e.type = '" + type + "' AND ec.corpus_id IN (" + artIds + ") GROUP BY e.id,e.display_name ORDER BY cnt DESC LIMIT " + limit;
			}
			else
			{
            	query = "SELECT COUNT(e.id) as cnt, e.id, e.display_name FROM ENTITY e, ENTITY_CORPUS ec WHERE  e.id = ec.entity_id AND ec.corpus_id IN (" + artIds + ") GROUP BY e.id,e.display_name ORDER BY cnt DESC LIMIT " + limit;
			}
			ResultSet rs = m_conn.prepareStatement(query).executeQuery();
			while (rs.next())
			{
				Entity ent = new Entity(rs.getInt("id"),rs.getString("display_name"));
				this.add(ent);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void getEntitiesForXml(String articleIds, String mainType)
	{
		String query;
		if (mainType == null)
		{
			query = "select q.id, q.name, ec.corpus_id FROM entity_corpus ec,(select e.id as id, e.display_name as name from entity e,entity_corpus ec " +
						"WHERE e.type not in ('location','organization','person') " +
							"AND e.id = ec.entity_id AND ec.corpus_id in (" + articleIds + ")group by e.id, e.display_name order by count(e.id) DESC limit 10) q " +
					"WHERE q.id = ec.entity_id AND ec.corpus_id in (" + articleIds + ") order by q.id";
		}
		else
		{
			query = "select q.id, q.name,ec.corpus_id from entity_corpus ec,(" +
			"select e.id as id, e.display_name as name	from entity_corpus ec inner join entity e on ec.entity_id = e.id " +
			"WHERE e.type = '" + mainType + "'	AND ec.corpus_id in (" + articleIds + ") group by e.id, e.display_name order by count(e.id) DESC limit 10) q " +
		"where q.id = ec.entity_id and ec.corpus_id IN (" + articleIds + ") order by q.id";
		}
		
		try {
			long timeStart = System.currentTimeMillis();
			ResultSet rs = m_conn.prepareStatement(query).executeQuery();
			long timeStop = System.currentTimeMillis();
			m_monitoringDB.queryTime(timeStart, timeStop, query,0);
			Entity temp = null;
			while (rs.next())
			{
				int id = rs.getInt("id");
				if (temp == null)
				{
					temp = new Entity(rs.getInt("id"),rs.getString("name"));
					temp.addArticleId(rs.getInt("corpus_id"));
				}
				else if (id == temp.getId())
				{
					temp.addArticleId(rs.getInt("corpus_id"));
				}
				else
				{
					Entity tempx = new Entity(temp.getId(), temp.getName());
					tempx.setArticleIds(temp.getArticleIds());
					add(tempx);
					temp = null;
					temp = new Entity(rs.getInt("id"),rs.getString("name"));
					temp.addArticleId(rs.getInt("corpus_id"));
				}
			}
			if (temp != null)
			{
				Entity tempx = new Entity(temp.getId(), temp.getName());
				tempx.setArticleIds(temp.getArticleIds());
				add(tempx);
			}
		} catch (SQLException e) {
			e.printStackTrace();
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
}
