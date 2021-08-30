package connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	private  String URL;
	private  String PW;
	private  String USER;
	public DBConnection(String urlConnection, String password, String user) {
		URL = urlConnection;
		PW = password;
		USER = user;
		
	}
	public static  Connection getConnettion(String url, String password, String user) {
		Connection connect = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(url,user,password);
			
		} catch (ClassNotFoundException|SQLException e) {
			System.out.println("Fail Connect");
			e.printStackTrace();
		} 
		return connect;
	}
	
	
}
