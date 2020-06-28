package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
	@SuppressWarnings("unused")
	public static Connection createConnection(String db_Name) {
		Connection con = null;
		String url = "jdbc:mysql://localhost:3306/" + db_Name;
		String user = "root";
		String password = "";
		try {
			if (con == null || con.isClosed()) {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(url, user, password);
				System.out.println("thành cong");
				return con;

			} else {
				System.out.println("fail");
				return con;
			}
		} catch (SQLException | ClassNotFoundException e) {
			return null;
		}
	}
//	public static void main(String[] args) {
//		ConnectionDB c =   new ConnectionDB();
//		c.createConnection("dbcontrol");
//		
//	}
}