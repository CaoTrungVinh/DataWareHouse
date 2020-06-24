package FileToData;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.MyConfig;

public class ConfigConnection {
	private static String URL = "jdbc:mysql://localhost:3306/configuration";
	private static String PW = "123456";
	private static String USER = "root";

	public  Connection getConnectionConfig() {
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

	public  void insertValues(MyConfig config) {
		String sql = "INSERT INTO `configuration`.`myconfig` (`URLconnection`, `sourcePath`,`filename`, `user`, `password`, `dbname`, `tablename`, `listField`, `dbnameCopy`, `tablenameCopy`, `listFieldCopy`) "
				+ "VALUES ('jdbc:mysql://localhost:3306/', '" + config.getSourcePath() + "', '" + config.getFilename() + "', " + "'" + config.getUser() + "', '"
				+ config.getPassword() + "', '" + config.getDbname() + "', '" + config.getTablename() + "', '" + config.getListField() + "', '" + config.getDbnameCopy()
				 + "', '" + config.getTablenameCopy()+ "', '" + config.getListFieldCopy() + "');";
		Long id = 1l;
		Connection connection = getConnectionConfig();
		PreparedStatement statement = null;
		ResultSet res = null;
				
		
		try {
			statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			statement.execute();
			res = statement.getGeneratedKeys();
			if (res.next()) {
				id = res.getLong(1);
			}
			System.out.println("Insert row completed, primary key :" + id);

		} catch (SQLException e) {

			System.out.println("Can't insert into table mycofig");
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
				if(statement != null) {
					statement.close();
				}
				if(res != null) {
					res.close();
				}

			} catch (SQLException sqlE) {
				sqlE.printStackTrace();
			}
		}
	}
	
}
