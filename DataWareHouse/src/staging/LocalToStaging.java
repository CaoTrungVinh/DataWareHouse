package staging;


import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import datawarehouse.DataWarehouse;
import mail.SendMailSSL;
public class LocalToStaging {
	SendMailSSL sendMail = null;
	private int id_config;
	private String status;
	public LocalToStaging(int id_config) {
		this.id_config = id_config;
		sendMail = new SendMailSSL();
	}

	public int getId_config() {
		return id_config;
	}

	public void setId_config(int id_config) {
		this.id_config = id_config;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
	public void run() throws ClassNotFoundException, SQLException {
		LocalToStaging dw = new LocalToStaging(1);
		dw.setId_config(id_config);
		dw.setStatus("ER");
		ExtracData dp = new ExtracData();
		DBControl cdb = new DBControl();
		cdb.setStaging_dbname("staging");
		cdb.setControl_dbname("control");
		cdb.setTable_name("config");
		dp.setCdb(cdb);
		dw.ExtractToDB(dp);
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Scanner sc = new Scanner(System.in);
		System.out.println("Nhập id config cần download: ");
		int id = sc.nextInt();
		LocalToStaging dw = new LocalToStaging(id);
		dw.run();
	}

	public void ExtractToDB(ExtracData dp) throws ClassNotFoundException, SQLException {
		List<MyConfig> listConf = dp.getCdb().loadAllConfig(this.id_config);
//		List<Log> listLog = dp.getCdb().getLogsWithStatus(this.status, this.id_config);
		GetConnection conectControl = new GetConnection();
		Connection connectionSta = conectControl.getConnection("staging");
		// Lấy các trường trong các dòng config ra:
		for (MyConfig config : listConf) {
			String staging_table = config.getStaging_table();
			String folder_download = config.getFolder_download();
			String delim = config.getDelimiter();
			String field_name = config.getField_name();
			String extention = "";
			System.out.println(staging_table);
			System.out.println(folder_download);

			if (dp.getCdb().checkTableExist(connectionSta, staging_table, "staging") == 0) {
				System.out.println();
				dp.getCdb().createTable(staging_table, field_name);
			} else {
				System.out.println("Bảng " + staging_table + " đã tồn tại sãn sàng insert dữ liệu!!!!");
			}
			// Lấy các trường có trong dòng log đầu tiên có state=ER với dòng config của nó
			ArrayList<Log> listLog = dp.getCdb().getLogsWithStatus(this.status, this.id_config);
//
			for (Log log : listLog) {
				// Lấy thuộc tính file_name từ trong logs
				String file_name = log.getFile_name();
				// đường dẫn chứa file download về
				String sourceFile = folder_download + File.separator + file_name;
				// Đếm số trường trong field_name ở trong bảng config
				StringTokenizer str = new StringTokenizer(field_name, delim);
				File file = new File(sourceFile);
				if (file.exists()) {
					String values = "";
					
						values = dp.readValuesXLSX(file, str.countTokens());
						
//					System.out.println(values);
					// Nếu đọc được các các giá trị trong file
					if (values != null) {
						String table = "logs";
						String status;
						int config_id = config.getId();			
							// load dữ liệu vô bảng, nếu mình ghi được dữ liệu vô bảng
							if (dp.insertValuesToBD(field_name, staging_table, values)) {
								status = "TR";
								sendMail.sendMail("[SUCCESS] LOAD DATA TO STAGING",
										staging_table + " update " + status + " TR");
								// update cái log lại với status là TR
								dp.getCdb().updateLog(status, file_name);
								System.out.println("\t \t .....PREPARING THE TRANSFORM PROCESS TO DATAWAREHOUSE.....");
								// ĐẾN PHẦN TRANSFORM SANG DATAWAREHOUSE
								try {
									TimeUnit.SECONDS.sleep(3);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								DataWarehouse dataWarehouse = new DataWarehouse(id_config);
								dataWarehouse.run();

							} else {
								// Nếu mà bị lỗi thì update log là state=Not TR 
								status = "Not TR";
								sendMail.sendMail("[ERROR] LOAD DATA TO STAGING",
										staging_table + " update " + status + " Not TR");
								dp.getCdb().updateLog(status, file_name);

							}
						}
					} else {
						System.out.println("Path not exists!!!");
						return;
//					}
				}
			}
		}
	}

}
