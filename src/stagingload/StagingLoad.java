package stagingload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

//import stagingload.MyConfig;import sun.tools.tree.ThisExpression;
//import sun.tools.tree.ThisExpression;
import stagingload.GetConnection;

public class StagingLoad {

	private static Object getCellValue(Cell cell, FormulaEvaluator eval) {
		DataFormatter dataFormatter = new DataFormatter();

		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue();

		case BOOLEAN:
			return cell.getBooleanCellValue();

		case NUMERIC:
			return dataFormatter.formatCellValue(cell);
		case _NONE:
			return null;
		case FORMULA:

			return dataFormatter.formatCellValue(cell, eval);

		}

		return null;
	}

	public static int executeInsertValues(MyConfig myConfig, XSSFSheet mySheet, XSSFWorkbook myWorkBook,
			int missingValues) {
		Connection connectionSta = GetConnection.getConnection("staging");
		// Lấy value từ các dạng Formula trong bảng excel

		FormulaEvaluator eval = myWorkBook.getCreationHelper().createFormulaEvaluator();
		// Get iterator to all the rows in current sheet
		Iterator<Row> rowIterator = mySheet.iterator();
		// count dùng để check dòng đầu thì bỏ qua không lấy
		int count = 0;
		DataFormatter formatter = new DataFormatter();
		// Traversing over each row of XLSX file
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if (count == 0) {
				count++;
				continue;
			}
			// For each row, iterate through each columns
			Iterator<Cell> cellIterator = row.cellIterator();
			String sqlInsert = "INSERT INTO " + myConfig.getStaging_table() + " VALUES(";
			int countColumn = 0;
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				if (countColumn == Integer.parseInt(myConfig.getNumber_cols()) - 1) {
					sqlInsert += "'" + getCellValue(cell, eval) + "');";
					countColumn++;
				} else {
					sqlInsert += "'" + getCellValue(cell, eval) + "',";
					countColumn++;
				}

			}
			int misValues = Integer.parseInt(myConfig.getNumber_cols()) - countColumn;
			missingValues += misValues;
			if (countColumn < Integer.parseInt(myConfig.getNumber_cols())) {
				for (int i = 0; i < misValues; i++) {
					if (i == misValues - 1) {
						sqlInsert += "null);";
					} else {
						sqlInsert += "null,";
					}

				}
				insertValuesIntoTable(connectionSta, sqlInsert);

			} else {
				insertValuesIntoTable(connectionSta, sqlInsert);

			}

		}
		return missingValues;
	}

	public static void insertValuesIntoTable(Connection connection, String sql) {
		PreparedStatement statement = null;
		ResultSet res = null;
		try {
			statement = connection.prepareStatement(sql);
			statement.executeUpdate();
			System.out.println("Successfully insert the row(s)");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null) {
					res.close();
				}
				if (statement != null) {
					statement.close();
				}

			} catch (SQLException e) {
				System.out.println("Khong the tao bang");
				e.printStackTrace();
			}
		}
	}

	// PhÆ°Æ¡ng thá»©c táº¡o báº£ng
//	public static boolean createTable(String staging_table, String field_name, String status)
//			throws ClassNotFoundException, SQLException, IOException {
//		// Má»Ÿ káº¿t ná»‘i vá»›i db staging
//		GetConnection connect = new GetConnection();
//		Connection connection = connect.getConnection("staging");
//		String sql = "CREATE TABLE " + staging_table + " ( id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, ";
//		String[] col = field_name.split(",");
//		for (int i = 0; i < col.length; i++) {
//			sql += col[i] + " " + "varchar(255)" + " NULL,";
//		}
//		sql = sql.substring(0, sql.length() - 1) + ")";
//		System.out.println(sql);
//		try {
//			if(status.equals("ER")){
//			PreparedStatement s = connection.prepareStatement(sql);
//			s.executeUpdate();
//			return true;
//			}else{
//				if(status.equals("Fail")){
//				System.out.println("KhÃ´ng thá»ƒ táº¡o báº£ng");}
//				
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return false;
//	}
	public static boolean createTable(MyConfig config, Connection connectionStaging) {
		boolean isCreated = false;
		PreparedStatement statement = null;
		ResultSet res = null;
		// Tạo ra một mảng từng field trong table
		String[] fields = config.getField_name().split(",");
		// Khỏi tạo câu lệnh sql create table
		String sql = "Create table " + config.getStaging_table() + " (";
		// Chạy vòng lặp để hoàn thiện câu lệnh sql
		for (int i = 0; i < fields.length; i++) {
			if (i == 0) {
				sql += fields[i] + " varchar(10) PRIMARY KEY,";
			} else if (i == fields.length - 1) {
				sql += fields[i] + " varchar(225));";
			} else {
				sql += fields[i] + " varchar(255),";
			}
		}

		// Thực hiện câu lệnh sql
		try {
			statement = connectionStaging.prepareStatement(sql);
			statement.execute();
			System.out.println("Create completed " + config.getStaging_table());
			isCreated = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null) {
					res.close();
				}
				if (statement != null) {
					statement.close();
				}

			} catch (SQLException e) {
				System.out.println("Khong the tao bang");
				e.printStackTrace();
			}
		}

		return isCreated;

	}

	private static int checkTableExist(Connection connection, String table_name, String db_name) {
		String sql = "SELECT COUNT(*)\r\n" + "FROM information_schema.tables \r\n" + "WHERE table_schema = '" + db_name
				+ "' \r\n" + "AND table_name = '" + table_name + "';";
		PreparedStatement statement = null;
		ResultSet res = null;
		try {
			statement = connection.prepareStatement(sql);
			res = statement.executeQuery();
			while (res.next()) {
				return res.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (res != null) {
					res.close();
				}
				if (statement != null) {
					statement.close();
				}

			} catch (SQLException e) {
				System.out.println("Khong the tao bang");
				e.printStackTrace();
			}
		}
		return 0;
	}

	public static void createAndInsert() throws IOException, SQLException {
		ArrayList<MyConfig> listvaluesConfig = getValuesFromConfig();

		Connection conConfig = GetConnection.getConnection("control");
		Connection connectionSta = GetConnection.getConnection("staging");
		for (MyConfig myConfig : listvaluesConfig) {
			int missingValues = 0;
			// Lấy file từ local
			File myFile = new File("D:\\\\Datawarehouse\\\\ListFolderDownload\\\\" + myConfig.getFile_name());
			FileInputStream fis = new FileInputStream(myFile);

			// Finds the workbook instance for XLSX file
			XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);

			// Return first sheet from the XLSX workbook
			XSSFSheet mySheet = myWorkBook.getSheetAt(0);
			// Kiểm tra nếu trạng thái status= ER thì mới thực hiện tiếp

			// Kiểm tra bảng nào chưa tồn tại thì tạo mới
			if (checkTableExist(connectionSta, myConfig.getStaging_table(), "staging") == 0) {
				// Nếu tạo thành công rồi thì tiến hành insert dữ liệu vao
				if (createTable(myConfig, connectionSta)) {
					missingValues = executeInsertValues(myConfig, mySheet, myWorkBook, missingValues);
				}

			} else {
				System.out.println(myConfig.getStaging_table() + " đã tồn tại");
				missingValues = executeInsertValues(myConfig, mySheet, myWorkBook, missingValues);
			}
			// time
			String timestamp = getCurrentTime();
			String file_status;
		if(missingValues>0) {
//			insertLog(myConfig, "ERROR_EMPTY_VALUES");
			file_status = "TR_fail";
			updateLogAfterLoadToStaging(file_status, timestamp);
		}else {
			
//			insertLog(myConfig, "TR");
			file_status = "TR";
			updateLogAfterLoadToStaging(file_status, timestamp);
		}

		}
	}
	public static boolean updateLogAfterLoadToStaging(String status, String fileTimeStamp) {
		Connection connection;
		String sql = "UPDATE logs SET status=?, time_Staging=? WHERE id_config";
		try {
			connection = GetConnection.getConnection("control");
			PreparedStatement ps1 = connection.prepareStatement(sql);
			ps1.setString(1, status);
			ps1.setString(2, fileTimeStamp);
			ps1.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Lay thoi gian hien tai:
	public static String getCurrentTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}

	public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
		createAndInsert();

	}

	private static ArrayList<MyConfig> getValuesFromConfig() {
		GetConnection connection = new GetConnection();
		Connection conConfig = connection.getConnection("control");
		ArrayList<MyConfig> listConfig = new ArrayList<MyConfig>();

		PreparedStatement statement = null;
		ResultSet res = null;

		// lay du lieu tu bang config
		try {
			statement = conConfig.prepareStatement("SELECT * FROM `config` JOIN logs ON config.id = logs.id WHERE status ='ER'");
			res = statement.executeQuery();
			while (res.next()) {
				MyConfig config = new MyConfig();
				config.setId(Integer.parseInt(res.getString("id")));
				config.setFile_name(res.getString("file_name"));
				config.setStaging_table(res.getString("staging_table"));
				config.setField_name(res.getString("field_name"));
				config.setNumber_cols(res.getString("number_cols"));
				listConfig.add(config);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (res != null) {
					res.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (conConfig != null) {
					conConfig.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return listConfig;

	}

//	private static ArrayList<Log> getValuesFromLog() {
////		 GetConnection connection = new GetConnection();
//		Connection conLog = GetConnection.getConnection("control");
//		ArrayList<Log> listLog = new ArrayList<Log>();
//
//		PreparedStatement statement = null;
//		ResultSet res = null;
//
//		// lay du lieu tu bang logs
//		try {
//			statement = conLog.prepareStatement("Select * from logs");
//			res = statement.executeQuery();
//			while (res.next()) {
//				Log log = new Log();
//				log.setId(Integer.parseInt(res.getString("id")));
//				log.setFile_name(res.getString("file_name"));
////		 log.setTime_Staging(res.getString("time_Staging"));
//				log.setStatus(res.getString("status"));
//				log.setId_config(res.getInt("id_config"));
//				listLog.add(log);
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			try {
//				if (res != null) {
//					res.close();
//				}
//				if (statement != null) {
//					statement.close();
//				}
//				if (conLog != null) {
//					conLog.close();
//				}
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		return listLog;
//
//	}
//
//	// Phuong thuc lay cac thuoc tinh co trong bang log:
//	public static Log getLogsWithStatus(String condition) throws SQLException {
//		// List<Log> listLog = new ArrayList<Log>();
//		Log log = new Log();
//		Connection conn = GetConnection.getConnection("staging");
//		String selectLog = "select * from logs where status=?";
//		PreparedStatement ps = conn.prepareStatement(selectLog);
//		ps.setString(1, condition);
//		ResultSet res = ps.executeQuery();
//		res.last();
//		if (res.getRow() >= 1) {
//			res.first();
//			log.setId(Integer.parseInt(res.getString("id")));
//			log.setFile_name(res.getString("file_name"));
////				 log.setTime_Staging(res.getString("time_Staging"));
//			log.setStatus(res.getString("status"));
//			log.setId_config(res.getInt("id_config"));
//		}
//		return log;
//	}
}
