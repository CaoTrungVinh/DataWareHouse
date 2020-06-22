package FileToData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConfigConnection {
	private static String URL = "jdbc:mysql://localhost:3306/configuration";
	private static String PW = "123456";
	private static String USER = "root";

	public static Connection getConnectionConfig() {
		Connection connect = null;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(URL, USER, PW);

		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("Fail Connect");
			e.printStackTrace();
		}
		return connect;

	}
}
