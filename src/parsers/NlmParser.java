package parsers;

import java.sql.Date;
import java.text.ParseException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
//import javax.xml.stream.events.XMLEvent;

public class NlmParser extends CorpusParser
{
	public NlmParser (String path)
	{
		super(path);
	}
	
	public void parse(XMLStreamReader xReader) throws XMLStreamException, ParseException
	{
		boolean inArticle = false;
		boolean inFront = false;
		boolean inBody = false;
		
		setSource("Nature");
		
		while (xReader.hasNext())
		{			
			if (xReader.isStartElement() && xReader.getLocalName().equals("article"))
			{
				inArticle = true;
				xReader.next();
			}
			if (inArticle && xReader.isStartElement())
			{
				if (xReader.getLocalName().equals("front"))
				{
					inFront = true;
					xReader.next();
				}
				else if (xReader.getLocalName().equals("body"))
				{
					inBody = true;
					xReader.next();
				}
			}
			else if ( inArticle && xReader.isEndElement())
			{
				if (xReader.getLocalName().equals("front"))
				{
					inFront = false;		
				}
				else if (xReader.getLocalName().equals("body"))
				{
					inBody = false;
				}
				else if (xReader.getLocalName().equals("article"))
				{
					inArticle = false;
					break;
				}
			}

			if (inFront && xReader.isStartElement())
			{
				if (xReader.getLocalName().equals("article-title"))
				{
					setTitle(xReader.getElementText());
				}
				else if (xReader.getLocalName().equals("contrib") &&
							xReader.getAttributeValue(null, "contrib-type").equals("author"))
				{
					xReader.next();
					String author = null;
					while (!(xReader.isEndElement() && xReader.getLocalName().equals("contrib")))
					{
						if (xReader.isStartElement() && xReader.getLocalName().equals("surname"))
						{
							author = ((author != null ? author + " " : "") + xReader.getElementText());
						}
						if (xReader.isStartElement() && xReader.getLocalName().equals("given-names"))
						{
							author = (xReader.getElementText() + (author != null ? " " + author : ""));
						}
						xReader.next();
					}
					author = author != null ? author.trim() : null;
					if (author != null && !author.isEmpty())
					{
						addAuthor(author);
					}
				}
				else if (xReader.getLocalName().equals("pub-date"))
				{
					xReader.next();
					String day = null;
					String month = null;
					String year = null;
					while (!(xReader.isEndElement() && xReader.getLocalName().equals("pub-date")))
					{
						if (xReader.isStartElement() && xReader.getLocalName().equals("day"))
						{
							day = xReader.getElementText().trim();
							if (day.length() > 2) day = null;
							if (day.length() == 1) day = "0" + day;
						}
						if (xReader.isStartElement() && xReader.getLocalName().equals("month"))
						{
							month = xReader.getElementText();
							if (month.length() > 2) month = null;
							if (month.length() == 1) month = "0" + month;
						}
						if (xReader.isStartElement() && xReader.getLocalName().equals("year"))
						{
							year = xReader.getElementText();
						}
						xReader.next();
					}
					if (day != null && month != null && year != null) 
					{
							setPublishDate(Date.valueOf(year + "-" + month + "-" + day));
					}
				}
			}	
			else if (inBody)
			{
				if (xReader.isStartElement() && xReader.getLocalName().equals("p"))
				{
					String text = ""; 
					
					while (!(xReader.isEndElement() && xReader.getLocalName().equals("p")))
					{
						xReader.next();
						if (xReader.isStartElement())
						{
							text = text.trim() + " " + xReader.getElementText();
						}
						else if (!xReader.isEndElement())
						{
							text = text.trim() + " " + xReader.getText().trim();
						}
						
					}
					text = text != null ? text.trim(): null;
					if (text != null && !text.isEmpty())
					{
						addText(text);
					}
				}
				else if (xReader.isStartElement() && xReader.getLocalName().equals("sec"))
				{
					String citationText = "";
					String citationHeader = "";
					while (!(xReader.isEndElement() && xReader.getLocalName().equals("sec")))
					{
						xReader.next();
						if (xReader.isStartElement() && xReader.getLocalName().equals("title"))
						{
							String title = xReader.getElementText(); 
							citationHeader = title + " " + citationHeader;
						}
						else if (xReader.isStartElement() && xReader.getLocalName().equals("p"))
						{
							citationText = citationText + "<p>";
							while(!(xReader.isEndElement() && xReader.getLocalName().equals("p")))
							{
								xReader.next();
								if (xReader.isStartElement() && xReader.getLocalName().equals("citation"))
								{
									String citation = "(";
									while(!(xReader.isEndElement() && xReader.getLocalName().equals("citation")))
									{
										xReader.next();
										if (xReader.isStartElement() && xReader.getLocalName().equals("source"))
										{
											citation = citation + xReader.getElementText();
										}
										else if (xReader.isStartElement() && xReader.getLocalName().equals("year"))
										{
											citation = citation + ", " + xReader.getElementText();
										}
									}
									citationHeader = citationHeader + citation + ")";
								}
								else if (!xReader.isStartElement() && !xReader.isEndElement())
								{
									citationText = citationText + " " + xReader.getText();
								}
								else if (xReader.isStartElement() && xReader.getLocalName().equals("italic"))
								{
									try
									{
										citationText = citationText + "<i>" + xReader.getElementText() + "</i>";
									}
									catch (XMLStreamException e) {
										//TODO get more info on elts inside italic elt
									}
								}
							}
							citationText = citationText + "</p>";
						}
					}
					addText("<b>" + citationHeader + "</b>" + citationText); 
					if (xReader.isStartElement())
					{
						continue;
					}
				}
				else if (!xReader.isStartElement())
				{
					String text = "";
					while (!xReader.isStartElement() && !xReader.isEndElement())
					{
						text = text + " " + xReader.getText().trim();
						text = text.trim();
						xReader.next();
					}
					text = text.trim();
					if (text != null && !text.isEmpty())
					{
						addText(text);
					}
					if (xReader.isStartElement())
					{
						if (xReader.getLocalName().equals("sec") || xReader.getLocalName().equals("p"))
						{
							continue;
						}
					}
				}
			}
			xReader.next();
		}
	} 
}
