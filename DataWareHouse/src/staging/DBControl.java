package staging;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
public class DBControl {
	private String control_dbname;
	private String staging_dbname;
	private String table_name;
	private PreparedStatement pst = null;
	private ResultSet rs = null;
	private String sql;

	public DBControl(String db_name, String table_name, String staging_dbname) {
		this.control_dbname = db_name;
		this.table_name = table_name;
		this.staging_dbname = staging_dbname;
	}

	public DBControl() {
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

	public PreparedStatement getPst() {
		return pst;
	}

	public ResultSet getRs() {
		return rs;
	}

	public String getSql() {
		return sql;
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

	public void setPst(PreparedStatement pst) {
		this.pst = pst;
	}

	public void setRs(ResultSet rs) {
		this.rs = rs;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public boolean tableExist(String table_name) throws ClassNotFoundException {
		try {
			DatabaseMetaData dbm = GetConnection.getConnection("staging").getMetaData();
			ResultSet tables = dbm.getTables(null, null, table_name, null);
			try {
				if (tables.next()) {
					System.out.println(true);
					return true;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println(false);
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return false;
	}
	
	public static List<MyConfig> loadAllConfig(String file_name) throws ClassNotFoundException, SQLException {
		List<MyConfig> listConfig = new ArrayList<MyConfig>();
		String sql = "select * from config where config_name = ?";
		Connection con = GetConnection.getConnection("control");
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, file_name);
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			MyConfig config = new MyConfig();
			config.setId(rs.getInt("id"));
			config.setSource_host(rs.getString("source_host"));
			config.setUser_name(rs.getString("user_name"));
			config.setPassword(rs.getString("password"));
			config.setList_file(rs.getString("list_file"));
			config.setFolder_download(rs.getString("folder_download"));
			config.setExtension_file(rs.getString("extension_file"));
//			config.setFile_name(rs.getString("file_name"));
			config.setConfig_name(rs.getString("config_name"));
			config.setDelimiter(rs.getString("delimiter"));
			config.setStaging_table(rs.getString("staging_table"));
			config.setField_name(rs.getString("field_name"));
			config.setNumber_cols(rs.getString("number_cols"));
			listConfig.add(config);
		}
		ps.close();
		return listConfig;
	}
	// Phương thức lấy một dòng log đầu tiên trong table log có state = ER
		public static ArrayList<Log> getLogsWithStatus(String condition) throws SQLException {
			Log log = null;
			ArrayList<Log> listLog = new ArrayList<Log>();
			Connection conn = GetConnection.getConnection("control");
			String selectLog = "select * from logs where status=?";
			PreparedStatement ps = conn.prepareStatement(selectLog);
			ps.setString(1, condition);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				log.setId(rs.getInt("id"));
				log.setFile_name(rs.getString("file_name"));
				log.setStatus(rs.getString("status"));
				log.setId_config(rs.getInt("id_config"));
				listLog.add(log);
			}
			return listLog;
		}

		// Phương thức chèn giá trị vào bảng có trong db staging, giá trị có
		// được từ quá trình đọc file (file .xlsx):
	public boolean insertValues(String field_name, String values, String staging_table) throws ClassNotFoundException {
		StringTokenizer stoken = new StringTokenizer(values, "|");
		while (stoken.hasMoreElements()) {
			sql = "INSERT INTO " + staging_table + "(" + field_name + ") VALUES " +  stoken.nextToken() ;
			System.out.println(sql);
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
	// Phương thức update lại log sau khi đã extract file từ local lên
		// database staging thành công, cập nhật lại status=TR
		// dateLoadToStaging=getCurrentTime();
		public boolean updateLog(String status, String time_Staging, String file_name) {
			Connection connection;
			String sql = "UPDATE logs SET status=?, time_Staging=? WHERE file_name=?";
			try {
				connection = GetConnection.getConnection("control");
				PreparedStatement ps1 = connection.prepareStatement(sql);
				ps1.setString(1, status);
				ps1.setString(2, time_Staging);
				ps1.setString(3, file_name);
				ps1.executeUpdate();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}


//	public boolean insertLog(String table, String status, int config_id, String timestamp,
//			String stagin_load_count, String file_name) throws ClassNotFoundException {
//		sql = "INSERT INTO " + table
//				+ "(config_id, file_name, state, staging_timestamp, download_timestamp,transform_timestamp,staging_count, transform_count) values(?,?,?,?,?,?,?,?)";
//		try {
//			pst = GetConnection.getConnection("control").prepareStatement(sql);
//			pst.setString(1, file_name);
//			pst.setInt(2, config_id);
//			pst.setString(3, file_status);
//			pst.setInt(4, Integer.parseInt(stagin_load_count));
//			pst.setString(5, timestamp);
//			pst.executeUpdate();
//			return true;
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return false;
//		} finally {
//			try {
//				if (pst != null)
//					pst.close();
//				if (rs != null)
//					rs.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//
//		}
//	}
//	public boolean updateLog(int config_id, String file_name, String state, Date staging_timestamp) throws ClassNotFoundException {
//		Connection connection;
//		try {
//			connection = GetConnection.getConnection("control");
//			PreparedStatement ps1 = connection.prepareStatement("UPDATE logs SET WHERE file_name=?");
//			ps1.setString(1, file_name);
//			ps1.executeUpdate();
//			PreparedStatement ps = connection.prepareStatement("INSERT INTO logs (config_id, file_name, file_type, status, file_timestamp, active) value (?,?,?,?,?,1)");
//			ps.setInt(1, config_id);
//			ps.setString(2, file_name);
//			ps.setString(3, state);
//			ps.setDate(4, staging_timestamp);
//			ps.executeUpdate();
//			connection.close();
//			return true;
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}

//	public boolean createTable(String table_name, String field_name) throws ClassNotFoundException {
//		System.out.println("create");
//		sql = "CREATE TABLE "+table_name+" (STT INT NOT NULL AUTO_INCREMENT PRIMARY KEY,";
//		String[] fields = field_name.split(",");
////		String[] col = column_list.split(",");
//		for(int i =0;i<fields.length;i++) {
//			sql+=fields[i]+" "+"varchar(255)"+ " NOT NULL,";
//		}
//		sql = sql.substring(0,sql.length()-1)+")";
//		System.out.println(sql);
//		try {
//			pst = GetConnection.getConnection("staging").prepareStatement(sql);
//			pst.executeUpdate();
//			return true;
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return false;
//		} finally {
//			try {
//				if (pst != null)
//					pst.close();
//				if (rs != null)
//					rs.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//
//		}
//	}
	// Hàm main này để test các phương thức trên chạy ổn hay chưa:
		public static void main(String[] args) throws SQLException  {
			DBControl cb = new DBControl("control","config","staging");
			ArrayList<Log> listlog = cb.getLogsWithStatus("ER");
			for (Log log2 : listlog) {
				System.out.println(log2.toString());
			}
			
			
		}

//	public static void main(String[] args) throws ClassNotFoundException, SQLException {
//		DatabaseMetaData dbm = GetConnection.getConnection("control").getMetaData();
//		ResultSet tables = dbm.getTables(null, null, "student", null);
//		while(!tables.next()) {
//			System.out.println("???");
//		}
//	}
}
