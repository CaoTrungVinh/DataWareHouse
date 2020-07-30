package staging;

//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
//import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import datawarehouse.DataWarehouse;

public class LocalToStaging {
	private int id_config;
	private String status;
	static final String EXT_TEXT = ".txt";
	static final String EXT_CSV = ".csv";
	static final String EXT_EXCEL = ".xlsx";
	public LocalToStaging(int id_config) {
		this.id_config = id_config;
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
			// Lấy các trường có trong dòng log đầu tiên có state=ER;
			
			ArrayList<Log> listLog = dp.getCdb().getLogsWithStatus(this.status, this.id_config);
			
			for (Log log : listLog) {
				// Lấy file_name từ trong config ra
				String file_name = log.getFile_name();
				// đường dẫn chứa file download về
				String sourceFile = folder_download + File.separator + file_name;
				// Đếm số trường trong filedName ở trong bảng config
				StringTokenizer str = new StringTokenizer(field_name, delim);
				System.out.println(sourceFile);
				File file = new File(sourceFile);
				// Lấy cái đuôi file ra coi đó là kiểu file gì để xử lí đọc file
				extention = file.getPath().endsWith(".xlsx") ? EXT_EXCEL
						: file.getPath().endsWith(".txt") ? EXT_TEXT : EXT_CSV;
				if (file.exists()) {
					String values = "";
					// Nếu file là .txt thì đọc file .txt
					if (extention.equals(".txt")) {
						values = dp.readValuesTXT(file, str.countTokens());
						extention = ".txt";
						// Nếu file là .xlsx thì đọc file .xlsx
					} else if (extention.equals(".xlsx")) {
						values = dp.readValuesXLSX(file, str.countTokens());
						extention = ".xlsx";
					}
					System.out.println(values);
					// Nếu đọc được giá trị rồi
					if (values != null) {
						String table = "logs";
						String status;
						int config_id = config.getId();
						// time
//						String time_staging = new Timestamp(System.currentTimeMillis().toString.subString ) ;
						// thì mình ghi dữ liệu vô bảng, nếu mình ghi được dữ liệu vô bảng
						if (dp.insertValuesToBD(field_name, staging_table, values)) {
							status = "TR";
							// update cái log lại, chuyển file đã extract xong vào thư mục success
							dp.getCdb().updateLog(status,file_name);
							
							
							System.out.println("\t \t .....PREPARING THE TRANSFORM PROCESS TO DATAWAREHOUSE.....");
							//ĐẾN PHẦN TRANSFORM SANG DATAWAREHOUSE
							try {
								TimeUnit.SECONDS.sleep(3);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							DataWarehouse dataWarehouse = new DataWarehouse(id_config);
							dataWarehouse.run();

						} else {
							// Nếu mà bị lỗi thì update log là state=Not TR và và ghi file vào thư mục error
							status = "Not TR";
							dp.getCdb().updateLog(status, file_name);

						}
					}
				} else {
					System.out.println("Path not exists!!!");
					return;
				}
			}
	
		}
	}

	// Phương thức lấy ra thời gian hiện tạo để ghi vào log:
	public String getCurrentTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}
	
}
