package staging;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

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

	public void loadData() throws ClassNotFoundException, SQLException {
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
		System.out.println("Nhập id config cần load: ");
		int id = sc.nextInt();
		LocalToStaging dw = new LocalToStaging(id);
		dw.loadData();
	}

	public void ExtractToDB(ExtracData dp) throws ClassNotFoundException, SQLException {
		// 1. Mở Kết nối với database control -----> 2.Lấy dữ liệu từ bảng config dựa trên điều kiện ID
		// 3. Trả về một ResultSet thỏa điều kiện truy xuất -----> 4. Chạy từng record trong resultset và lưu dữ liệu vào config
		List<MyConfig> listConf = dp.getCdb().loadAllConfig(this.id_config);
		// Lấy các trường trong các dòng config ra:
		for (MyConfig config : listConf) {
			String staging_table = config.getStaging_table();
			String folder_download = config.getFolder_download();
			String delim = config.getDelimiter();
			String field_name = config.getField_name();
			System.out.println(staging_table);// in ra tên table staging
			System.out.println(folder_download);// in ra thư mục chứa file
			// 5. Mở kết nối với database Staging
			GetConnection conectControl = new GetConnection();
			Connection connectionSta = conectControl.getConnection("staging");
			// 6.kiểm tra bảng đã tồn tại hay chưa
			if (dp.getCdb().checkTableExist(connectionSta, staging_table, "staging") == 0) {
				// 6.1 gọi hàm tạo bảng nếu chưa tạo bảng
				dp.getCdb().createTable(staging_table, field_name);
			} else {
				// 6.2 gọi hàm trumcatetable
				dp.getCdb().truncateTable(connectionSta, staging_table);
				// in ra thông báo nếu bảng đã tồn tại
				System.out.println("Bảng " + staging_table + " đã tồn tại sãn sàng insert dữ liệu!!!!");
			}
			// 7. Mở kết nối database control ----> 8. Lấy ra dữ liệu từ bảng logs theo điều kiện status= 'ER' và id_confiig
			// 9. Trả về một ResultSet -------> 10. Chạy từng dòng record lưu dữ liệu vào listLogs
			ArrayList<Log> listLog = dp.getCdb().getLogsWithStatus(this.status, this.id_config);
			for (Log log : listLog) {
				
				// Đếm số trường trong field_name ở trong bảng config
				StringTokenizer str = new StringTokenizer(field_name, delim);
				// Lấy thuộc tính file_name từ trong logs
				String file_name = log.getFile_name();
				// 11. Kiểm tra file có tồn tại trên folder_download ở local chưa
				String sourceFile = folder_download + File.separator + file_name;
				File file = new File(sourceFile);// mo file
				System.out.println(file);
				if (file.exists()) {
					String values = "";// tạo 1 biến lưu dữ liệu file đọc được.
					// 12. Đọc dữ liêu của file
					values = dp.readValuesXLSX(file, str.countTokens());
					// Nếu đọc được giá trị rồi
					if (values != null) {
						String table = "logs";
						String status;
						String time_Staging;
						int config_id = config.getId();
						dp.getCdb().truncateTable(connectionSta, staging_table);
						// 13. Insert dữ liệu vào trong bảng của db staging
						// thì mình ghi dữ liệu vô bảng, nếu mình ghi được dữ liệu vô bảng
						if (dp.insertValuesToBD(field_name, staging_table, values)) {
							status = "TR";
							// 14. gửi mail thông báo thành công với status ="TR" và thời gian insert thành
							// công
							sendMail.sendMail("[SUCCESS] INSERT DATA TO DATABASE STAGING",
									staging_table + " update " + status + " TR");
							// 15. update cái logs với status ="TR" và thời gian insert thành công
							dp.getCdb().updateLog(status, file_name);
							System.out.println("\t \t .....PREPARING THE TRANSFORM PROCESS TO DATAWAREHOUSE.....");
							// ĐẾN PHẦN TRANSFORM SANG DATAWAREHOUSE
							DataWarehouse dataWarehouse = new DataWarehouse(id_config);
							dataWarehouse.start();
							System.out.println(values);

						} else {
							status = "Not TR";
							// 14. gửi mail thông báo lỗi với status ="TR" và thời gian insert không thành
							// công
							sendMail.sendMail("[ERROR] INSERT DATA TO DATABASE STAGING",
									staging_table + " update " + status + "Not TR");
							// 15. Cập nhật lại logs với status ="TR" và thời gian insert không thành công
							dp.getCdb().updateLog(status, file_name);

						}
					}

				} else {
					// in ra nếu đường dẫn không tồn tại
					System.out.println("Path not exists!!!");
					// trả về rỗng
					return;
				}
			}

		}
	}
}
