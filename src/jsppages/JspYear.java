package jsppages;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import db.Article;
import db.ArticleList;
import db.Author;
import db.CategoriesList;
import db.Entity;
import db.Keyword;
import db.TopByYear;

public class JspYear 
{
	private Connection m_conn;
	private String m_year;
	private CategoriesList m_catList;
	private ArrayList<Article> m_artList;
	private String m_articleIds;
	private ArrayList<Entity> m_personList;
	private ArrayList<Entity> m_locList;
	private ArrayList<Entity> m_orgList;
	private ArrayList<Keyword> m_keywordList;
	private ArrayList<Author> m_authorList;
	
	public JspYear(String year, Connection conn)
	{
		m_year = year.trim();
		m_conn = conn;
		
		getData();
	}
	
	private void getData()
	{
		int yearMin = Integer.parseInt(m_year);
		ArticleList artEntList = new ArticleList(m_conn);
        m_articleIds = "";
        HashMap<Integer, Article> articleMap = artEntList.getArticlesByYear(yearMin,0);
        m_artList = new ArrayList<Article>();
        m_artList.addAll(articleMap.values());
        
        for (Integer aid : articleMap.keySet())
        {
        	m_articleIds = m_articleIds + "," + aid;
        }
        m_articleIds = m_articleIds.trim();
        if (!m_articleIds.isEmpty())
        {
        	m_articleIds = m_articleIds.substring(1);
        }
        
        m_catList = new CategoriesList(m_conn);
        m_catList.getCategoriesByArticleIds(m_articleIds,7);
        
        TopByYear top = new TopByYear(m_conn);
        m_personList = new ArrayList<Entity>();
        m_locList = new ArrayList<Entity>();
        m_orgList = new ArrayList<Entity>();
        m_keywordList = new ArrayList<Keyword>();
        m_authorList = new ArrayList<Author>();
        try {
			top.loadDataForYear(yearMin);
			m_personList = top.getPeopleList().get(yearMin);
			m_locList = top.getLocationList().get(yearMin);
			m_orgList = top.getEntitiesList().get(yearMin);
			m_keywordList =  top.getKeywordsList().get(yearMin);
			m_authorList =  top.getAuthorsList().get(yearMin);
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
	}
	public CategoriesList getCategories()
	{
		return m_catList;
	}
	public ArrayList<Article> getArticles()
	{
		return m_artList;
	}
	public ArrayList<Entity> getPeople()
	{
		return m_personList;
	}
	public ArrayList<Entity> getLocations()
	{
		return m_locList;
	}
	public ArrayList<Entity> getOrganizations()
	{
		return m_orgList;
	}
	public String getArticleIds()
	{
		return m_articleIds;
	}
	public ArrayList<Keyword> getKeywords()
	{
		return m_keywordList;
	}
	public ArrayList<Author> getAuthors()
	{
		return m_authorList;
	}
}
