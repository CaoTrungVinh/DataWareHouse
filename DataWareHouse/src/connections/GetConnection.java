package connections;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class GetConnection {
	static String driver = null;
	static String url = null;
	static String user = null;
	static String pass = null;
	static String databasebName = null;

	public static Connection getConnection(String location) {
		Connection result = null;
		
		if (location.equalsIgnoreCase("control")) {
				driver = "com.mysql.jdbc.Driver";
				url = "jdbc:mysql://localhost:3306/";
				databasebName = "control";
				user = "root";
				pass = "";
			
		
		} else if (location.equalsIgnoreCase("staging")) {
			driver = "com.mysql.jdbc.Driver";
			url = "jdbc:mysql://localhost:3306/";
			databasebName = "staging";
			user = "root";
			pass = "";
		} else if (location.equalsIgnoreCase("datawarehouse")) {
			driver = "com.mysql.jdbc.Driver";
			url = "jdbc:mysql://localhost:3306/";
			databasebName = "datawarehouse";
			user = "root";
			pass = "";
		} else {
			System.out.println("Nhap ten database sai !!!");

		}
		try {
			Class.forName(driver);
			String connectionURL = url + databasebName;
			try {
				result = DriverManager.getConnection(connectionURL, user, pass);
			} catch (SQLException e) {
				System.out.println("Loi ket noi, kiem tra lai");
				System.exit(0);
				e.printStackTrace();
			}

		} catch (ClassNotFoundException e) {
			System.out.println("Khong thay file config");
			System.exit(0);
			e.printStackTrace();
		}

		return result;
	}
// HƯỚNG DẪN SỬ DỤNG TRƯỚC KHI DÙNG
	public static void main(String[] args) {
		
		Connection conn = new GetConnection().getConnection("control");
		if (conn != null) {
			System.out.println("Thanh cong");
		}
	}

}
