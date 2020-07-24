package staging;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
//import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
//import java.io.InputStreamReader;
//import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
//import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

//import org.apache.commons.compress.archivers.dump.InvalidFormatException;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;

//import staging.MyConfig;
//import staging.DBControl;
//import staging.Log;

public class LocalToStaging {
	static final String EXT_TEXT = ".txt";
	static final String EXT_CSV = ".csv";
	static final String EXT_EXCEL = ".xlsx";
	private String file_name;
	private String status;
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFile_name() {
		return file_name;
	}
	public String getStatus() {
		return status;
	}
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		LocalToStaging dw = new LocalToStaging();
		dw.setFile_name("f_monhoc");
		dw.setStatus("ER");
		ExtracData dp = new ExtracData();
		DBControl cdb = new DBControl();
		cdb.setStaging_dbname("staging");
		cdb.setControl_dbname("control");
		cdb.setTable_name("config");
		dp.setCdb(cdb);
		dw.ExtractToDB(dp);
	}
	public void ExtractToDB(ExtracData dp) throws ClassNotFoundException, SQLException {
		List<MyConfig> listConf = dp.getCdb().loadAllConfig(this.file_name);
		// Lấy các trường trong các dòng config ra:
		for (MyConfig config : listConf) {
			String extention = "";
			String staging_table = config.getStaging_table();
			String folder_download = config.getFolder_download();
			String delim = config.getDelimiter();
			String field_name = config.getField_name();
			
			System.out.println(staging_table);
			System.out.println(folder_download);
			// Lấy các trường có trong dòng log đầu tiên có state=ER;
			Log log = dp.getCdb().getLogsWithStatus(this.status);
			// Lấy file_name từ trong config ra
			String file_name = log.getFile_name();
			// Ráp với importDir đề được cái đường dẫn tới file
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
						String time_staging = getCurrentTime();
						String target_dir;
						// thì mình ghi dữ liệu vô bảng, nếu mình ghi được dữ liệu vô bảng
						if (dp.writeDataToBD(field_name, staging_table, values)) {
						status = "TR";
							// update cái log lại, chuyển file đã extract xong vào thư mục success
							dp.getCdb().updateLog(status, time_staging, file_name);
//							target_dir = config.getSuccess_dir();
//							if (moveFile(target_dir, file))
//								;

						} else {
							// Nếu mà bị lỗi thì update log là state=Not TR và và ghi file vào thư mục error
							status = "Not TR";
							dp.getCdb().updateLog(status, time_staging, file_name);
//							target_dir = config.getError_dir();
//							if (moveFile(target_dir, file))
//								;
						}
					}
			} else {
				System.out.println("Path not exists!!!");
				return;
			}
		}
	}
	// Phương thức lấy ra thời gian hiện tạo để ghi vào log:
	public String getCurrentTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}

	// Phương thức chuyển file vào các thư mục (success, error):
	private boolean moveFile(String target_dir, File file) {
		try {
			BufferedInputStream bReader = new BufferedInputStream(new FileInputStream(file));
			BufferedOutputStream bWriter = new BufferedOutputStream(
					new FileOutputStream(target_dir + File.separator + file.getName()));
			byte[] buff = new byte[1024 * 10];
			int data = 0;
			while ((data = bReader.read(buff)) != -1) {
				bWriter.write(buff, 0, data);
			}
			bReader.close();
			bWriter.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			file.delete();
		}
	}
}
