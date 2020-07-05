package dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connection.ConnectionDB;

public class ControlDB {
	private String config_db_name;
	private String target_db_name;
	private String table_name;
	private PreparedStatement pst = null;
	private ResultSet rs = null;
	private String sql;

	public ControlDB(String db_name, String table_name, String target_db_name) {
		this.config_db_name = db_name;
		this.table_name = table_name;
		this.target_db_name = target_db_name;
	}

	public ControlDB() {
	}

	public String getConfig_db_name() {
		return config_db_name;
	}

	public void setConfig_db_name(String config_db_name) {
		this.config_db_name = config_db_name;
	}

	public String getTarget_db_name() {
		return target_db_name;
	}

	public void setTarget_db_name(String target_db_name) {
		this.target_db_name = target_db_name;
	}

	public String getTable_name() {
		return table_name;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	public boolean tableExist(String table_name) throws ClassNotFoundException {
		try {
			DatabaseMetaData dbm = ConnectionDB.getConnection(this.target_db_name).getMetaData();
			ResultSet tables = dbm.getTables(null, null, table_name, null);
			try {
				if (tables.next()) {
					return true;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return false;
	}

	public boolean insertValues(String field_name, String values, String staging_table) throws ClassNotFoundException {
		sql = "INSERT INTO " + staging_table + "(" + field_name + ") VALUES " + values;
		System.out.println(sql);
		try {
			
			pst = ConnectionDB.getConnection(this.target_db_name).prepareStatement(sql);
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

	public boolean insertLog(String table, String status, int id_config, String time_Staging, String file_name) throws ClassNotFoundException {
		sql = "INSERT INTO " + table
				+ "(file_name,time_Staging,status,id_config) value (?,?,?,?)";
		try {
			pst = ConnectionDB.getConnection(this.config_db_name).prepareStatement(sql);
			pst.setString(1, file_name);
			pst.setString(2, time_Staging);
			pst.setString(3, status);
			pst.setInt(4, id_config);
//			pst.setInt(4, Integer.parseInt(stagin_load_count));
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
	public boolean updateLog(int id, String fileName, String fileType, String status, String time_Staging) {
		Connection connection;
		try {
			connection = ConnectionDB.getConnection("control");
			PreparedStatement ps1 = connection.prepareStatement("UPDATE logs SET status='OKk' WHERE file_name=?");
			ps1.setString(1, fileName);
			ps1.executeUpdate();
			PreparedStatement ps = connection.prepareStatement("INSERT INTO logs (id, time_Staging, status, file_name) value (?,?,?,?,?)");
			ps.setInt(1, id);
			ps.setString(2, fileName);
			ps.setString(3, fileName.substring(fileName.indexOf('.')+1));
			ps.setString(4, time_Staging);
			ps.setString(5, status);
			
			ps.executeUpdate();
			connection.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean createTable(String table_name, String field_name) throws ClassNotFoundException {
		// 4.tạo bảng
		sql = "CREATE TABLE "+table_name+" (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,";
		String[] field = field_name.split(",");
		for(int i =0;i<field.length;i++) {
			sql+=field[i]+" "+"varchar(50)"+ " NOT NULL,";
		}
		sql = sql.substring(0,sql.length()-1)+")";
		System.out.println(sql);
		try {
			//3. mở kết nối với dbstaging.
			pst = ConnectionDB.getConnection(this.target_db_name).prepareStatement(sql);
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
