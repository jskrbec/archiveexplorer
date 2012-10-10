import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;


public class SocialContext 
{
	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException 
	{
		ArrayList<Integer> ents = new ArrayList<Integer>();
		ArrayList<String> outputStrings = new ArrayList<String>();
		
		Class.forName("org.postgresql.Driver");
		Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/corpusdb", "appUser", "bla");
		String queryM = "SELECT e.id FROM ENTITY e WHERE (e.id % 3000) = 0 ORDER BY e.id";
		try {
			ResultSet rs = conn.createStatement().executeQuery(queryM);
			while (rs.next())
			{
				ents.add(rs.getInt("id"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Integer eid : ents)
		{
			int cntPerson = 0;
			int cntOrgs = 0;
			int cntLoc = 0;
			int cntAll = 0;
			int cntConnAll = 0;
			ArrayList<Integer> tempEntIds = new ArrayList<Integer>();
			HashSet<Integer> tempSet = new HashSet<Integer>();
			String queryCon = "SELECT distinct ec.entity_id FROM ENTITY_CORPUS ec " +
			"WHERE  ec.corpus_id IN (SELECT corpus_id FROM entity_corpus where entity_id = " + eid + ") ";
			try {
				ResultSet rs = conn.createStatement().executeQuery(queryCon);
				while (rs.next())
				{
					cntAll++;
					tempEntIds.add(rs.getInt("entity_id"));
					tempSet.add(rs.getInt("entity_id"));
				}
				System.out.println(eid);
				for (Integer tempId : tempEntIds)
				{
					String q2 = "SELECT distinct entity_id FROM ENTITY_CORPUS " +
					"WHERE corpus_id IN (SELECT corpus_id FROM entity_corpus where entity_id = " + tempId + ") ";
					ResultSet rs2 = conn.createStatement().executeQuery(q2);
					while (rs2.next())
					{
						if (tempSet.contains(rs2.getInt("entity_id")))
						{
							cntConnAll++;
						}
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String line = eid + "," + cntAll + "," + cntPerson + "," + cntOrgs + "," + cntLoc + "," + (cntConnAll/cntAll);
			outputStrings.add(line);
			System.out.println(line);
		}

//		String path = "G:/Users/jasna.MARQUIS/projects/socialContext/";
		String path = "D:/Users/jasna/projects/socialContext/";
		File errFile = new File(path + "Entities1.txt");
		try {
			errFile.createNewFile();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		PrintWriter output = new PrintWriter(new FileWriter(errFile));
		output.println("entityId,cntAll,cntPerson,cntOrgs,cntLoc,avgConn");
		for (String line : outputStrings)
		{
			output.println(line);
		}
		output.close();
	}
}
