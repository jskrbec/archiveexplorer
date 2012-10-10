package jsppages;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import db.Article;
import db.ArticleList;

public class JspDocAtlas 
{
	private String m_ids;
	private Connection m_conn;
	private String m_articleIds;
	private ArrayList<Article> m_articles100;
	
	public JspDocAtlas(String ids, Connection conn) throws SQLException
	{
		m_ids = ids;
		m_conn = conn;
		getData();
	}
	
	private void getData() throws SQLException
	{        
        ArticleList artList = new ArticleList(m_conn);
        m_articleIds = "";
        artList.getByIds(m_ids);
        int count =0;
        m_articles100 = new ArrayList<Article>();
        for (Article a : artList)
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
	}
	
	public String getArticleIds()
	{
		return m_articleIds;
	}
	public ArrayList<Article> getArticles100()
	{		
		return m_articles100;
	}
}
