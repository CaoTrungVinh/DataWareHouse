package nghia.stagingtowarehouse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import FileToData.GetConnection;
import model.MyConfig;

public class SimulatorStaging {

	public static void main(String[] args) throws Exception {
		// Tạo bảng và insert dữ liệu vào staging
		createAndInsert();


	}

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

	private static void createAndInsert() throws IOException {
		ArrayList<MyConfig> listvaluesConfig = getValuesFromConfig();
		GetConnection conectControl = new GetConnection();
		Connection connectionSta = conectControl.getConnection("staging");

		for (MyConfig myConfig : listvaluesConfig) {
			int missingValues = 0;

			// Lấy file từ local
			File myFile = new File(
					"D:\\GitHub\\DataWareHouse\\DataWareHouse\\ListFileDownload\\" + myConfig.getFile_name());
			FileInputStream fis = new FileInputStream(myFile);

			// Finds the workbook instance for XLSX file
			XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);

			

			// Return first sheet from the XLSX workbook
			XSSFSheet mySheet = myWorkBook.getSheetAt(0);

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
			if(missingValues>0) {
				insertLog(myConfig, "ERROR_EMPTY_VALUES");
			}else {
				insertLog(myConfig, "TR");
			}

		}
	}
	public static int executeInsertValues(MyConfig myConfig,XSSFSheet mySheet,XSSFWorkbook myWorkBook,int missingValues) {
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
//				System.out.print(getCellValue(cell,eval)+"\t");

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

	public static void insertLog(MyConfig myConfig, String status) {
		PreparedStatement statement = null;
		int id_config = myConfig.getId();

		String sql = "Insert into logs(id_config,time_Staging,status) VALUES(" + id_config + "," + "now(),'" + status
				+ "')";
		Connection connection = GetConnection.getConnection("control");
		try {
			statement = connection.prepareStatement(sql);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {

				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}

			} catch (SQLException e) {
				System.out.println("Khong the tao bang");
				e.printStackTrace();
			}
		}
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

	public static boolean createTable(MyConfig config, Connection connectionStaging) {
		boolean isCreated = false;
		PreparedStatement statement = null;
		ResultSet res = null;
		// Tạo ra một mảng từng field trong table
		String[] fields = config.getVariabless().split(",");
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

	private static ArrayList<MyConfig> getValuesFromConfig() {
		GetConnection connection = new GetConnection();
		Connection conConfig = connection.getConnection("control");
		ArrayList<MyConfig> listConfig = new ArrayList<MyConfig>();

		PreparedStatement statement = null;
		ResultSet res = null;

		// lay du lieu tu bang config
		try {
			statement = conConfig.prepareStatement(
					"Select id,staging_table,variabless,file_name,number_cols,query_insert_datawarehouse from config");
			res = statement.executeQuery();
			while (res.next()) {
				MyConfig config = new MyConfig();
				config.setId(Integer.parseInt(res.getString("id")));
				config.setFile_name(res.getString("file_name"));
				config.setStaging_table(res.getString("staging_table"));
				config.setVariabless(res.getString("variabless"));
				config.setNumber_cols(res.getString("number_cols"));
				config.setQuery_insert_datawarehouse(res.getString("query_insert_datawarehouse"));
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

}
