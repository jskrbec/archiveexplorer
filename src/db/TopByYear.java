package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class TopByYear 
{
	Connection m_conn;
	private MonitoringDB m_monitoringDB = new MonitoringDB("TopByYear.java");
	HashMap<Integer, ArrayList<Integer>> people;
	HashMap<Integer, ArrayList<Integer>> locations;
	HashMap<Integer, ArrayList<Integer>> entities;
	HashMap<Integer, ArrayList<Integer>> keywords;
	HashMap<Integer, ArrayList<Integer>> authors;
	private String pIds;
	private String lIds;
	private String eIds;
	private String kIds;
	private String aIds;
	private int limit = 30;
	
	public TopByYear(Connection conn)
	{
		m_conn = conn;
		people = new HashMap<Integer, ArrayList<Integer>>();
		locations = new HashMap<Integer, ArrayList<Integer>>();
		entities = new HashMap<Integer, ArrayList<Integer>>();
		keywords = new HashMap<Integer, ArrayList<Integer>>();
		authors = new HashMap<Integer, ArrayList<Integer>>();
		pIds = "";
		lIds = "";
		eIds = "";
		kIds = "";
		aIds = "";
	}
	
	public HashMap<Integer, ArrayList<Entity>> getPeopleList()
	{
		HashMap<Integer, ArrayList<Entity>> pplByYears = new HashMap<Integer, ArrayList<Entity>>();
		EntityList entList = new EntityList(m_conn);
		HashMap<Integer, String> entMap = entList.getByIds(pIds.substring(1));
		for (Integer year : people.keySet())
		{
			ArrayList<Entity> entIdName = new ArrayList<Entity>();
			for(Integer eid : people.get(year))
			{
				if (entMap.containsKey(eid))
					{
						if (entMap.get(new Integer(eid)) != null)
						{
							entIdName.add(new Entity(eid,entMap.get(new Integer(eid))));
						}
					}
				if (entIdName.size() > limit)
				{
					break;
				}
			}
			pplByYears.put(year, entIdName);
		}
		return pplByYears;
	}
	public HashMap<Integer, ArrayList<Entity>> getLocationList()
	{
		HashMap<Integer, ArrayList<Entity>> locByYears = new HashMap<Integer, ArrayList<Entity>>();
		EntityList entList = new EntityList(m_conn);
		HashMap<Integer, String> entMap = entList.getByIds(lIds.substring(1));
		for (Integer year : locations.keySet())
		{
			ArrayList<Entity> entIdName = new ArrayList<Entity>();
			for(Integer eid : locations.get(year))
			{
				if (entMap.containsKey(eid))
				{
					if (entMap.get(eid) != null)
					{
						entIdName.add(new Entity(eid,entMap.get(eid)));
					}
				}
				if (entIdName.size() > (limit/2 -1))
				{
					break;
				}
			}
			locByYears.put(year, entIdName);
		}
		return locByYears;
	}
	public HashMap<Integer, ArrayList<Entity>> getEntitiesList()
	{
		HashMap<Integer, ArrayList<Entity>> entByYears = new HashMap<Integer, ArrayList<Entity>>();
		EntityList entList = new EntityList(m_conn);
		HashMap<Integer, String> entMap = entList.getByIds(eIds.substring(1));
		for (Integer year : entities.keySet())
		{
			ArrayList<Entity> entIdName = new ArrayList<Entity>();
			for(Integer eid : entities.get(year))
			{
				if (entMap.containsKey(eid))
				{
					if (entMap.get(eid) != null)
					{
						entIdName.add(new Entity(eid,entMap.get(eid)));
					}
				}
				if (entIdName.size() > (limit/2 - 1))
				{
					break;
				}
			}
			entByYears.put(year, entIdName);
		}
		return entByYears;
	}
	public HashMap<Integer, ArrayList<Keyword>> getKeywordsList()
	{
		HashMap<Integer, ArrayList<Keyword>> kwByYears = new HashMap<Integer, ArrayList<Keyword>>();
		KeywordList kwList = new KeywordList(m_conn);
		kwList.getByIds(kIds.substring(1));
		for (Integer year : keywords.keySet())
		{
			ArrayList<Keyword> kwIdName = new ArrayList<Keyword>();
			for(Integer kid : keywords.get(year))
			{
				for (Keyword k: kwList)
				{
					if (k.getId() == kid)
					{
						if (k.getName() != null)
						{
							kwIdName.add(k);
						}
						break;
					}
				}
				if (kwIdName.size() > limit)
				{
					break;
				}
			}
			kwByYears.put(year, kwIdName);
		}
		return kwByYears;
	}
	public HashMap<Integer, ArrayList<Author>> getAuthorsList()
	{
		HashMap<Integer, ArrayList<Author>> autByYears = new HashMap<Integer, ArrayList<Author>>();
		AuthorList autList = new AuthorList(m_conn);
		autList.getByIds(aIds.substring(1));
		for (Integer year : authors.keySet())
		{
			ArrayList<Author> autIdName = new ArrayList<Author>();
			for(Integer aid : authors.get(year))
			{
				for (Author a: autList)
				{
					if (a.getId() == aid)
					{
						if (a.getName() != null)
						{
							autIdName.add(a);
						}
						break;
					}
				}
				if (autIdName.size() > limit)
				{
					break;
				}
			}
			autByYears.put(year, autIdName);
		}
		return autByYears;
	}
	
	public void loadData() throws SQLException
	{
		String query = "SELECT year, type, id FROM top_by_year ORDER BY year,type";
		long timeStart = System.currentTimeMillis();
		ResultSet rs = m_conn.createStatement().executeQuery(query);
		long timeStop = System.currentTimeMillis();
		m_monitoringDB.queryTime(timeStart, timeStop, query,0);
		while (rs.next())
		{
			int year = rs.getInt("year");
			if (rs.getString("type").equals("ps"))
			{
				ArrayList<Integer> pplIds = people.get(year);
				if (pplIds == null)
				{
					people.put(year, new ArrayList<Integer>());
					pplIds = people.get(year);
				}
				pplIds.add(rs.getInt("id"));
				pIds = pIds + "," + rs.getInt("id");
			}
			else if (rs.getString("type").equals("lc"))
			{
				ArrayList<Integer> entIds = locations.get(year);
				if (entIds == null)
				{
					locations.put(year, new ArrayList<Integer>());
					entIds = locations.get(year);
				}
				entIds.add(rs.getInt("id"));
				lIds = lIds + "," + rs.getInt("id");
			}
			else if (rs.getString("type").equals("en"))
			{
				ArrayList<Integer> entIds = entities.get(year);
				if (entIds == null)
				{
					entities.put(year, new ArrayList<Integer>());
					entIds = entities.get(year);
				}
				entIds.add(rs.getInt("id"));
				eIds = eIds + "," + rs.getInt("id");
			}
			else if (rs.getString("type").equals("kw"))
			{
				ArrayList<Integer> kwIds = keywords.get(year);
				if (kwIds == null)
				{
					keywords.put(year, new ArrayList<Integer>());
					kwIds = keywords.get(year);
				}
				kwIds.add(rs.getInt("id"));
				kIds = kIds + "," + rs.getInt("id");
			}
			else if (rs.getString("type").equals("au"))
			{
				ArrayList<Integer> autIds = authors.get(year);
				if (autIds == null)
				{
					authors.put(year, new ArrayList<Integer>());
					autIds = authors.get(year);
				}
				autIds.add(rs.getInt("id"));
				aIds = aIds + "," + rs.getInt("id");
			}
		}
		if (people.isEmpty() || entities.isEmpty() || keywords.isEmpty() || authors.isEmpty())
		{
			fillData();
		}
	}
	
	private void fillData() throws SQLException
	{
		YearList years = new YearList(m_conn);
		years.getYearsByArticleIds(null, 0);
		if (people.isEmpty())
		{
			for(Integer year : years)
			{
				System.out.println("year: " + year);
				String insertEnt = "";
				people.put(year, new ArrayList<Integer>());
				
				String query = "SELECT distinct COUNT(ec.corpus_id) as cnt, e.id, e.display_name FROM ENTITY e, ENTITY_CORPUS ec " +
				"WHERE e.id = ec.entity_id AND e.type = 'person' AND ec.corpus_id in (select id from corpus where PUBLISH_DATE >= '" + year + "-01-01' AND PUBLISH_DATE <= '" + year + "-12-31') GROUP BY e.id, e.display_name ORDER BY cnt desc limit 50";
				long timeStart = System.currentTimeMillis();
				ResultSet rs = m_conn.createStatement().executeQuery(query);
				long timeStop = System.currentTimeMillis();
				m_monitoringDB.queryTime(timeStart, timeStop, query,0);
				while (rs.next())
				{
					insertEnt = insertEnt + ",(" + year + ",'ps'," + rs.getInt("id") + ")";
					
//					entIds.add(rs.getInt("id"));
					pIds = pIds + "," + rs.getInt("id");
				}
				insertEnt = insertEnt.substring(1);
				String inputQuery = "INSERT INTO top_by_year (year,type,id) VALUES " + insertEnt;
				System.out.println(inputQuery);
				m_conn.createStatement().execute(inputQuery);
			}
		}
		if (locations.isEmpty())
		{
			for(Integer year : years)
			{
				System.out.println("year: " + year);
				String insertEnt = "";
				locations.put(year, new ArrayList<Integer>());
				
				String query = "SELECT distinct COUNT(ec.corpus_id) as cnt, e.id, e.display_name FROM ENTITY e, ENTITY_CORPUS ec " +
				"WHERE e.id = ec.entity_id AND e.type = 'location' AND ec.corpus_id in (select id from corpus where PUBLISH_DATE >= '" + year + "-01-01' AND PUBLISH_DATE <= '" + year + "-12-31') GROUP BY e.id, e.display_name ORDER BY cnt desc limit 50";
				long timeStart = System.currentTimeMillis();
				ResultSet rs = m_conn.createStatement().executeQuery(query);
				long timeStop = System.currentTimeMillis();
				m_monitoringDB.queryTime(timeStart, timeStop, query,0);
				while (rs.next())
				{
					insertEnt = insertEnt + ",(" + year + ",'lc'," + rs.getInt("id") + ")";
					
//					entIds.add(rs.getInt("id"));
					lIds = lIds + "," + rs.getInt("id");
				}
				insertEnt = insertEnt.substring(1);
				String inputQuery = "INSERT INTO top_by_year (year,type,id) VALUES " + insertEnt;
				System.out.println(inputQuery);
				m_conn.createStatement().execute(inputQuery);
			}
		}
		if (entities.isEmpty())
		{
			for(Integer year : years)
			{
				System.out.println("year: " + year);
				String insertEnt = "";
				entities.put(year, new ArrayList<Integer>());
//				ArrayList<Integer> entIds = entities.get(year);
				
				String query = "SELECT distinct COUNT(ec.corpus_id) as cnt, e.id, e.display_name FROM ENTITY e, ENTITY_CORPUS ec " +
				"WHERE e.id = ec.entity_id AND e.type NOT IN ('person','location') AND ec.corpus_id in (select id from corpus where PUBLISH_DATE >= '" + year + "-01-01' AND PUBLISH_DATE <= '" + year + "-12-31') GROUP BY e.id, e.display_name ORDER BY cnt desc limit 50";
				long timeStart = System.currentTimeMillis();
				ResultSet rs = m_conn.createStatement().executeQuery(query);
				long timeStop = System.currentTimeMillis();
				m_monitoringDB.queryTime(timeStart, timeStop, query,0);
				while (rs.next())
				{
					insertEnt = insertEnt + ",(" + year + ",'en'," + rs.getInt("id") + ")";
					
//					entIds.add(rs.getInt("id"));
					eIds = eIds + "," + rs.getInt("id");
				}
				insertEnt = insertEnt.substring(1);
				String inputQuery = "INSERT INTO top_by_year (year,type,id) VALUES " + insertEnt;
				System.out.println(inputQuery);
				m_conn.createStatement().execute(inputQuery);
			}
		}
		if (keywords.isEmpty())
		{
			for(Integer year : years)
			{
				String insertKw = "";
				keywords.put(year, new ArrayList<Integer>());
//				ArrayList<Integer> kwIds = keywords.get(year);
				
				String query = "SELECT distinct COUNT(kc.corpus_id) as cnt, k.id FROM KEYWORD k, KEYWORD_CORPUS kc " +
				"WHERE k.id = kc.keyword_id AND kc.corpus_id in (select id from corpus where PUBLISH_DATE >= '" + year + "-01-01' AND PUBLISH_DATE <= '" + year + "-12-31') GROUP BY k.id ORDER BY cnt desc limit 50";
				long timeStart = System.currentTimeMillis();
				ResultSet rs = m_conn.createStatement().executeQuery(query);
				long timeStop = System.currentTimeMillis();
				m_monitoringDB.queryTime(timeStart, timeStop, query,0);
				while (rs.next())
				{
					insertKw = insertKw + ",(" + year + ",'kw'," + rs.getInt("id") + ")";
					
//					kwIds.add(rs.getInt("id"));
					kIds = kIds + "," + rs.getInt("id");
				}
				insertKw = insertKw.substring(1);
				m_conn.createStatement().execute("INSERT INTO top_by_year (year,type,id) VALUES " + insertKw);
			}
		}
		if (authors.isEmpty())
		{
			for(Integer year : years)
			{
				String insertAut = "";
				authors.put(year, new ArrayList<Integer>());
//				ArrayList<Integer> autIds = authors.get(year);
				
				String query = "SELECT distinct COUNT(ac.corpus_id) as cnt, a.id FROM AUTHOR a, AUTHOR_CORPUS ac " +
				"WHERE a.id = ac.author_id AND ac.corpus_id in (select id from corpus where PUBLISH_DATE >= '" + year + "-01-01' AND PUBLISH_DATE <= '" + year + "-12-31') GROUP BY a.id ORDER BY cnt desc limit 50";
				long timeStart = System.currentTimeMillis();
				ResultSet rs = m_conn.createStatement().executeQuery(query);
				long timeStop = System.currentTimeMillis();
				m_monitoringDB.queryTime(timeStart, timeStop, query,0);
				while (rs.next())
				{
					insertAut = insertAut + ",(" + year + ",'au'," + rs.getInt("id") + ")";
					
//					autIds.add(rs.getInt("id"));
					aIds = aIds + "," + rs.getInt("id");
				}
				insertAut = insertAut.substring(1);
				m_conn.createStatement().execute("INSERT INTO top_by_year (year,type,id) VALUES " + insertAut);
			}
		}
	}
	
	public void loadDataForYear(int year) throws SQLException
	{
		String query = "SELECT type, id FROM top_by_year WHERE year = " + year + " ORDER BY type";
		long timeStart = System.currentTimeMillis();
		ResultSet rs = m_conn.createStatement().executeQuery(query);
		long timeStop = System.currentTimeMillis();
		m_monitoringDB.queryTime(timeStart, timeStop, query,0);
		while (rs.next())
		{
			if (rs.getString("type").equals("ps"))
			{
				ArrayList<Integer> entIds = people.get(year);
				if (entIds == null)
				{
					people.put(year, new ArrayList<Integer>());
					entIds = people.get(year);
				}
				entIds.add(rs.getInt("id"));
				pIds = pIds + "," + rs.getInt("id");
			}
			else if (rs.getString("type").equals("lc"))
			{
				ArrayList<Integer> entIds = locations.get(year);
				if (entIds == null)
				{
					locations.put(year, new ArrayList<Integer>());
					entIds = locations.get(year);
				}
				entIds.add(rs.getInt("id"));
				lIds = lIds + "," + rs.getInt("id");
			}
			else if (rs.getString("type").equals("en"))
			{
				ArrayList<Integer> entIds = entities.get(year);
				if (entIds == null)
				{
					entities.put(year, new ArrayList<Integer>());
					entIds = entities.get(year);
				}
				entIds.add(rs.getInt("id"));
				eIds = eIds + "," + rs.getInt("id");
			}
			else if (rs.getString("type").equals("kw"))
			{
				ArrayList<Integer> kwIds = keywords.get(year);
				if (kwIds == null)
				{
					keywords.put(year, new ArrayList<Integer>());
					kwIds = keywords.get(year);
				}
				kwIds.add(rs.getInt("id"));
				kIds = kIds + "," + rs.getInt("id");
			}
			else if (rs.getString("type").equals("au"))
			{
				ArrayList<Integer> autIds = authors.get(year);
				if (autIds == null)
				{
					authors.put(year, new ArrayList<Integer>());
					autIds = authors.get(year);
				}
				autIds.add(rs.getInt("id"));
				aIds = aIds + "," + rs.getInt("id");
			}
		}
		if (people.isEmpty() || locations.isEmpty() || entities.isEmpty() || keywords.isEmpty() || authors.isEmpty())
		{
			fillData();
		}
	}
}
