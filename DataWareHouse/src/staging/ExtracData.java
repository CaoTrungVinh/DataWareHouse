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
	private DBControl cdb;// gọi class DBControl
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
	private String readLines(String value, String delim) {
		String values = "";
		StringTokenizer stoken = new StringTokenizer(value, delim);//tạo ra một lớp StringTokenizer dựa trên chuỗi value và dấu phân cách.
		int countToken = stoken.countTokens();//trả về tổng số lượng token
		String lines = "(";
		// duyêt các token trong chuổi
		for (int j = 0; j < countToken; j++) {
			String token = stoken.nextToken();//Trả về token tiếp theo khi duyệt StringTokenizer.
			if (Pattern.matches(NUMBER_REGEX, token)) {
				//nếu duyệt thấy token kế tiếp là số thì ko cần thêm dấu nháy đơn
				lines += (j == countToken - 1) ? token.trim() + ")," : token.trim() + ",";
			} else {
				//nếu duyệt thấy token kế tiếp là chữ thì thêm dấu nháy đơn cho nó
				lines += (j == countToken - 1) ? "'" + token.trim() + "')," : "'" + token.trim() + "',";
			}
			values += lines;
			lines = "";
		}
		return values;
	}
		// Phương thức đọc dữ liệu trong file .xlsx:
		public String readValuesXLSX(File s_file, int countField) {
			String values = "";
			String value = "";
			String delim = "|";
			try {
				//
				FileInputStream fileIn = new FileInputStream(s_file);
				XSSFWorkbook workBook = new XSSFWorkbook(fileIn);//các class xử lý với Excel Workbook
				XSSFSheet sheet = workBook.getSheetAt(0);// các class xử lý với Excel Worksheet
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
								//cung cấp phương thức để định dạng và phân tích ngày tháng và thời gian
								SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
								value += dateFormat.format(cell.getDateCellValue()) + delim;//dùng để chuyển đổi date thành string 
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
