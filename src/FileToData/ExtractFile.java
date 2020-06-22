package FileToData;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class ExtractFile {
	String url_1 = "jdbc:mysql://localhost/data?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&characterEncoding=UTF-8";
	String userName_1 = "root";
	String passWord_1 = "";

	String url_2 = "jdbc:mysql://localhost/datacopy?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&characterEncoding=UTF-8";
	String userName_2 = "root";
	String passWord_2 = "";

	public void load(String Filetxt) throws ClassNotFoundException, SQLException, IOException {

		Connection connect = DBConnection.getConnection(url_1, userName_1, passWord_1);
		System.out.println("Connect DB Successfully :)");

		BufferedReader lineReader = new BufferedReader(new FileReader(Filetxt));
		String lineText = null;

		int count = 0;
		String sql;

		lineText = lineReader.readLine();
		String[] fields = lineText.split("\t");
		System.out.println(fields.length);
		System.out.println(lineText);
		// create table
		sql = "CREATE table data(" + fields[0] + " CHAR(50)," + fields[1] + " CHAR(50)," + fields[2]
				+ " CHAR(50)," + fields[3] + " CHAR(50)," + fields[4] + " CHAR(50)," + fields[5] + " CHAR(50)," + fields[6] + " CHAR(50))";
		System.out.println(sql);
		PreparedStatement preparedStatement = connect.prepareStatement(sql);
		preparedStatement.execute();
		System.out.println("Create table Successfully :)");
		

		// skip header line
		String query = "INSERT INTO data VALUES(?, ?, ?, ?, ?, ?,?)";
		PreparedStatement pre = connect.prepareStatement(query);
		while ((lineText = lineReader.readLine()) != null) {
			String[] data = lineText.split("\t");
			System.out.println(data);
			
			String stt = data[0];
			String mssv = data[1];
			String fname = data[2];
			String lname = data[3];
			String gender = data[4];
			String birhDay = data[5];
			String address = data[6];
			pre.setString(1, stt);
			pre.setString(2, mssv);
			pre.setString(3, fname);
			pre.setString(4, lname);
			pre.setString(5, gender);
			pre.setString(6, birhDay);
			pre.setString(7, address);
			pre.execute();
		}
	}

	public void copy(String database1, String database2, String nameDB) throws ClassNotFoundException, SQLException {
		Connection connectionDB1 = DBConnection.getConnection(url_1, userName_1, passWord_1);
		System.out.println("ok");
		Connection connectionDB2 = DBConnection.getConnection(url_2, userName_2, passWord_2);
		System.out.println("ok");

		ResultSet rs;
		Statement stmt = connectionDB1.createStatement();
		rs = stmt.executeQuery("SELECT * FROM data");
		ResultSetMetaData md = (ResultSetMetaData) rs.getMetaData();
		int counter = md.getColumnCount();
		String colName[] = new String[counter];

		System.out.println("The column names are as follows:");

		for (int loop = 1; loop <= counter; loop++) {
			colName[loop - 1] = md.getColumnLabel(loop);
//			sqlCreateTable += colName[loop - 1] + " CHAR(50),";
		}

		String sqlCreateTable = "CREATE table " + nameDB + "copy" + "(" + colName[0] + " VARCHAR(15)," + colName[1]
				+ " CHAR(50)," + colName[2] + " CHAR(50)," + colName[3] + " CHAR(50)," + colName[4] + " CHAR(50),"
				+ colName[5] + " CHAR(50),"+ colName[6] + " CHAR(50))";
//		sqlCreateTable += ")";

		System.out.println(sqlCreateTable);
		
		PreparedStatement p = connectionDB2.prepareStatement(sqlCreateTable);
		p.execute();

//		COPY 
		String insert = "INSERT INTO " + database2 + "." + nameDB + "copy " + "SELECT * FROM " + database1 + "."
				+ nameDB;
		System.out.println(insert);
		PreparedStatement pc = connectionDB2.prepareStatement(insert);
		pc.execute();
	}

	public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
		ExtractFile ex = new ExtractFile();
		String urlFile = "E:\\AI\\DataWareHouse\\src\\FileToData\\thongtincanhan.txt";
//		ex.load(urlFile);
		ex.copy("data", "datacopy", "data");
	}
}
