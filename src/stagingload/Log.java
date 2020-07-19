package stagingload;

import java.sql.Date;

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
