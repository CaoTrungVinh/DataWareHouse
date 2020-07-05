package control;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connection.ConnectionDB;

public class MyConfig {
	private int id;
	private String source_host;
	private String user_name;
	private String password;
	private String list_file;
	private String folder_download;
	private String folder_error;
	private String folder_success;
	private String extension_file;
	private String file_name;
	private String delimiter;
	private String staging_table;
	private String field_name;
//	private String variabless;
	private String number_cols;
	private String datawarehouse_table;
	public MyConfig(int condition) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM config WHERE id=?";
		Connection conn;
		try {
			conn = ConnectionDB.getConnection("control");
			pst = conn.prepareStatement(sql);
			pst.setInt(1,condition );
			rs = pst.executeQuery();
			while (rs.next()) {
				this.id = condition;
				source_host = rs.getString("source_host");
				user_name = rs.getString("user_name");
				password = rs.getString("password");
				list_file = rs.getString("list_file");
				folder_download = rs.getString("folder_download");
				folder_success = rs.getString("folder_success");
				folder_error = rs.getString("folder_error");
				extension_file = rs.getString("extension_file");
				file_name = rs.getString("file_name");
				delimiter = rs.getString("delimiter");
				staging_table = rs.getString("staging_table");
				field_name = rs.getString("field_name");
//				variabless = rs.getString("variabless");
				number_cols = rs.getString("number_cols");
				datawarehouse_table= rs.getString("datawarehouse_table");
			}

		} catch (Exception e) {
			e.printStackTrace();
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
	public int getId() {
		return id;
	}
	public String getSource_host() {
		return source_host;
	}
	public String getUser_name() {
		return user_name;
	}
	public String getPassword() {
		return password;
	}
	public String getList_file() {
		return list_file;
	}
	public String getFolder_download() {
		return folder_download;
	}
	public String getFolder_error() {
		return folder_error;
	}
	public String getFolder_success() {
		return folder_success;
	}
	public String getExtension_file() {
		return extension_file;
	}
	public String getFile_name() {
		return file_name;
	}
	public String getDelimiter() {
		return delimiter;
	}
	public String getStaging_table() {
		return staging_table;
	}
//	public String getVariabless() {
//		return variabless;
//	}
	public String getNumber_cols() {
		return number_cols;
	}
	public String getDatawarehouse_table() {
		return datawarehouse_table;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setSource_host(String source_host) {
		this.source_host = source_host;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setList_file(String list_file) {
		this.list_file = list_file;
	}
	public void setFolder_download(String folder_download) {
		this.folder_download = folder_download;
	}
	public void setFolder_error(String folder_error) {
		this.folder_error = folder_error;
	}
	public void setFolder_success(String folder_success) {
		this.folder_success = folder_success;
	}
	public void setExtension_file(String extension_file) {
		this.extension_file = extension_file;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	public void setStaging_table(String staging_table) {
		this.staging_table = staging_table;
	}
//	public void setVariabless(String variabless) {
//		this.variabless = variabless;
//	}
	public void setNumber_cols(String number_cols) {
		this.number_cols = number_cols;
	}
	public void setDatawarehouse_table(String datawarehouse_table) {
		this.datawarehouse_table = datawarehouse_table;
	}
	
	public String getField_name() {
		return field_name;
	}
	public void setField_name(String field_name) {
		this.field_name = field_name;
	}
	
	@Override
	public String toString() {
		return "MyConfig [id=" + id + ", source_host=" + source_host + ", user_name=" + user_name + ", password="
				+ password + ", list_file=" + list_file + ", folder_download=" + folder_download + ", folder_error="
				+ folder_error + ", folder_success=" + folder_success + ", extension_file=" + extension_file
				+ ", file_name=" + file_name + ", delimiter=" + delimiter + ", staging_table=" + staging_table
				+ ", field_name=" + field_name + ", number_cols=" + number_cols
				+ ", datawarehouse_table=" + datawarehouse_table + "]";
	}
	public static void main(String[] args) {

		MyConfig myconfig = new MyConfig(1);
		System.out.println(myconfig.toString());

	}
	
}
