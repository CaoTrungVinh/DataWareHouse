package model;

public class MyConfig {
	private String URLconnection;
	private String sourcePath;
	private String filename;
	private String user;
	private String password;
	private String dbname;
	private String tablename;
	private String listField;
	private String dbnameCopy;
	private String tablenameCopy;
	private String listFieldCopy;

	public MyConfig() {
		formatField();
	}

	public MyConfig(String uRLconnection, String sourcePath, String filename, String user, String password,
			String dbname, String tablename, String listField, String dbnameCopy, String tablenameCopy,
			String listFieldCopy) {
		MyConfig config = new MyConfig();

		URLconnection = uRLconnection;
		this.sourcePath = sourcePath;
		this.filename = filename;
		this.user = user;
		this.password = password;
		this.dbname = dbname;
		this.tablename = tablename;
		this.listField = listField;
		this.dbnameCopy = dbnameCopy;
		this.tablenameCopy = tablenameCopy;
		this.listFieldCopy = listFieldCopy;
	}

	public void formatField() {
		if (this.URLconnection == "" || this.URLconnection == null) {
			this.URLconnection = "no data";
		}
		if (this.sourcePath == "" || this.sourcePath == null) {
			this.sourcePath = "no data";
		}
		if (this.filename == "" || this.filename == null) {
			this.filename = "no name";
		}
		if (this.user == "" || this.user == null) {
			this.user = "root";
		}
		if (this.password == "" || this.password == null) {
			this.password = "123456";
		}
		if (this.dbname == "" || this.dbname == null) {
			this.dbname = "staging";
		}
		if (this.tablename == "" || this.tablename == null) {
			this.tablename = "";
		}

		if (this.listField == "" || this.listField == null) {
			this.listField = "id int auto_increment";
		}
		if (this.dbnameCopy == "" || this.dbnameCopy == null) {
			this.dbnameCopy = "datawarehouse";
		}
		if (this.tablenameCopy == "" || this.tablenameCopy == null) {
			this.tablenameCopy = this.tablename;
		}
		if (this.listFieldCopy == "" || this.listFieldCopy == null) {
			this.listFieldCopy = this.listField;
		}

	}

	public String getURLconnection() {
		return URLconnection;
	}

	public void setURLconnection(String uRLconnection) {
		URLconnection = uRLconnection;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getTablename() {

		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
		if (this.tablenameCopy == "" || this.tablenameCopy == null) {
			this.tablenameCopy = tablename;
		}
		
	}

	public String getListField() {
		return listField;
	}

	public void setListField(String listField) {
		this.listField = listField;
		if(this.listFieldCopy ==""||this.listFieldCopy==null) {
			this.listFieldCopy = this.listField;
		}
	}

	public String getDbnameCopy() {
		return dbnameCopy;
	}

	public void setDbnameCopy(String dbnameCopy) {
		this.dbnameCopy = dbnameCopy;
	}

	public String getTablenameCopy() {
		return tablenameCopy;
	}

	public void setTablenameCopy(String tablenameCopy) {
		this.tablenameCopy = tablenameCopy;
	}

	public String getListFieldCopy() {
		return listFieldCopy;
	}

	public void setListFieldCopy(String listFieldCopy) {
		this.listFieldCopy = listFieldCopy;
		
	}

}
