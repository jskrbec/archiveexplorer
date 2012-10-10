package parsers;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.ParseException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class ReutersParser extends CorpusParser
{
	public ReutersParser(String path) 
	{
		super(path);
	}

	public void parse(XMLStreamReader xReader) throws XMLStreamException, ParseException
	{
		boolean inMetadata = false;
		boolean inText = false;
		
		setSource("Reuters");
		
		while (xReader.hasNext())
		{
			if (xReader.isEndElement() && xReader.getLocalName().equals("newsitem"))
			{
				break;
			}
			if (xReader.isStartElement() && xReader.getLocalName().equals("title"))
			{
				setTitle(xReader.getElementText());
			}
			else if (xReader.isStartElement() && xReader.getLocalName().equals("byline"))
			{
				addAuthor(xReader.getElementText().trim());
			}
			else if (xReader.isStartElement() && xReader.getLocalName().equals("text"))
			{
				inText = true;
			}
			else if (xReader.isEndElement() && xReader.getLocalName().equals("text"))
			{
				inText = false;
			}
			else if (xReader.isStartElement() && xReader.getLocalName().equals("metadata"))
			{
				inMetadata = true;
			}
			else if (xReader.isEndElement() && xReader.getLocalName().equals("metadata"))
			{
				inMetadata = false;
			}
			if (inText)
			{
				if (xReader.isStartElement() && xReader.getLocalName().equals("p"))
				{
					try {
						String text = xReader.getElementText();
						byte[] latin = text.getBytes();
						byte[] utf8 =new String(latin,"ISO-8859-1").getBytes("UTF-8"); 
						addText(new String(utf8, "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
			else if (inMetadata)
			{
				if (xReader.isStartElement() && xReader.getLocalName().equals("dc") &&
						xReader.getAttributeValue(null, "element").equals("dc.date.published"))
				{
					setPublishDate(Date.valueOf(xReader.getAttributeValue(null, "value")));
				}
			}
			xReader.next();
		}
	}
}
