package parsers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class NytParser extends CorpusParser
{
	public NytParser(String path) 
	{
		super(path);
	}

	public void parse(XMLStreamReader xReader) throws XMLStreamException, ParseException
	{
		setSource("The New York Times Archive");
		while (xReader.hasNext())
		{
			if (xReader.isStartElement())
			{
				if (xReader.getLocalName().equals("title"))
				{
					setTitle(xReader.getElementText().trim());
				}
				if (xReader.getLocalName().equals("p"))
				{
					if (xReader.getAttributeCount() == 0)
					{
						String text = xReader.getElementText().trim();
						if (text.trim().startsWith("LEAD:"))
						{
							setLeadParagraph(text.substring(5).trim());
						}
						else if (getLeadParagraph() == null || 
								(getLeadParagraph() != null && !text.equals(getLeadParagraph())))
						{
							addText(text);
						}
					}
					else if (xReader.getAttributeValue(null, "class").equals("timestamp"))
					{
						DateFormat df = new SimpleDateFormat("MMM dd, yyyy");
						Date pDate = df.parse(xReader.getElementText());
						setPublishDate(new java.sql.Date(pDate.getTime()));
					}
					else if (xReader.getAttributeValue(null, "class").equals("byline"))
					{
						String author = xReader.getElementText().trim();
						if (author != null && !author.isEmpty() && author.length() > 1)
						{
							if (author != null && !author.isEmpty() && author.substring(0, 2).equalsIgnoreCase("By"))
							{
								author = author.substring(2, author.length());
							}
							if (author.contains(", SPECIAL TO"))
							{
								author = author.substring(0, author.indexOf(','));
							}
							if (author.contains(";"))
							{
								author = author.substring(0, author.indexOf(';'));
							}
							if (author.contains(" and ") || author.contains(" AND "))
							{
								int and = author.indexOf(" and ") < 0 ? author.indexOf(" AND ") : author.indexOf(" and ");
								addAuthor(author.substring(0,and).trim());
								author = author.substring(and + 4);
							}
							if (author.trim().equals("SPECIAL TO THE NEW YORK TIMES"))
							{
								author = null;
							}
						}
						if (author != null && !author.isEmpty())
						{
							addAuthor(author.trim());
						}
					}
				}
			}
			if (xReader.isEndElement() && xReader.getLocalName().equals("article"))
			{
				break;
			}
			xReader.next();
		}
	}
}
