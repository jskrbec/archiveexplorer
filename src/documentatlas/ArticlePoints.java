package documentatlas;

import db.Article;

public class ArticlePoints 
{
	private Article m_article;
	private double m_x;
	private double m_y;
	
	public ArticlePoints(Article a, double x, double y)
	{
		m_article = a;
		m_x = x;
		m_y = y;
	}
	public double getX()
	{
		return m_x;
	}
	public double getY()
	{
		return m_y;
	}
	public Article getArticle()
	{
		return m_article;
	}
}
