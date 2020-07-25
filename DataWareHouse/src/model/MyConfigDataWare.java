package model;

public class MyConfigDataWare {
	private int id;
	private String source_host;
	private String user_name;
	private String password;
	private String list_file;
	private String folder_download;
	private String error_dir;
	private String success_dir;
	private String extension_file;
	private String file_name;
	private String delimiter;
	private String staging_table;
	private String variabless;
	private String number_cols;
	private String datawarehouse_table;
	private String cols_date;
	
	
	private int id_log;
	
	
	
	
	public MyConfigDataWare() {
	}




	//contructor 
	public MyConfigDataWare(int id, String source_host, String user_name, String password, String list_file,
			String folder_download, String error_dir, String success_dir, String extension_file, String file_name,
			String delimiter, String staging_table, String variabless, String number_cols,
			String datawarehouse) {
		super();
		this.id = id;
		this.source_host = source_host;
		this.user_name = user_name;
		this.password = password;
		this.list_file = list_file;
		this.folder_download = folder_download;
		this.error_dir = error_dir;
		this.success_dir = success_dir;
		this.extension_file = extension_file;
		this.file_name = file_name;
		this.delimiter = delimiter;
		this.staging_table = staging_table;
		this.variabless = variabless;
		this.number_cols = number_cols;
		this.datawarehouse_table = datawarehouse;
	}




	public String getCols_date() {
		return cols_date;
	}




	public void setCols_date(String cols_date) {
		this.cols_date = cols_date;
	}




	public int getId_log() {
		return id_log;
	}




	public void setId_log(int id_log) {
		this.id_log = id_log;
	}




	public String getDatawarehouse_table() {
		return datawarehouse_table;
	}


	public void setDatawarehouse_table(String datawarehouse_table) {
		this.datawarehouse_table = datawarehouse_table;
	}




	public int getId() {
		return id;
	}




	public void setId(int id) {
		this.id = id;
	}




	public String getSource_host() {
		return source_host;
	}




	public void setSource_host(String source_host) {
		this.source_host = source_host;
	}




	public String getUser_name() {
		return user_name;
	}




	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}




	public String getPassword() {
		return password;
	}




	public void setPassword(String password) {
		this.password = password;
	}




	public String getList_file() {
		return list_file;
	}




	public void setList_file(String list_file) {
		this.list_file = list_file;
	}




	public String getFolder_download() {
		return folder_download;
	}




	public void setFolder_download(String folder_download) {
		this.folder_download = folder_download;
	}




	public String getError_dir() {
		return error_dir;
	}




	public void setError_dir(String error_dir) {
		this.error_dir = error_dir;
	}




	public String getSuccess_dir() {
		return success_dir;
	}




	public void setSuccess_dir(String success_dir) {
		this.success_dir = success_dir;
	}




	public String getExtension_file() {
		return extension_file;
	}




	public void setExtension_file(String extension_file) {
		this.extension_file = extension_file;
	}




	public String getFile_name() {
		return file_name;
	}




	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}




	public String getDelimiter() {
		return delimiter;
	}




	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}




	public String getStaging_table() {
		return staging_table;
	}




	public void setStaging_table(String staging_table) {
		this.staging_table = staging_table;
	}




	public String getVariabless() {
		return variabless;
	}




	public void setVariabless(String variabless) {
		this.variabless = variabless;
	}




	public String getNumber_cols() {
		return number_cols;
	}




	public void setNumber_cols(String number_cols) {
		this.number_cols = number_cols;
	}



	
	
	
	
	

}