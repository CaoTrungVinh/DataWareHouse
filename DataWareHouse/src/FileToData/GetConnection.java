package FileToData;

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
		String link = "config.properties";
		Connection result = null;
		
		if (location.equalsIgnoreCase("control")) {
			try (InputStream input = new FileInputStream(link)) {
				Properties prop = new Properties();
				prop.load(input);
				driver = prop.getProperty("driver_local");
				url = prop.getProperty("url_local");
				databasebName = prop.getProperty("dbName_control");
				user = prop.getProperty("user_local");
				pass = prop.getProperty("pass_local");
			} catch (IOException ex) {
				ex.printStackTrace();
			
		}
		} else if (location.equalsIgnoreCase("staging")) {
			try (InputStream input = new FileInputStream(link)) {
				Properties prop = new Properties();
				prop.load(input);
				driver = prop.getProperty("driver_local");
				url = prop.getProperty("url_local");
				databasebName = prop.getProperty("dbName_staging");
				user = prop.getProperty("user_local");
				pass = prop.getProperty("pass_local");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else if (location.equalsIgnoreCase("datawarehouse")) {
			try (InputStream input = new FileInputStream(link)) {
				Properties prop = new Properties();
				prop.load(input);
				driver = prop.getProperty("driver_local");
				url = prop.getProperty("url_local");
				databasebName = prop.getProperty("dName_datawarehouse");
				user = prop.getProperty("user_local");
				pass = prop.getProperty("pass_local");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
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
