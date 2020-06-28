package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import util.ConnectionDB;

public class Configuration {
	private int configID;
	private String configName;
	private String configDes;
	private String targetTable;
	private String fileType;
	private String importDir;
	private String successDir;
	private String errorDir;
	private String columnList;
	private String delimeter;
	private String variabless;

	public Configuration(String condition) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM configuration WHERE config_name=?";
		Connection conn;
		try {
			conn = ConnectionDB.createConnection("dbcontrol");
			pst = conn.prepareStatement(sql);
			pst.setString(1, condition);
			rs = pst.executeQuery();
			while (rs.next()) {
				configID = rs.getInt("config_id");
				this.configName = condition;
				configDes = rs.getString("config_des");
				targetTable = rs.getString("target_table");
				fileType = rs.getString("file_type");
				importDir = rs.getString("import_dir");
				successDir = rs.getString("success_dir");
				errorDir = rs.getString("error_dir");
				columnList = rs.getString("column_list");
				delimeter = rs.getString("delimeter");
				variabless = rs.getString("variabless");
			}

		} catch (Exception e) {

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

	public int getConfigID() {
		return configID;
	}

	public String getConfigName() {
		return configName;
	}

	public String getConfigDes() {
		return configDes;
	}

	public String getTargetTable() {
		return targetTable;
	}

	public String getFileType() {
		return fileType;
	}

	public String getImportDir() {
		return importDir;
	}

	public String getSuccessDir() {
		return successDir;
	}

	public String getErrorDir() {
		return errorDir;
	}

	public String getColumnList() {
		return columnList;
	}

	public String getDelimeter() {
		return delimeter;
	}

	public String getVariabless() {
		return variabless;
	}

	public void setConfigID(int configID) {
		this.configID = configID;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public void setConfigDes(String configDes) {
		this.configDes = configDes;
	}

	public void setTargetTable(String targetTable) {
		this.targetTable = targetTable;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public void setImportDir(String importDir) {
		this.importDir = importDir;
	}

	public void setSuccessDir(String successDir) {
		this.successDir = successDir;
	}

	public void setErrorDir(String errorDir) {
		this.errorDir = errorDir;
	}

	public void setColumnList(String columnList) {
		this.columnList = columnList;
	}

	public void setDelimeter(String delimeter) {
		this.delimeter = delimeter;
	}

	public void setVariabless(String variabless) {
		this.variabless = variabless;
	}

	@Override
	public String toString() {
		return "Configuration [configID=" + configID + ", configName=" + configName
				+ ", configDes=" + configDes + ", targetTable=" + targetTable + ", fileType=" + fileType
				+ ", importDir=" + importDir + ", successDir=" + successDir + ", errorDir=" + errorDir + ", columnList="
				+ columnList + ", delimeter=" + delimeter + ", variabless=" + variabless + "]";
	}

	public static void main(String[] args) {
		
		Configuration configuration = new Configuration("f_txt");
		System.out.println(configuration.toString());
		
	}

}
