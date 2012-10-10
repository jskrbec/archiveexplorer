package searchpoint;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import db.Article;
import db.Entity;
import db.EntityList;

public class XmlGenerator 
{
	private ArrayList<Article> m_corpusIds;
	private Connection m_conn;
	private ArrayList<Entity> m_entities = new ArrayList<Entity>();
	EntityList persons;	
	EntityList orgs;
	EntityList locations;
	EntityList otherEntities;
	String xml;
	public XmlGenerator (ArrayList<Article> corpusIds, Connection conn)
	{
		m_corpusIds = corpusIds;
		m_conn = conn;
		persons = new EntityList(m_conn);
		orgs = new EntityList(m_conn);
		locations = new EntityList(m_conn);
		otherEntities = new EntityList(m_conn);
		xml = "";
	}
	
	public String generate() throws ParserConfigurationException
	{
		getEntities();
		m_entities.addAll(persons);
		m_entities.addAll(orgs);
		m_entities.addAll(locations);
		m_entities.addAll(otherEntities);
		
		Document document;
		DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dBF.newDocumentBuilder();
		document = builder.newDocument();
		
		Element root = document.createElement("AllData");
		document.appendChild(root);
		
		Node indexNode = createIndex(document);
		root.appendChild(indexNode);
		
		Node invertedNode = createInverted(document);
		root.appendChild(invertedNode);
		
		Node filtersNode = createFilters(document);
		root.appendChild(filtersNode);
		
		// write the XML document to disk
		try {
			// create DOMSource for source XML document
			Source xmlSource = new DOMSource(document);

//			// create StreamResult for transformation result
//			Result result = new StreamResult(new FileOutputStream(f));
//
			// create TransformerFactory
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
//
			// create Transformer for transformation
			Transformer transformer = transformerFactory.newTransformer();

//			transformer.setOutputProperty("indent", "yes");	//Java XML Indent
//
//			// transform and deliver content to client
//			transformer.transform(xmlSource, result);
			
			
			StringWriter writer = new StringWriter();
			Result resultStr = new StreamResult(writer);
			transformer.transform(xmlSource, resultStr);
			writer.close();
			xml = writer.toString();
		}

		// handle exception creating TransformerFactory
		catch (TransformerFactoryConfigurationError factoryError) {
			System.err.println("Error creating " + "TransformerFactory");
			factoryError.printStackTrace();
		} catch (TransformerException transformerError) {
			System.err.println("Error transforming document");
			transformerError.printStackTrace();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
		
		return xml;
	}
	
	public String getXml()
	{
		return xml;
	}
	
	private Node createIndex(Document doc)
	{		
		Element index = doc.createElement("Index");
		
		for (Article a : m_corpusIds)
		{
			Element d = doc.createElement("d");
			d.setAttribute("Id", a.getId()+"");
			d.setAttribute("Score", a.getSearchScore()+"");

			String tags = "";

			for (Entity e : m_entities)
			{
				if (e.getArticleIds().contains(a.getId()))
				{
					tags = tags + e.getId() + ",";
				}
			}
			if (!tags.isEmpty())
			{
				tags = tags.substring(0, tags.length()-1);
			}
			d.setAttribute("Tags", tags);
			index.appendChild(d);
		}
		
		return index;
	}
	
	private Node createInverted(Document doc)
	{		
		Element inverted = doc.createElement("Inverted");
		
		for (Entity e : m_entities)
		{
			Element t = doc.createElement("t");
			t.setAttribute("TId", e.getId() + "");
			String docIds = "";
			for (Integer docId : e.getArticleIds())
			{
				docIds = docIds + docId + ",";
			}
			docIds = docIds.substring(0, docIds.length()-1);
			t.setAttribute("DIds", docIds);
			
			inverted.appendChild(t);
		}
		
		return inverted;
	}
	
	private Node createFilters(Document doc)
	{
		Element person = doc.createElement("person");
		Element organization = doc.createElement("organization");
		Element location = doc.createElement("location");
		Element other = doc.createElement("other");
		
		createFilterType(persons, person, doc);
		createFilterType(orgs, organization, doc);
		createFilterType(locations, location, doc);
		createFilterType(otherEntities, other, doc);
		
		
		Element filters = doc.createElement("Filters");
		
		filters.appendChild(person);
		filters.appendChild(organization);
		filters.appendChild(location);
		filters.appendChild(other);
		
		return filters;
	}
	
	private void createFilterType(ArrayList<Entity> entities, Element node, Document doc)
	{
		ArrayList<Integer> doneEnt = new ArrayList<Integer>();
		for (Entity e : entities)
		{
			Element t = doc.createElement("t");
			t.setAttribute("ID", e.getId() + "");
			t.setAttribute("Name", e.getName());
			t.setAttribute("Num", e.getArticleIds().size() + "");
			node.appendChild(t);
			
			for (Entity e2 : entities)
			{
				if (!e.equals(e2) && !doneEnt.contains(e2.getId()))
				{
					ArrayList<Integer> artE = e.getArticleIds();
					ArrayList<Integer> artE2 = e2.getArticleIds();
					int countSameArt = 0;
					for (Integer artId : artE)
					{
						if (artE2.contains(artId))
						{
							countSameArt++;
						}
					}
					if (countSameArt > 0)
					{
						Element l = doc.createElement("l");
						l.setAttribute("ID1", e.getId() + "");
						l.setAttribute("ID2", e2.getId() + "");
						l.setAttribute("Num", countSameArt + "");
						node.appendChild(l);
					}
				}
				doneEnt.add(e.getId());
			}
		}
	}
	
	private void getEntities()
	{
		String articleIds = "";
		for (Article a : m_corpusIds)
		{
			articleIds = articleIds + "," + a.getId();
		}
		articleIds = articleIds.substring(1);
		
		persons.getEntitiesForXml(articleIds, "person");
		orgs.getEntitiesForXml(articleIds, "organization");
		locations.getEntitiesForXml(articleIds, "location");
		otherEntities.getEntitiesForXml(articleIds, null);
	}
}
