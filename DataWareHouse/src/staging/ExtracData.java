package staging;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Iterator;
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
		//tạo ra 1 lớp StringTokenizer phân tách các value theo dấu phân cách
		StringTokenizer stoken = new StringTokenizer(value, delim);
		int countToken = stoken.countTokens();
		String lines = "(";
		// chạy dòng for để duyệt các stoken để đưa dlieu vào lines
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

	// Phương thức đọc dữ liệu trong file .xlsx:
	public String readValuesXLSX(File s_file, int countField) {
		String values = "";
		String value = "";
		String delim = "|";
		try {
			FileInputStream fileIn = new FileInputStream(s_file);
			XSSFWorkbook workBook = new XSSFWorkbook(fileIn);// khai báo file xlsx
			XSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.iterator();// Lấy ra cái danh sách các hàng
			// Nếu cột đầu tiên, hàng đầu tiên là dạng số thì bắt đầu lấy dữ liệu 
			// Còn dạng chữ thì bỏ qua
			if (rows.next().cellIterator().next().getCellType().equals(CellType.NUMERIC)) {
				rows = sheet.iterator();
			}
			
			while (rows.hasNext()) {
				Row row = rows.next();
				for (int i = 0; i < countField; i++) {
					Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);// tạo ô trông
					CellType cellType = cell.getCellType();// gán kiểu cho các ô trống
					switch (cellType) {
					case NUMERIC:
						if (DateUtil.isCellDateFormatted(cell)) {
							SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
							value += dateFormat.format(cell.getDateCellValue()) + delim;
						} else {
							value += cell.getNumericCellValue() + delim;
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
							//
							if(i<2) {
								value += (long) cell.getNumericCellValue() + delim;
							}else {
							value += " " + delim;
							}
							break;
						}
						break;
					case BLANK:
					default:
						value += " " + delim;
						break;
					}
				}
				//
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
