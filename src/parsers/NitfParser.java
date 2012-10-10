package parsers;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.iptc.std.nitf.x20060301.BlockDocument.Block;
import org.iptc.std.nitf.x20060301.BodyContentDocument.BodyContent;
import org.iptc.std.nitf.x20060301.BodyDocument.Body;
import org.iptc.std.nitf.x20060301.BylineDocument.Byline;
import org.iptc.std.nitf.x20060301.ClassifierDocument.Classifier;
import org.iptc.std.nitf.x20060301.HeadDocument.Head;
import org.iptc.std.nitf.x20060301.IdentifiedContentDocument.IdentifiedContent;
import org.iptc.std.nitf.x20060301.LocationDocument.Location;
import org.iptc.std.nitf.x20060301.MetaDocument.Meta;
import org.iptc.std.nitf.x20060301.NitfDocument;
import org.iptc.std.nitf.x20060301.OrgDocument.Org;
import org.iptc.std.nitf.x20060301.PDocument.P;
import org.iptc.std.nitf.x20060301.PersonDocument.Person;

import db.Entity;


public class NitfParser extends CorpusParser
{
	//nitfxml -> objekt
	protected Stack<File> files;
	
	public NitfParser(String path)
	{
		super(path);
	}
	
	public void parse(XMLStreamReader xReader) throws FileNotFoundException, XMLStreamException, FactoryConfigurationError, XmlException, ParseException
	{
		XmlOptions options = new XmlOptions();

		Map<String, String> subst = new HashMap<String, String>();
		subst.put("", "http://iptc.org/std/nitf/2006-03-01/");
		Map<String, String> nsPrefixes = new HashMap<String, String>();
		nsPrefixes.put("http://iptc.org/std/nitf/2006-03-01/", "");

		options.setLoadSubstituteNamespaces(subst);
		options.setUseDefaultNamespace();
		options.setSaveSuggestedPrefixes(nsPrefixes);
		options.setLoadUseDefaultResolver();
		options.setSaveNoXmlDecl();
		options.setUnsynchronized();


		NitfDocument nitf = null;
		
		setSource("The New York Times");

		// 1. Get a XMLBeans representation of the article:
		nitf = NitfDocument.Factory.parse(xReader,options);
		
		Head head = nitf.getNitf().getHead();
		Body body = nitf.getNitf().getBody();

		String title = (head.getTitle() == null) ? null : head.getTitle().toString();
		title = (title == null || title.isEmpty()) ? null : title.substring(title.toString().indexOf('>')+1, title.toString().lastIndexOf('<')).trim();
		setTitle(title);

		String print_byline = null;
		for (Byline byLine : body.getBodyHead().getBylineArray())
		{
			if (byLine.getClass1().equals("normalized_byline"))
			{
				
				String authorString = byLine.toString();
				authorString = authorString.substring(authorString.indexOf('>')+1, authorString.lastIndexOf('<')).trim();
				for (String author : authorString.split(";"))
				{
					setAuthor(new ArrayList<String>());
					String[] a = author.split(",");
					String name = a.length > 1 ? a[1].trim() : "";
					String surname = a.length > 0 ? a[0].trim() : "";
					addAuthor(name + " " + surname);
				}
			}
			else if (byLine.getClass1().equals("print_byline") && getAuthor().isEmpty())
			{
				print_byline =  byLine.toString();
			}
		}
		if (print_byline != null && !print_byline.isEmpty() && getAuthor().isEmpty())
		{
			String author = print_byline.substring(print_byline.indexOf('>')+1, print_byline.toString().lastIndexOf('<')).trim();
			if (author.startsWith("By ") || author.startsWith("by "))
			{
				author = author.substring(2).trim();
			}
			if (author.contains("Special to The New York Times"))
			{
				if (author.equalsIgnoreCase("Special to The New York Times"))
				{
					author = null;
				}
				else
				{
					if (author.startsWith("Special to The New York Times"))
					{
						author = author.substring(new String("Special to The New York Times").length());
					}
					else
					{
						author = author.substring(0, author.indexOf("Special to The New York Times")).trim();
						if (author.endsWith(",") || author.endsWith(";"))
						{
							author = author.substring(0, author.length() -1 );
						}
					}
				}
			}
			if (author.contains(":") || author.contains(";") || author.contains(","))
			{
				String separator = author.contains(":") ? ":" : author.contains(";") ? ";" : ",";
				String tempAuthor = author.substring(0, author.indexOf(separator)).trim();
				if (author.substring(author.indexOf(separator),tempAuthor.length()).equalsIgnoreCase(tempAuthor))
				{
					author = tempAuthor;
				}
			}
			String[] splitAuthor =author.split(" "); 
			if (splitAuthor.length > 5)
			{
				author = "";
				for (int i =0; i< 10 ; i++)
				{
					author = author + " " + splitAuthor[i];
				}
			}
			addAuthor(author.trim());
		}
		
		String day = null; 
		String month = null; 
		String year = null;
		for (Meta meta : head.getMetaArray())
		{
			if (meta.getName().equals("publication_day_of_month"))
			{
				day = meta.getContent().trim();
				if (day.length() > 2) day = null;
				if (day.length() == 1) day = "0" + day;
			}
			else if (meta.getName().equals("publication_month"))
			{
				month = meta.getContent().trim();
				if (month.length() > 2) month = null;
				if (month.length() == 1) month = "0" + month;
			}
			else if (meta.getName().equals("publication_year"))
			{
				year = meta.getContent().trim();
			}
		}
		if (day != null && month != null && year != null) 
		{	
			Date pDate = null;
			try
			{
				pDate = Date.valueOf(year + "-" + month + "-" + day); 
			}catch (IllegalArgumentException e) {
				// date is not in right form
			}
			setPublishDate(pDate);
		}

		for (BodyContent bc : body.getBodyContentArray()) 
		{
			for (Block b : bc.getBlockArray())
			{
				if (b.getClass1().equals("full_text"))
				{
					for (P p : b.getPArray())
					{
						String paragraph = p.toString().substring(p.toString().indexOf('>') + 1, p.toString().lastIndexOf('<')).trim();
						if (paragraph.startsWith("LEAD:")){ 
							paragraph = paragraph.substring(5).trim();
						}
						if (getLeadParagraph() != null && !getLeadParagraph().contains(paragraph))
						{
							addText(paragraph);
						}
					}
				}
				else if (b.getClass1().equals("lead_paragraph"))
				{
					String leadP = "";
					for (P p : b.getPArray())
					{
						leadP = leadP + " " + p.toString().substring(p.toString().indexOf('>') + 1, p.toString().lastIndexOf('<'));
					}
					leadP = leadP.trim();
					if (leadP.startsWith("LEAD:")){ 
						leadP = leadP.substring(5).trim();
					}
					if (leadP.length() > 1000)
					{
						leadP = leadP.substring(0, 999);
					}
					setLeadParagraph(leadP);
				}
			}
		}
		
		for (IdentifiedContent ic : head.getDocdata().getIdentifiedContentArray())
		{
			for (Person person : ic.getPersonArray())
			{
				String p = person.toString();
				p = p.substring(p.toString().indexOf('>') + 1, p.toString().lastIndexOf('<')).trim();
				String[] pStrings = p.split(",");
				String name = pStrings.length > 1 ? pStrings[1].trim() : "";
				String surname = pStrings.length > 0 ? pStrings[0].trim() : "";
				addEntity(new Entity(name + " " + surname, "person"));
			}
			for (Location location : ic.getLocationArray())
			{
				String l = location.toString();
				addEntity(new Entity(l.substring(l.indexOf('>') + 1, l.lastIndexOf('<')).trim(), "location"));
			}
			for (Org org : ic.getOrgArray())
			{
				String o = org.toString();
				addEntity(new Entity(o.substring(o.indexOf('>') + 1, o.lastIndexOf('<')).trim(), "organization"));
			}
			for (Classifier classifier : ic.getClassifierArray())
			{
				if (classifier.getType().equalsIgnoreCase("descriptor") ||classifier.getType().equalsIgnoreCase("general_descriptor"))
				{
					String kw = classifier.toString();
					kw = kw.substring(kw.indexOf('>') + 1, kw.lastIndexOf('<')).trim();
					if (!getKeywords().contains(kw))
					{
						addKeyword(kw);
					}
				}
				else if (classifier.getType().equalsIgnoreCase("taxonomic_classifier"))
				{
					String c = classifier.toString();
					addCategory(c.substring(c.indexOf('>') + 1, c.lastIndexOf('<')).trim());
				}
				else if(classifier.getType().equalsIgnoreCase("types_of_material"))
				{
					String t = classifier.toString();
					setTypesOfMaterial(t.substring(t.indexOf('>') + 1, t.lastIndexOf('<')).trim());
				}
				else
				{
					System.out.println("------------ NEW classifier: " + classifier.toString());
				}
			}
		}
	}
}
