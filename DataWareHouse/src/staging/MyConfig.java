package staging;

public class MyConfig {
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
	private String field_name;
	private String number_cols;
	private String datawarehouse_table;
	private String cols_date;
	public MyConfig() {
	}
	//contructor 
	public MyConfig(int id, String source_host, String user_name, String password, String list_file,
			String folder_download, String error_dir, String success_dir, String extension_file, String file_name,
			String delimiter, String staging_table, String field_name, String number_cols, String datawarehouse_table,
			String cols_date) {
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
		this.field_name = field_name;
		this.number_cols = number_cols;
		this.datawarehouse_table = datawarehouse_table;
		this.cols_date = cols_date;
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


	public String getError_dir() {
		return error_dir;
	}


	public String getSuccess_dir() {
		return success_dir;
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


	public String getField_name() {
		return field_name;
	}


	public String getNumber_cols() {
		return number_cols;
	}


	public String getDatawarehouse_table() {
		return datawarehouse_table;
	}


	public String getCols_date() {
		return cols_date;
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


	public void setError_dir(String error_dir) {
		this.error_dir = error_dir;
	}


	public void setSuccess_dir(String success_dir) {
		this.success_dir = success_dir;
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


	public void setField_name(String field_name) {
		this.field_name = field_name;
	}


	public void setNumber_cols(String number_cols) {
		this.number_cols = number_cols;
	}


	public void setDatawarehouse_table(String datawarehouse_table) {
		this.datawarehouse_table = datawarehouse_table;
	}


	public void setCols_date(String cols_date) {
		this.cols_date = cols_date;
	}
	@Override
	public String toString() {
		return "MyConfig [id=" + id + ", source_host=" + source_host + ", user_name=" + user_name + ", password="
				+ password + ", list_file=" + list_file + ", folder_download=" + folder_download + ", error_dir="
				+ error_dir + ", success_dir=" + success_dir + ", extension_file=" + extension_file + ", file_name="
				+ file_name + ", delimiter=" + delimiter + ", staging_table=" + staging_table + ", field_name="
				+ field_name + ", number_cols=" + number_cols + ", datawarehouse_table=" + datawarehouse_table
				+ ", cols_date=" + cols_date + "]";
	}
	public static void main(String[] args) {
		MyConfig donlao = new MyConfig();
		System.out.println(donlao.toString());
	}
	
}