package staging;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import mail.SendMailSSL;

public class DBControl {
	private String control_dbname;
	private String staging_dbname;
	private String table_name;
	private PreparedStatement pst = null;
	private ResultSet rs = null;
	private String sql;
	SendMailSSL sendMail = null;

	public DBControl(String db_name, String table_name, String staging_dbname) {
		this.control_dbname = db_name;
		this.table_name = table_name;
		this.staging_dbname = staging_dbname;

	}

	public DBControl() {
		sendMail = new SendMailSSL();
	}

	public String getControl_dbname() {
		return control_dbname;
	}

	public String getStaging_dbname() {
		return staging_dbname;
	}

	public String getTable_name() {
		return table_name;
	}

	public void setControl_dbname(String control_dbname) {
		this.control_dbname = control_dbname;
	}

	public void setStaging_dbname(String staging_dbname) {
		this.staging_dbname = staging_dbname;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

//phương thức lấy các thuộc tính của bảng config
	public List<MyConfig> loadAllConfig(int condition) throws ClassNotFoundException, SQLException {
		// 1. Mở kết với database Control
		Connection con = GetConnection.getConnection("control");
		List<MyConfig> listConfig = new ArrayList<MyConfig>();
		// 2.Lấy dữ liệu từ bảng conig có id thỏa điều kiện
		String sql = "select * from config where id= ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1, condition);
		// 3. Nhận được ResultSet chứa các record thỏa điều kiện truy xuất
		ResultSet rs = ps.executeQuery();
		// 4. chạy từng record trong resultset
		while (rs.next()) {
			MyConfig config = new MyConfig();
			// Lấy dữ liệu lưu vào config
			config.setId(rs.getInt("id"));
			config.setFolder_download(rs.getString("folder_download"));
			config.setFile_name(rs.getString("file_name"));
			config.setDelimiter(rs.getString("delimiter"));
			config.setStaging_table(rs.getString("staging_table"));
			config.setField_name(rs.getString("field_name"));
			listConfig.add(config);
		}
		// đóng việc thực thi câu lệnh sql.
		ps.close();
		
		// trả về một listConfig
		return listConfig;
	}
	// Phương thức lấy các thuộc tính config với status = ER và theo dòng config của nó
	public ArrayList<Log> getLogsWithStatus(String condition, int id_config) throws SQLException {
		// 7.Mở kết nối với database control
		Connection conn = GetConnection.getConnection("control");
		ArrayList<Log> listLog = new ArrayList<Log>();
		Log log = null;
		// 8.Lấy dữ liệu từ bảng logs có status và id_config thỏa điều kiện
		String selectLog = "select * from logs where status=? and id_config=?";
		// Thực hiện hiện câu sql
		PreparedStatement ps = conn.prepareStatement(selectLog);
		ps.setString(1, condition);
		ps.setInt(2, id_config);
		// 9. Nhận được ResultSet chứa các record thỏa điều kiện truy xuất
		ResultSet rs = ps.executeQuery();// trả về một ResultSet khi thực hiện một câu lệnh select
		// 10. chạy từng record trong resultset và lưu dữ liệu vào listLog
		while (rs.next()) {
			log = new Log();
			log.setId(rs.getInt("id"));
			log.setFile_name(rs.getString("file_name"));
			log.setStatus(rs.getString("status"));
			log.setId_config(rs.getInt("id_config"));
			listLog.add(log);
		}
		ps.close();
		// trả về listLog
		return listLog;
	}

	public void truncateTable(Connection connection, String table_name) {
		PreparedStatement statementTruncate;
		try {
			//Thực hiện câu lệnh sql
			statementTruncate = connection.prepareStatement("TRUNCATE TABLE " + table_name);
			statementTruncate.execute();//

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("TRUNCATED " + table_name);
	}

	// Phương thức chèn giá trị vào bảng có trong db staging, giá trị có
	// được từ quá trình đọc file (file .xlsx):
	public boolean insertValues(String field_name, String values, String staging_table) throws ClassNotFoundException {
		StringTokenizer stoken = new StringTokenizer(values, "|");
		while (stoken.hasMoreElements()) {
			sql = "INSERT INTO " + staging_table + "(" + field_name + ") VALUES " + stoken.nextToken();
			System.out.println("insert successfull " + staging_table);
			try {
				pst = GetConnection.getConnection("staging").prepareStatement(sql);
				pst.executeUpdate();

			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	// Phương thức update lại log sau khi đã load file từ local lên
	// database staging thành công, cập nhật lại status=TR và cập nhật lại thời gian
	// (time_Staging) load dữ liệu lên
	public boolean updateLog(String status, String file_name) {
		Connection connection;
		String sql = "UPDATE logs SET status=?, time_Staging=? WHERE file_name=?";
		try {
			connection = GetConnection.getConnection("control");
			PreparedStatement ps1 = connection.prepareStatement(sql);
			ps1.setString(1, status);
			ps1.setString(2, new Timestamp(System.currentTimeMillis()).toString().substring(0, 19));
			ps1.setString(3, file_name);
			ps1.executeUpdate();//Thực hiên câu lệnh update
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

//phương thức kiểm tra bảng có tồn tại hay ko
	public int checkTableExist(Connection connection, String table_name, String db_name) {
		String sql = "SELECT COUNT(*)\r\n" + "FROM information_schema.tables \r\n" + "WHERE table_schema = '" + db_name
				+ "' \r\n" + "AND table_name = '" + table_name + "';";

		try {
			pst = connection.prepareStatement(sql);
			rs = pst.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}

			} catch (SQLException e) {
				System.out.println("Khong the tao bang");
				e.printStackTrace();
			}
		}
		return 0;
	}

	public boolean createTable(String table_name, String field_name) {
		System.out.println("create");
		//Lấy các field_name từ config
		String[] fields = field_name.split(",");
		//Khởi tạo câu tạo bảng và tạo khóa chính
		sql = "CREATE TABLE " + table_name + " ( ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,";
		// Chạy vòng for để tạo các cột field_name 
		for (int i = 0; i < fields.length; i++) {
			sql += fields[i] + " " + "varchar(255)" + " NOT NULL,";
		}
		
		sql = sql.substring(0, sql.length() - 1) + ")";
		System.out.println(sql);
		try {
			pst = GetConnection.getConnection("staging").prepareStatement(sql);
			pst.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (pst != null)
					pst.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}

}
