package jsppages;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import db.Article;
import db.ArticleList;
import db.Author;
import db.AuthorList;
import db.CategoriesList;
import db.Category;
import db.EntityList;

public class JspCategory 
{
	private String m_id;
	private Connection m_conn;
	private Category m_cat;
	private ArticleList m_articles;
	private HashMap<Integer,String> m_authors;
	private String m_articleIdsString;
	private EntityList m_people;
	private EntityList m_locs;
	private EntityList m_orgs;
	private ArrayList<Integer> m_years;
	
	public JspCategory(String id, Connection conn)
	{
		m_id = id;
		m_conn = conn;
		
		try {
			getData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void getData() throws SQLException
	{
		m_cat = new Category(Integer.parseInt(m_id.trim()),m_conn);
        m_cat.getCategory();
        m_cat.findParentsForCategory(m_conn);
        m_cat.findChildren();
        m_articles = new ArticleList(m_conn);
        m_articles.getArticlesByCategory(m_id);
        m_authors = new HashMap<Integer,String>();
        m_articleIdsString = "";
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
        m_people = new EntityList(m_conn);
        m_people.getEntitiesByArticleIds(m_articleIdsString,"person", 15);
        m_locs = new EntityList(m_conn);
        m_locs.getEntitiesByArticleIds(m_articleIdsString,"location", 15);
        m_orgs = new EntityList(m_conn);
        m_orgs.getEntitiesByArticleIds(m_articleIdsString,"organization",15);
        
        m_years = new ArrayList<Integer>();
        m_years = m_articles.getAllYears();
        
        m_cat.findRelatedCategories();
	}
	
	public String getName()
	{
		return m_cat.getName();
	}
	public String getParentsLine()
	{
		String line = "";
		for (Category c : m_cat.getParents())
        {
            line = line + "<span onClick=\"document.location='category.jsp?&id=" + c.getId() + "'\" style=\"cursor: pointer\">" + c.getName() + "</span>/";
        }
		return line;
	}
	public CategoriesList getChildren()
	{
		return m_cat.getChildren();
	}
	public CategoriesList getRelCat()
	{
		return m_cat.getRelatedCategories();
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
	public String getArticleIdsString()
	{
		return m_articleIdsString;
	}
}
