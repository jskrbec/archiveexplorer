package parsers;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamReader;

import db.CategoriesList;
import db.Entity;
import db.Keyword;
import db.MonitoringDB;

public class CorpusParser 
{	
	private String path;
	private String title;
	private List<String> author;
	private Date publishDate;
	private String leadParagraph = null;
	private List<String> text;
	private String source;
	private String typesOfMaterial;
	
	private int corpusId;
	private int authorId;
	
	private ArrayList<Entity> entities;
	private ArrayList<String> keywords;
	private ArrayList<String> categories;
	
	MonitoringDB m_monitoringDB = new MonitoringDB("CorpusParser");
	
	public CorpusParser (String path)
	{
		setPath(path);
		
		author = new ArrayList<String>();
		text = new ArrayList<String>();
		
		entities = new ArrayList<Entity>();
		keywords = new ArrayList<String>();
		categories = new ArrayList<String>();
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title != null ? replaceChars(title).trim() : null;
	}
	public List<String> getAuthor() {
		return author;
	}
	public void setAuthor(List<String> author) {
		this.author = author;
	}
	public void addAuthor(String a) {
		this.author.add(a);
	}
	public Date getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}
	public String getLeadParagraph()
	{
		return leadParagraph;
	}
	public void setLeadParagraph(String leadP)
	{
		leadParagraph = leadP;
	}
	public List<String> getText() {
		return text;
	}
	public void setText(List<String> text) {
		this.text = text;
	}	
	public void addText(String paragraph)
	{
		text.add(paragraph);
	}
	public String getSource()
	{
		return source;
	}
	public void setSource(String source)
	{
		this.source = source != null ? replaceChars(source).trim() : null;
	}
	public void setTypesOfMaterial(String typesOfMaterial) {
		this.typesOfMaterial = typesOfMaterial;
	}
	public String getTypesOfMaterial() {
		return typesOfMaterial;
	}
	public int getCorpusId()
	{
		return corpusId;
	}
	public int getAuthorId()
	{
		return authorId;
	}
	public List<Entity> getEntities() {
		return entities;
	}
	public void addEntity(Entity e) {
		this.entities.add(e);
	}
	public List<String> getKeywords() {
		return keywords;
	}
	public void setKeywords(ArrayList<String> keywords) {
		this.keywords = keywords;
	}
	public void addKeyword(String k) {
		this.keywords.add(k);
	}
	public List<String> getCategories() {
		return categories;
	}
	public void setCategories(ArrayList<String> categories) {
		this.categories = categories;
	}
	public void addCategory(String c) {
		this.categories.add(c);
	}
	
	public void parse(XMLStreamReader xReader) throws Exception
	{	
		throw new Exception("Unknown format!");
	}
	
	public void save(Connection conn) throws SQLException
	{			
			String text = "";
			for (String s : getText())
			{
				s = replaceChars(s);
				text = text + " <p>" + s + "</p>";
			}
			String leadParagraph = getLeadParagraph()!= null ? replaceChars(getLeadParagraph()).trim() : null; 
			
			int sourceId = 0;
			if (source != null)
			{
				String selectS = "SELECT id FROM SOURCE WHERE name = ?";
				PreparedStatement checkSource = conn.prepareStatement(selectS);
				checkSource.setString(1, getSource());
				ResultSet sourceIds = checkSource.executeQuery();
				if (sourceIds.next())
				{
					sourceId = sourceIds.getInt(1);
				}

				if (sourceId == 0)
				{
					String sourceSql = "INSERT INTO SOURCE (name, url,location) VALUES (?,null,null) RETURNING id";
					PreparedStatement insertSource = conn.prepareStatement(sourceSql);
					insertSource.setString(1, getSource());
					insertSource.execute();
					sourceIds = insertSource.getResultSet();
					while(sourceIds.next())
					{
						sourceId = sourceIds.getInt(1);
					}
					insertSource.close();
				}
			}
				
			int idTypesOfMaterial = 0;
			if (typesOfMaterial != null && !typesOfMaterial.isEmpty())
			{
				String query = "SELECT id FROM TYPES_OF_MATERIAL WHERE name = ?";
				PreparedStatement checkType = conn.prepareStatement(query);
				checkType.setString(1, typesOfMaterial);
				ResultSet typesIds = checkType.executeQuery();
				if (typesIds.next())
				{
					idTypesOfMaterial = typesIds.getInt(1);
				}
				if (idTypesOfMaterial == 0)
				{
					String insertQuery = "INSERT INTO TYPES_OF_MATERIAL (name) VALUES (?) RETURNING id";
					PreparedStatement insertTypes = conn.prepareStatement(insertQuery);
					insertTypes.setString(1, typesOfMaterial);
					insertTypes.execute();
					typesIds = insertTypes.getResultSet();
					while(typesIds.next())
					{
						idTypesOfMaterial = typesIds.getInt(1);
					}
					insertTypes.close();
				}
			}
			
			String sql = "INSERT INTO CORPUS (title, publish_date, file_path, lead_paragraph, text,source_id,types_of_material_id) VALUES (?,?,?,?,?,?,?) RETURNING id";
			PreparedStatement insertCorpus = conn.prepareStatement(sql);
			if(title == null)
			{ 
				insertCorpus.setNull(1, Types.NULL);
			}
			else
			{
				insertCorpus.setString(1, getTitle());
			}
			insertCorpus.setDate(2, getPublishDate());
			insertCorpus.setString(3, getPath());
			insertCorpus.setString(4, leadParagraph);
			insertCorpus.setString(5, text);
			insertCorpus.setInt(6, source == null ? null : sourceId);
			if (idTypesOfMaterial == 0)
			{
				insertCorpus.setNull(7, Types.NULL);
			}
			else
			{
				insertCorpus.setInt(7, idTypesOfMaterial);
			}
			insertCorpus.execute();		
			ResultSet corpusIds = insertCorpus.getResultSet();
			corpusIds.next();
			corpusId = corpusIds.getInt(1);
			insertCorpus.close();
			corpusIds.close();
			
			String sqlVector = "UPDATE corpus SET vector_text_all = setweight(to_tsvector(coalesce(title,'')),'A') || setweight(to_tsvector(coalesce(lead_paragraph,'')),'B') || setweight(to_tsvector(coalesce(text,'')),'C') WHERE id = ?";
			PreparedStatement updateVector = conn.prepareStatement(sqlVector);
			updateVector.setInt(1, corpusId);
			updateVector.execute();		
			
			for(String author : getAuthor())
			{
				author = replaceChars(author);
				authorId = 0;
				String selectA = "SELECT id FROM AUTHOR WHERE name = ?";
				PreparedStatement checkAuthor = conn.prepareStatement(selectA);
				checkAuthor.setString(1, author);
				ResultSet ids = checkAuthor.executeQuery();
				if (ids.next())
				{
					authorId = ids.getInt(1);
				}
				ids.close();
				if (authorId == 0)
				{
					String authorSql = "INSERT INTO AUTHOR (name) VALUES ( ?) RETURNING id";
					PreparedStatement insertAuthor = conn.prepareStatement(authorSql);
					insertAuthor.setString(1, author);
					insertAuthor.execute();
					ResultSet authorIds = insertAuthor.getResultSet();
					authorIds.next();
					authorId = authorIds.getInt(1);
					insertAuthor.close();
				}
				conn.createStatement().execute("INSERT INTO AUTHOR_CORPUS (author_id,corpus_id) VALUES ( '" + 
						authorId + "', '" + corpusId + "')");
				
			}
			
			saveAdditionalData(conn);
	}
	
	private void saveAdditionalData(Connection conn) throws SQLException
	{
		if (entities != null && !entities.isEmpty())
		{
			ArrayList<Integer> enIds = new ArrayList<Integer>();
			ArrayList<String>	enNamesDb = new ArrayList<String>();
			String entityNames = "";
			for (Entity entity : entities)
			{
				entity.setName(replaceChars(entity.getName()));
				entityNames = entityNames + ",'" + entity.getName().toLowerCase() + "'";
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
			for (Entity e : entities)
			{
				if (!enNamesDb.contains(e.getName().toLowerCase()))
				{
					e.save(conn);
					enIds.add(e.getId());
				}
			}
			String insertIds = "";
			for (Integer enId : enIds)
			{
				insertIds = insertIds + ",('" + enId + "','" + corpusId + "')";
			}
			if (!insertIds.isEmpty())
			{
				insertIds = insertIds.substring(1);
				conn.createStatement().execute("INSERT INTO ENTITY_CORPUS (entity_id,corpus_id) VALUES " + insertIds);
			}
		}
		if (keywords != null && !keywords.isEmpty())
		{
			ArrayList<Integer> kwIds = new ArrayList<Integer>();
			ArrayList<String>	kwNamesDb = new ArrayList<String>();
			String kwNames = "";
			for (String kw : keywords)
			{
				kwNames = kwNames + ",'" + replaceChars(kw).toLowerCase() + "'";
			}
			if (!kwNames.isEmpty())
			{
				kwNames = kwNames.substring(1);
				String query = "SELECT id,name FROM KEYWORD WHERE lower(name) IN (" + kwNames + ")";
				long timeStart = System.currentTimeMillis();
				ResultSet rs = conn.createStatement().executeQuery(query);
				long timeStop = System.currentTimeMillis();
				m_monitoringDB.queryTime(timeStart, timeStop, query,10);
				while (rs.next())
				{
					kwIds.add(rs.getInt("id"));
					kwNamesDb.add(rs.getString("name").toLowerCase());
				}
			}
			for (String kw : keywords)
			{
				kw = replaceChars(kw);
				if (!kwNamesDb.contains(kw.toLowerCase()))
				{
					Keyword keyword = new Keyword(conn);
					keyword.setName(kw);
					keyword.save();
					kwIds.add(keyword.getId());
				}
			}
			String insertIds = "";
			for (Integer kwId : kwIds)
			{
				insertIds = insertIds + ",('" + kwId + "','" + corpusId + "')";
			}
			if (!insertIds.isEmpty())
			{
				insertIds = insertIds.substring(1);
				conn.createStatement().execute("INSERT INTO KEYWORD_CORPUS (keyword_id,corpus_id) VALUES " + insertIds);
			}
		}
		if (categories != null && !categories.isEmpty())
		{
			CategoriesList catList = new CategoriesList(conn);
			catList.save(categories,corpusId);
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
