package jsppages;

import java.sql.Connection;
import java.util.ArrayList;

import db.Article;
import db.ArticleList;
import db.AuthorList;
import db.CategoriesList;
import db.Entity;
import db.EntityList;
import db.EntityLocs;
import db.EntityOrgs;
import db.EntityPerson;
import db.KeywordList;

public class JspEntity 
{
	private String m_id;
	private Connection m_conn;
	private Entity m_entity;
	private CategoriesList m_catList;
	private EntityList m_entList;
	private KeywordList m_kwList;
	private ArrayList<Integer> m_years;
	private AuthorList m_auList;
	private String m_articleIds;
	private ArrayList<Article> m_articles100;
	
	public JspEntity(String id, Connection conn)
	{
		m_id = id;
		m_conn = conn;
		getData();
	}
	
	private void getData()
	{
		m_entity = new Entity(m_conn);
        m_entity.read(m_id);
        
        ArticleList artEntList = new ArticleList(m_conn);
        m_articleIds = "";
        artEntList.getArticlesByEntity(m_id);
        int count =0;
        m_articles100 = new ArrayList<Article>();
        for (Article a : artEntList)
        {
            m_articleIds = m_articleIds + a.getId() + ",";
            
            if (count < 500)
            {
            	String articleString = "";
            	if (a.getTitle() != null && !a.getTitle().isEmpty())
            	{
            		articleString = a.getTitle();
            	}
            	if (a.getLeadParagraph() != null && !a.getLeadParagraph().isEmpty())
            	{
            		articleString = articleString + " " + a.getLeadParagraph();
            	}
            	if (a.getText() != null && !a.getText().isEmpty())
            	{
            		articleString = articleString + " " + a.getText();
            	}
            	if (articleString != null && !articleString.isEmpty())
            	{
            		m_articles100.add(a);
            		count++;
            	}
            }
            
        }
        m_articleIds = m_articleIds.trim();
        if (!m_articleIds.isEmpty())
        {
            m_articleIds = m_articleIds.substring(0,m_articleIds.length()-1);
        }
        m_catList = new CategoriesList(m_conn);
        m_catList.getCategoriesByArticleIds(m_articleIds,10);
        
        m_entList = new EntityList(m_conn);
        m_entList.getEntitiesByArticleIds(m_articleIds,null,20);
        
        m_kwList = new KeywordList(m_conn);
        m_kwList.getKeywordsByArticleIds(m_articleIds,20);
        
        m_years = new ArrayList<Integer>();
        m_years = artEntList.getAllYears();
        
        m_auList = new AuthorList(m_conn);
        m_auList.getAuthorsByArticleIds(m_articleIds,null);
	}
	public String getDbrLink(boolean isNice)
	{
		if (isNice && m_entity.getDbrLink()!= null)
		{
			return m_entity.getDbrLink().replace("_", " ");
		}
		else
		{
			return m_entity.getDbrLink();
		}
	}
	public String getType()
	{
		return m_entity.getType();
	}
	public String getName()
	{
		return m_entity.getName();
	}
	public String getPicLink()
	{
		String picLink = m_entity.getPicLink();
        if (picLink == null || picLink.isEmpty())
        {
    	   picLink = "notavailable.jpg";
    	}
        return picLink;
	}
	public String getAbstract()
	{
		return m_entity.getAbstract();
	}
	public CategoriesList getCategories()
	{
		return m_catList;
	}
	public EntityList getEntities()
	{
		return  m_entList;
	}
	public KeywordList getKeywords()
	{
		return m_kwList;
	}
	public ArrayList<Integer> getYears()
	{
		return m_years;
	}
	public AuthorList getAuthors()
	{
		return m_auList;
	}
	public String getArticleIds()
	{
		return m_articleIds;
	}
	public ArrayList<Article> getArticles100()
	{		
		return m_articles100;
	}
	public String getAdditionalInfo()
	{
		String addInfo = "";
		if (m_entity.getType().equals("person"))
        {
        	EntityPerson person = new EntityPerson(m_entity.getId());
        	person.read(m_conn);
        	String born = "";
        	if (person.getBirthDate()!= null && !person.getBirthDate().isEmpty())
        	{
        		born = person.getBirthDate();
        	}
        	if (person.getBirthPlace() != null && !person.getBirthPlace().isEmpty())
        	{
        		born = born.isEmpty() ? person.getBirthPlace() : born + ", " + person.getBirthPlace();
        	}
        	if (!born.isEmpty())
        	{
        		addInfo = "<span style=\"cursor:text\">Born:" + born + " </span><br>";
        	}
        	String death = "";
        	if (person.getDeathDate()!= null && !person.getDeathDate().isEmpty())
            {
                death = person.getDeathDate();
            }
            if (person.getDeathPlace() != null && !person.getDeathPlace().isEmpty())
            {
                death = born.isEmpty() ? person.getDeathPlace() : born + ", " + person.getDeathPlace();
            }
            if (!death.isEmpty())
            {
                addInfo = addInfo + "<span style=\"cursor:text\">Died:" + death + "</span><br>";
            }
            if (person.getDescription() != null && !person.getDescription().isEmpty())
            {
            	addInfo = addInfo + "<span style=\"cursor:text\">Short description:" + person.getDescription() + "</span><br>";
            }
        }
        else if (m_entity.getType().equals("organization"))
        {
        	EntityOrgs org = new EntityOrgs(m_entity.getId());
        	org.read(m_conn);
        	if (org.getHomepage() != null && !org.getHomepage().isEmpty())
            {
        		addInfo = "<span style=\"cursor:text\">Homepage:</span><a href=\" " + org.getHomepage() + "\"> " + org.getHomepage() + "</a><br>";
            }
        }
        else if (m_entity.getType().equals("location"))
        {
        	EntityLocs loc = new EntityLocs(m_entity.getId());
        	loc.read(m_conn);
        	if (loc.getLat() != null && !loc.getLat().isEmpty())
            {
                addInfo = "<span style=\"cursor:text\">Latitude:" + loc.getLat() + "</span><br>";
            }
        	if (loc.getLong() != null && !loc.getLong().isEmpty())
            {
                addInfo = addInfo + "<span style=\"cursor:text\">Longitude:" + loc.getLong() + "</span><br>";
            }
        	if (loc.getLat() != null && !loc.getLat().isEmpty() && loc.getLong() != null && !loc.getLong().isEmpty())
        	{
                addInfo = addInfo + "<a href=\"https://maps.google.com/?ie=UTF8&ll=" + loc.getLat() + "," + loc.getLong() + "&spn=" + loc.getLat() + "," + loc.getLong() + "&t=w\">Show on a map</a><br>";
        	}
        }
		return addInfo;
	}
}
