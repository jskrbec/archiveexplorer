package jsppages;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import db.Article;
import db.ArticleList;
import db.Author;
import db.AuthorList;
import db.CategoriesList;
import db.EntityList;

public class JspKeyword 
{
	private Connection m_conn;
	private String m_id; 
	private ArticleList m_articles;
	private CategoriesList m_catList;
	private EntityList m_people;
	private EntityList m_locs;
	private EntityList m_orgs;
	private String m_articleIdsString;
	private ArrayList<Integer> m_years;
	private HashMap<Integer,String> m_authors;
	
	public JspKeyword(String id, Connection conn)
	{
		m_id = id;
		m_conn = conn;
		getData();
	}
	
	private void getData()
	{
		m_articles = new ArticleList(m_conn);
        m_articleIdsString = "";
        m_articles.getArticlesByKeyword(m_id);
        m_authors = new HashMap<Integer,String>();
        for (Article aid : m_articles)
        {
           m_articleIdsString = m_articleIdsString + aid.getId() + ",";
           AuthorList temp = aid.getAuthors(m_conn);
           if (temp != null && !temp.isEmpty())
           {
        	   for (Author a : temp)
        	   {
        		   String name = (a.getName().length() > 30) ? a.getName().substring(0, 30) : a.getName();
        		   m_authors.put(a.getId(),name);
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
        	m_catList.getCategoriesByArticleIds(m_articleIdsString,7);
        }
        
        m_people = new EntityList(m_conn);
        m_people.getEntitiesByArticleIds(m_articleIdsString,"person", 15);
        m_locs = new EntityList(m_conn);
        m_locs.getEntitiesByArticleIds(m_articleIdsString,"location", 15);
        m_orgs = new EntityList(m_conn);
        m_orgs.getEntitiesByArticleIds(m_articleIdsString,"organization",15);
        
        m_years = new ArrayList<Integer>();
        m_years = m_articles.getAllYears();
	}
	
	public String getArticleIdsString()
	{
		return m_articleIdsString;
	}
	public CategoriesList getCategories()
	{
		return m_catList;
	}
	public ArticleList getArticles()
	{
		return m_articles;
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
	public ArrayList<Author> getAuthors()
	{
		ArrayList<Author> temp = new ArrayList<Author>();
		for (Integer i : m_authors.keySet())
		{
			temp.add(new Author(i.intValue(), m_authors.get(i)));
		}
		return temp;
	}
}
