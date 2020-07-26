package staging;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExtracData {
	static final String NUMBER_REGEX = "^[0-9]+$";
	static final String DATE_FORMAT = "yyyy-MM-dd";
	private DBControl cdb;
	private String control_dbname;
	private String staging_dbname;
	private String table_name;
	public ExtracData() {
		cdb = new DBControl(this.control_dbname, this.table_name, this.staging_dbname);
	}
	public DBControl getCdb() {
		return cdb;
	}

	public void setCdb(DBControl cdb) {
		this.cdb = cdb;
	}

	public String getControl_dbname() {
		return control_dbname;
	}

	public void setControl_dbname(String control_dbname) {
		this.control_dbname = control_dbname;
	}

	public String getStaging_dbname() {
		return staging_dbname;
	}

	public void setStaging_dbname(String staging_dbname) {
		this.staging_dbname = staging_dbname;
	}

	public String getTable_name() {
		return table_name;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	// Phương thức đọc những giá trị có trong file (value), cách nhau bởi dấu phân cách (delimeter).
	private String readLines(String value, String delim) {
		String values = "";
		StringTokenizer stoken = new StringTokenizer(value, delim);
		int countToken = stoken.countTokens();
		String lines = "(";
		for (int j = 0; j < countToken; j++) {
			String token = stoken.nextToken();
			if (Pattern.matches(NUMBER_REGEX, token)) {
				lines += (j == countToken - 1) ? token.trim() + ")," : token.trim() + ",";
			} else {
				lines += (j == countToken - 1) ? "'" + token.trim() + "')," : "'" + token.trim() + "',";
			}
			values += lines;
			lines = "";
		}
		return values;
	}
	// Phương thức đọ dữ liệu trong file .txt:
	public String readValuesTXT(File s_file, int count_field) {
		if (!s_file.exists()) {
			return null;
		}
		String values = "";
		String delim = "|"; // hoặc \t
		try {
			// Đọc một dòng dữ liệu có trong file:
			BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(s_file), "utf8"));
			String line = bReader.readLine();
			if (line.indexOf("\t") != -1) {
				delim = "\t";
			}
			// Kiểm tra xem tổng số field trong file có đúng format hay không (11 trường)
			// không phải số nên là header -> bỏ qua line
			// Kiểm tra xem có phần header hay không
			if (Pattern.matches(NUMBER_REGEX, line.split(delim)[0])) {
				values += readLines(line + delim, delim);
			}
			while ((line = bReader.readLine()) != null) {
				// Nếu có field 11 thì dư khoảng trắng lên readLines() có trim(), còn 10 field thì fix lỗi out index
				values += readLines(line + " " + delim, delim);
			}
			bReader.close();
			return values.substring(0, values.length() - 1);

		} catch (NoSuchElementException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
		// Phương thức đọc dữ liệu trong file .xlsx:
		public String readValuesXLSX(File s_file, int countField) {
			String values = "";
			String value = "";
			String delim = "|";
			try {
				FileInputStream fileIn = new FileInputStream(s_file);
				XSSFWorkbook workBook = new XSSFWorkbook(fileIn);
				XSSFSheet sheet = workBook.getSheetAt(0);
				Iterator<Row> rows = sheet.iterator();
				// Kiểm tra xem có phần header hay không, nếu không có phần header 
				//Gọi rows.next, nếu có header thì vị trí dòng dữ liệu là 1. Nếu kiểm tra mà không có header thì phải set lại cái row bắt đầu ở vị trí 0
				if (rows.next().cellIterator().next().getCellType().equals(CellType.NUMERIC)) {
					rows = sheet.iterator();
				}
				while (rows.hasNext()) {
					Row row = rows.next();
					// Kiểm tra cái số trường ở trong file excel có đúng với
					// số trường có trong cái bảng mình tạo ở trong table
					// staging không
					// Bắt đầu lấy giá trị trong các ô ra:
					for (int i = 0; i < countField; i++) {
						Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						CellType cellType = cell.getCellType();
						switch (cellType) {
						case NUMERIC:
							if (DateUtil.isCellDateFormatted(cell)) {
								SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
								value += dateFormat.format(cell.getDateCellValue()) + delim;
							} else {
								value += (long) cell.getNumericCellValue() + delim;
							}
							break;
						case STRING:
							value += cell.getStringCellValue() + delim;
							break;
						case FORMULA:
							switch (cell.getCachedFormulaResultType()) {
							case NUMERIC:
								value += (long) cell.getNumericCellValue() + delim;
								break;
							case STRING:
								value += cell.getStringCellValue() + delim;
								break;
							default:
								value += " " + delim;
								break;
							}
							break;
						case BLANK:
						default:
							value += " " + delim;
							break;
						}
					}
					if (row.getLastCellNum() == countField) {
						value += "|";
					}
					values += readLines(value, delim);
					value = "";
				}
				workBook.close();
				fileIn.close();
				return values.substring(0, values.length() - 1);
			} catch (Exception e) {
				return null;
			}
		}

		// Ghi dữ liệu vô table ở trong database staging
		public boolean insertValuesToBD(String field_name, String staging_table, String values) throws ClassNotFoundException {
			if (cdb.insertValues(field_name, values, staging_table))
				return true;
			return false;
		}
	
}
