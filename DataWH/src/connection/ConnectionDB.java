package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {

	@SuppressWarnings("unused")
	public static Connection getConnection(String db_Name) {
		Connection con = null;
		String url = "jdbc:mysql://localhost:3306/" + db_Name;
		String user = "root";
		String password = "";
		try {
			if (con == null || con.isClosed()) {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(url, user, password);
				System.out.println("thanh cong");
				return con;

			} else {
				System.out.println("ko thanh cong");
				return con;
			}
		} catch (SQLException | ClassNotFoundException e) {
			
			return null;
		}
	}
	public static void main(String[] args) {
		ConnectionDB db= new ConnectionDB();
		db.getConnection("control_data");
	}
}
