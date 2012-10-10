package jsppages;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import db.Article;
import db.ArticleList;
import db.Author;
import db.CategoriesList;
import db.EntityList;
import db.KeywordList;

public class JspAuthor 
{
	private String m_id;
	private Connection m_conn;
	private String m_name;
	private ArticleList m_articles;
	private String m_articleIdsString;
	private CategoriesList m_catList;
	private EntityList m_people;
	private EntityList m_locs;
	private EntityList m_orgs;
	private ArrayList<Integer> m_years;
	private ArrayList<Article> m_articles100;
	private KeywordList m_kwList;
	
	public JspAuthor(String id, Connection conn) throws SQLException
	{
		m_id = id;
		m_conn = conn;
		getData();
	}
	private void getData() throws SQLException
	{
		Author author = new Author(m_conn);
		author.setId(new Integer(m_id).intValue());
		author.getAuthor();
		m_name = author.getName();
		
		m_articles = new ArticleList(m_conn);
		m_articles.getArticlesByAuthor(m_id);
		m_articleIdsString = "";
		int count =0;
		m_articles100 = new ArrayList<Article>();
		for (Article a : m_articles)
        {
           m_articleIdsString = m_articleIdsString + a.getId() + ",";
           
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
        
        m_articleIdsString = m_articleIdsString.trim();
        if (!m_articleIdsString.isEmpty())
        {
        	m_articleIdsString = m_articleIdsString.substring(0,m_articleIdsString.length()-1);
        }
        
        m_catList = new CategoriesList(m_conn);
        if (m_articleIdsString != null && !m_articleIdsString.isEmpty())
        {
        	System.out.println(m_articleIdsString);
        	m_catList.getCategoriesByArticleIds(m_articleIdsString,10);
        }
        
        m_people = new EntityList(m_conn);
        m_people.getEntitiesByArticleIds(m_articleIdsString,"person", 15);
        m_locs = new EntityList(m_conn);
        m_locs.getEntitiesByArticleIds(m_articleIdsString,"location", 15);
        m_orgs = new EntityList(m_conn);
        m_orgs.getEntitiesByArticleIds(m_articleIdsString,"organization",15);
        
        m_years = new ArrayList<Integer>();
        m_years = m_articles.getAllYears();
        
        m_kwList = new KeywordList(m_conn);
        m_kwList.getKeywordsByArticleIds(m_articleIdsString,10);

	}
	public String getName()
	{
		return m_name;
	}
	public CategoriesList getCategories()
	{
		return m_catList;
	}
	public EntityList getPeople()
	{
		return m_people;
	}
	public EntityList getLocations()
	{
		return m_locs;
	}
	public EntityList getOrganizations()
	{
		return m_orgs;
	}
	public ArrayList<Integer> getYears()
	{
		return m_years;
	}
	public String getArticleIdsString()
	{
		return m_articleIdsString;
	}
	public ArrayList<Article> getArticles100()
	{
		return m_articles100;
	}
	public KeywordList getKeywords()
	{
		return m_kwList;
	}
}
