package control;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectionDB;

public class Log {
	private int id;
	private String file_name;
	private Date time_download;
	private Date time_Staging;
	private Date time_Datawarehouse;
	private String status;
	private int id_config;
	private int rows_update_datawarehouse;
	public Log() {
		// TODO Auto-generated constructor stub
	}
//	public Log(int dataFileID, String fileName, int dataFileConfigID, String fileStatus, int stagingLoadCount, String action, int active) {
//		this.dataFileID = dataFileID;
//		this.fileName = fileName;
//		this.dataFileConfigID = dataFileConfigID;
//		this.fileStatus = fileStatus;
//		this.stagingLoadCount = stagingLoadCount;
//		this.fileTimestamp = fileTimestamp;
//		this.action = action;
//		this.active = active;
//	}
	public Log(int id, String file_name, String status,int id_config, int rows_update_datawarehouse) {
		super();
		this.id = id;
		this.file_name = file_name;
		this.time_download = time_download;
		this.time_Staging = time_Staging;
		this.time_Datawarehouse = time_Datawarehouse;
		this.status = status;
		this.id_config = id_config;
		this.rows_update_datawarehouse = rows_update_datawarehouse;
	}
	public List<Log> getLogsWithStatus(String condition) {
		List<Log> list = new ArrayList<>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM logs where status = ?";
		Connection conn;
		try {
			conn = ConnectionDB.getConnection("control");
			pst = conn.prepareStatement(sql);
			pst.setString(1, condition);
			rs = pst.executeQuery();
			while(rs.next()) {
				list.add(new Log(rs.getInt("id"),
						rs.getString("file_name"),
						rs.getString("status"),
						rs.getInt("id_config"), 
						rs.getInt("rows_update_datawarehouse")));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if (pst != null)
					pst.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return list;
	}
	public boolean insertLog(int id_config, String fileName, String extension_file, String status, String time_Staging ) {
		Connection connection;
		try {
			connection = ConnectionDB.getConnection("control");
			PreparedStatement ps1 = connection.prepareStatement("UPDATE logs SET status=ERR WHERE file_name=?");
			ps1.setString(1, fileName);
			ps1.executeUpdate();
			PreparedStatement ps = connection.prepareStatement("INSERT INTO logs (config_id, file_name, status, time_Staging) value (?,?,?,?,?)");
			ps.setInt(1, id_config);
			ps.setString(2, fileName);
			ps.setString(3, fileName.substring(fileName.indexOf('.')+1));
			ps.setString(4, status);
			ps.setString(5, time_Staging);
			ps.executeUpdate();
			connection.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	public static void main(String[] args) {
		Log log = new Log();
		System.out.println(log.getLogsWithStatus("OK"));
	}
	public int getId() {
		return id;
	}
	public String getFile_name() {
		return file_name;
	}
	public Date getTime_download() {
		return time_download;
	}
	public Date getTime_Staging() {
		return time_Staging;
	}
	public Date getTime_Datawarehouse() {
		return time_Datawarehouse;
	}
	public String getStatus() {
		return status;
	}
	public int getId_config() {
		return id_config;
	}
	public int getRows_update_datawarehouse() {
		return rows_update_datawarehouse;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	public void setTime_download(Date time_download) {
		this.time_download = time_download;
	}
	public void setTime_Staging(Date time_Staging) {
		this.time_Staging = time_Staging;
	}
	public void setTime_Datawarehouse(Date time_Datawarehouse) {
		this.time_Datawarehouse = time_Datawarehouse;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setId_config(int id_config) {
		this.id_config = id_config;
	}
	public void setRows_update_datawarehouse(int rows_update_datawarehouse) {
		this.rows_update_datawarehouse = rows_update_datawarehouse;
	}
	@Override
	public String toString() {
		return "Log [id=" + id + ", file_name=" + file_name + ", time_download=" + time_download + ", time_Staging="
				+ time_Staging + ", time_Datawarehouse=" + time_Datawarehouse + ", status=" + status + ", id_config="
				+ id_config + ", rows_update_datawarehouse=" + rows_update_datawarehouse + "]";
	}
	
}
