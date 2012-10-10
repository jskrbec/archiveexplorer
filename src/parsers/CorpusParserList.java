package parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

public class CorpusParserList extends ArrayList<CorpusParser>
{
	private static final long serialVersionUID = 1L;

	private static final int BUFFER_SIZE = 8 * 1024 * 1024;
	
	private String m_path; 
	
	public CorpusParserList (String path)
	{
		m_path = path;
	}
	
	public void parseList() throws FactoryConfigurationError, Exception
	{
		try{
		File file = new File(m_path);
		InputStreamReader insr = new InputStreamReader(new FileInputStream(file),"UTF-8");
		
		BufferedReader in = new BufferedReader(insr, BUFFER_SIZE);
		XMLStreamReader xReader = XMLInputFactory.newInstance().createXMLStreamReader(in);
		CorpusParser corpusParser = null;
		boolean isNyt = false;
		
		while (xReader.hasNext()) 
		{
			if (xReader.getEventType() == XMLStreamConstants.START_ELEMENT)
			{
				if ("nitf".equals(xReader.getLocalName()))
				{
					corpusParser = new NitfParser(m_path);
				}
				else if (!isNyt && "article".equals(xReader.getLocalName()) && 
						!xReader.getAttributeValue(null, "article-type").isEmpty())
				{
					corpusParser = new NlmParser(m_path);
				}
				else if ("newsitem".equals(xReader.getLocalName()))
				{
					corpusParser = new ReutersParser(m_path);
				}
				else if ("articles".equals(xReader.getLocalName()))
				{
					isNyt = true;
					xReader.next();
					continue;
				}
				else if (isNyt && "article".equals(xReader.getLocalName()))
				{
					corpusParser = new NytParser(m_path);
				}
				corpusParser.parse(xReader);
				add(corpusParser);
			}
			else if (xReader.isEndElement() && xReader.getLocalName().equals("articles"))
			{
				isNyt = false;
			}
			if (xReader.hasNext())
			{
				xReader.next();
			}
		}
		xReader.close();
		}
		catch (Exception e) 
		{
			String savedIds = "";
			for(CorpusParser cp : this)
			{
				savedIds = savedIds + "," + cp.getCorpusId();
			}
			throw new Exception("Saved article ids: " + savedIds.substring(1) + ". Error: " + e.getMessage());
		}
	}
}
