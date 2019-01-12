package Sql;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
/*
 * The class is for get the statistics from yhe SQL database of this project and get our place in specific map Example
 */
public class SqlGame {

	private int mapId;
	private double myPoints;
	private int myPlace;



/*
 * Constructor for the statistics we get for the map
 * @param statistics String that is the result of the game
 * @param mapId the Example we want to compare
 */
	public SqlGame(String statistics, int mapId) {
		this.mapId = mapId;
		String statisticsLines[] ;
		String points[];
		statisticsLines = statistics.split(",");
		String scores = statisticsLines[2];
		points = scores.split(":");
		this.myPoints = Double.parseDouble(points[1]);

		ServerConnection();
	}
/*
 * This function is connecting to SQL server and compare the results of the students to us in specific example and print our Place
 * 
 */
	public void ServerConnection() {
		String jdbcUrl="jdbc:mysql://ariel-oop.xyz:3306/oop"; //?useUnicode=yes&characterEncoding=UTF-8&useSSL=false";
		String jdbcUser="student";
		String jdbcPassword="student";
		int map=0;
		ArrayList<Double> scoresOfMap = new ArrayList<Double>();
		
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = 
					DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);


			Statement statement = connection.createStatement();

			//select data
			String allCustomersQuery = "SELECT * FROM logs;";
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);

			while(resultSet.next())
			{
				map=resultSet.getInt("SomeDouble");
				
				if (map == this.mapId) {
					scoresOfMap.add(resultSet.getDouble("Point"));
				}
			}

			resultSet.close();		
			statement.close();		
			connection.close();	
			
			Collections.sort(scoresOfMap);

			int place = 0;
			for(int i = 0 ; i < scoresOfMap.size() ; i++) {
				if(scoresOfMap.get(i) <= myPoints) {
					place=i;
				}
			}
			
			this.myPlace =place+1;
			
			System.out.println("My Place : "+ myPlace+ "For Example: "+ map);

		}

		catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		}

		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}

