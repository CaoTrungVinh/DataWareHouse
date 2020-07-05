package model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.compress.archivers.dump.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import control.Log;
import control.MyConfig;
import dao.ControlDB;

public class DataStaging {
	private int id;
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
public static void main(String[] args) throws ClassNotFoundException {
	DataStaging dw = new DataStaging();
	dw.setId(1);
	dw.setStatus("OK");;
	DataProcess dp = new DataProcess();
	ControlDB cdb = new ControlDB();
	cdb.setConfig_db_name("control");
	cdb.setTarget_db_name("database_staging");
	cdb.setTable_name("config");
	dp.setCdb(cdb);
	dw.ExtractToDB(dp);
}
public void ExtractToDB(DataProcess dp) throws ClassNotFoundException {
	MyConfig myconfig = new MyConfig(this.id);
	String staging_table = myconfig.getStaging_table();
	String extension_file = myconfig.getExtension_file();
	String folder_download = myconfig.getFolder_download();
	String delim = myconfig.getDelimiter();
	String field_name = myconfig.getField_name();
//	String variabless = myconfig.getVariabless();
	System.out.println(staging_table);

	if (!dp.getCdb().tableExist(staging_table)) {
//		System.out.println(variabless);
		dp.getCdb().createTable(staging_table, field_name);
	}

	// File imp_dir = new File(import_dir);
	// if (imp_dir.exists()) {
	// // String extention = "";
	// // Log a = new Log();
	// List<Log> list = a.getLogsWithStatus("ER");
	// for (Log log : list) {
	// File file = new File(import_dir + "\\" + log.getFileName());
	// if (file.exists() && log.getFileStatus().equals("OK") &&
	// log.getActive() == 1) {
	// System.out.println(file.getName());
	// }
	// }
	File imp_dir = new File(folder_download);
	if (imp_dir.exists()) {
		String extention = "";
		Log a = new Log();
		List<Log> listLog = a.getLogsWithStatus("OK");
		File[] listFile = imp_dir.listFiles();
		for (File file : listFile) {
			for (Log log : listLog) {
				String file_name = file.getName().replaceAll(extension_file, "");
				if (file_name.equals(log.getFile_name()) && log.getStatus().equals("OK")) {
					System.out.println(file.getName());
					if (file.getName().indexOf(extension_file) != -1) {
						System.out.println(7);
						String values = "";
						if (extension_file.equals(".txt")) {
							values = dp.readValuesTXT(file, delim);
							extention = ".txt";
						} else if (extension_file.equals(".xlsx")) {
							values = dp.readValuesXLSX(file);
							extention = ".xlsx";
						}
						if (values != null) {
							String table = "logs";
							String file_status;
							int config_id = myconfig.getId();
							// time
							DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
							LocalDateTime now = LocalDateTime.now();
							String timestamp = dtf.format(now);
							// count line
							String stagin_load_count = "";
							try {
								stagin_load_count = countLines(file, extention) + "";
							} catch (InvalidFormatException
									| org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
								e.printStackTrace();
							}
							//
							String target_dir;

							if (dp.writeDataToBD(field_name, staging_table, values)) {
								file_status = "OK";
								dp.getCdb().insertLog(table, file_status, config_id, timestamp,
										file_name);
								target_dir = myconfig.getFolder_success();
								if (moveFile(target_dir, file))
									;

							} else {
								file_status = "ERR";
								dp.getCdb().insertLog(table, file_status, config_id, timestamp,
										file_name);
								target_dir = myconfig.getFolder_error();
								if (moveFile(target_dir, file))
									;

							}
						}
					}
				}
			}

		}

	} else {
		System.out.println("Path not exists!!!");
		return;
	}
}
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

private int countLines(File file, String extention)
		throws InvalidFormatException, org.apache.poi.openxml4j.exceptions.InvalidFormatException {
	int result = 0;
	XSSFWorkbook workBooks = null;
	try {
		if (extention.indexOf(".txt") != -1) {
			BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line;
			while ((line = bReader.readLine()) != null) {
				if (!line.trim().isEmpty()) {
					result++;
				}
			}
			bReader.close();
		} else if (extention.indexOf(".xlsx") != -1) {
			workBooks = new XSSFWorkbook(file);
			XSSFSheet sheet = workBooks.getSheetAt(0);
			Iterator<Row> rows = sheet.iterator();
			rows.next();
			while (rows.hasNext()) {
				rows.next();
				result++;
			}
			return result;
		}

	} catch (IOException | org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
		e.printStackTrace();
	} finally {
		if (workBooks != null) {
			try {
				workBooks.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	return result;
}
}
