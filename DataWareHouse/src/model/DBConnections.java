package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnections {
	public static Connection getConnection(String url, String userName, String passWord) throws SQLException {
		Connection connection = DriverManager.getConnection(url, userName, passWord);
		return connection;
	}
}
