package staging;

//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//import java.util.Properties;

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
				pass = "123456";
			
		
		} else if (location.equalsIgnoreCase("staging")) {
			driver = "com.mysql.jdbc.Driver";
			url = "jdbc:mysql://localhost:3306/";
			databasebName = "staging?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&characterEncoding=UTF-8";
			user = "root";
			pass = "123456";
		} else if (location.equalsIgnoreCase("datawarehouse")) {
			driver = "com.mysql.jdbc.Driver";
			url = "jdbc:mysql://localhost:3306/";
			databasebName = "datawarehouse";
			user = "root";
			pass = "123456";
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
	public static void main(String[] args) {
		Connection conn = new GetConnection().getConnection("control");
		if (conn != null) {
			System.out.println("Thanh cong");
		}
	}

}
