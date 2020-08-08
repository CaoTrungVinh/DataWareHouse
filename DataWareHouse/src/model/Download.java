package model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import mail.SendMailSSL;

public class Download {
	private String sid = null;
	private String url;
	private String folderPath;
	private StringBuffer noticeLogin;
	private StringBuffer noticeDownLoad;
	private SendMailSSL mail;

	String login_endpoint;
	String username;
	String password;
	static String listFile;
	static String folder_download;
	static String kieuFile;
	static String fileName;
	int id_config;

	static String url_mysql = "jdbc:mysql://localhost/control?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&characterEncoding=UTF-8";
	static String userName_mysql = "root";
	static String passWord_mysql = "";

	long millis = System.currentTimeMillis();
	java.sql.Date date = new java.sql.Date(millis);

//	public Download(String url,int i) throws SQLException {
	public Download(int i) throws SQLException {
		this.url = url;
//		this.folderPath = "/ECEP/song.nguyen/DW_2020/data";
		this.noticeLogin = new StringBuffer("[THÔNG BÁO] HỆ THỐNG TRUYỀN DỮ LIỆU");
		this.id_config = 0;
		this.noticeDownLoad = new StringBuffer("[THÔNG BÁO] HỆ THỐNG DOWNLOAD FILE");
		this.mail = new SendMailSSL();
		loadConfig(i);
	}

	// 1. kết nối với config
	private void loadConfig(int i) throws SQLException {
		Connection connectionDB1 = DBConnections.getConnection(url_mysql, userName_mysql, passWord_mysql);
		System.out.println("ok");
		ResultSet rs;
		Statement stmt = connectionDB1.createStatement();
		//Kết nối theo dòng id = i
		rs = stmt.executeQuery("SELECT * FROM config where id=" + i);
		System.out.println(username + " " + password);

		while (rs.next()) {
			this.id_config = rs.getInt("id");
			url = rs.getString("source_host");
			username = rs.getString("user_name");
			password = rs.getString("password");
			listFile = rs.getString("list_file");
			folder_download = rs.getString("folder_download");
			kieuFile = rs.getString("extension_file");
			fileName = rs.getString("file_name");
		}
		System.out.println(url + " " + username + " " + password + " " + listFile + " " + folder_download + " "
				+ kieuFile + " " + fileName);
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public StringBuffer getNotice() {
		return noticeLogin;
	}

	public void setNotice(StringBuffer notice) {
		this.noticeLogin = notice;
	}

	public StringBuffer getNoticeDownLoad() {
		return noticeDownLoad;
	}

	public void setNoticeDownLoad(StringBuffer noticeDownLoad) {
		this.noticeDownLoad = noticeDownLoad;
	}

	public SendMailSSL getMail() {
		return mail;
	}

	// 2. Sử dụng SYNO.API.Auth kết nối Server
	private void login() throws Exception {
		noticeLogin.append("\nBạn vùa mới đăng nhập vào hệ thống web :" + new Date() + " \n");
		System.out.println("-------------------Infor Connection -------------------");

		URL urlAPI = new URL(url + "/webapi/auth.cgi?api=SYNO.API.Auth&version=3&method=login&account="
				+ username + "&passwd=" + password + "&session=FileStation&format=cookie");
		//Kết nối vào web qua url
		HttpURLConnection conection = (HttpURLConnection) urlAPI.openConnection();
		conection.setRequestMethod("GET");//get lấy thông tin connection
		int responseCode = conection.getResponseCode();
		System.out.println(responseCode);
		System.out.println(HttpURLConnection.HTTP_OK);
		if (responseCode == HttpURLConnection.HTTP_OK) {//kết nối thành công 
			//Đọc connec
			BufferedReader in = new BufferedReader(new InputStreamReader(conection.getInputStream()));
			StringBuffer response = new StringBuffer();
			String line = null;
			while ((line = in.readLine()) != null) {
				response.append(line);
			}
			in.close();//Buffêred đọc connec thành công đóng
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(response.toString());
			this.sid = (String) ((JSONObject) jsonObject.get("data")).get("sid");//trả về 1 cookie nhận biết đăng nhập thành công
			// print result
			System.out.println("SID: " + sid);
			System.out.println("status success: " + jsonObject.get("success"));

			System.out.println("Login Successfull ! \n------------------------------------------------------");
			noticeLogin.append("\nSID: " + sid);
			noticeLogin.append("\nLogin Successfull ! \n------------------------------------------------------");
		} else {
			System.out.println(
					"Đăng nhập thất bại vui lòng kiểm tra lại ! \n---------------------------------------------------");
			noticeLogin.append(
					"Đăng nhập thất bại vui lòng kiểm tra lại ! \n---------------------------------------------------");
		} // 3. Thông báo ra màn hình đăng nhập thất bại vui lòng kiểm tra lại
	}
	
	// 4. Lấy danh sách các file trên NAS 
	public LinkedList<String> listFiles() throws Exception {
		LinkedList<String> result = new LinkedList<String>();
		if (sid != null) {
			URL urlForGetRequest = new URL(url + "/webapi/entry.cgi?api=SYNO.FileStation.List&version=1&method=list"
					+ "&folder_path=" + listFile + "&_sid=" + sid);
			//Kết nối vào web qua url lấy danh sách file
			HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
			conection.setRequestMethod("GET");//Lấy thông tin trang web để connection
			int responseCode = conection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(conection.getInputStream()));
					
				StringBuffer response = new StringBuffer();
				String line = null;
				while ((line = in.readLine()) != null) {
					response.append(line);
				}
				in.close();//Đọc thông tin dữ liệu trên fiêl thành công 

				JSONParser parser = new JSONParser();
				JSONObject jsonObject = (JSONObject) parser.parse(response.toString());

				JSONArray files = (JSONArray) ((JSONObject) jsonObject.get("data")).get("files");
				//Trả về tất cả các data có trong NAS
				for (int i = 0; i < files.size(); i++) {
					result.push((String) ((JSONObject) files.get(i)).get("name"));
				}
			} else {
				//5. Thông báo không thể truy cập thể lấy danh sách các file
				System.out.println("Không thể truy cập được vào hệ thống");

			}
			System.out.println(result);
		}
		return result;
	}

	public void download(int idConfig) throws Exception {
		LinkedList<String> lisFile = listFiles();
		for (int i = 0; i < lisFile.size(); i++) {
			String srcNameFile = lisFile.get(i);
			String nameFile = srcNameFile.substring(srcNameFile.lastIndexOf("/") + 1);
			//6. Kiểu tra những trong list File trùng với định dạng file trong config như loại file (sinh viên, môn học, đăng ký) và đuôi file 
			if (nameFile.contains(fileName) && nameFile.contains(kieuFile)) {
				System.out.println(nameFile);
				URL urlForGetRequest = new URL(
						url + "webapi/entry.cgi?api=SYNO.FileStation.Download&version=1&method=download&mode=open"
								+ "&path=" + listFile + "/" + srcNameFile + "&_sid=" + sid);
				//8. Kết nối vào urlAPI để download các file về local
				HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
				conection.setRequestMethod("GET");
				int responseCode = conection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					InputStream in = new BufferedInputStream((conection.getInputStream()));
					BufferedOutputStream out = new BufferedOutputStream(
							new FileOutputStream(folder_download + nameFile));
					int readData;
					byte[] buff = new byte[1024];
					while ((readData = in.read(buff)) > -1) {
						//9. các file download về local BufferedOutputStream out
						out.write(buff, 0, readData);
					}
					in.close();
					out.close();
				}
				System.out.println("down file thanh cong");
				// 10. Cập nhập logs
				insertLog(nameFile, idConfig);
				noticeDownLoad.append(
						"Dowload thành công " + nameFile + " ! \n---------------------------------------------------");
				//11. Thông báo về email
				getMail().sendMail("[THÔNG BÁO] HỆ THỐNG DOWNLOAD FILE", noticeDownLoad.toString());
			}
		}
		// 7. Thông báo download thất bại
		noticeDownLoad.append("Dowload thất bại ! \n---------------------------------------------------");
		getMail().sendMail("[THÔNG BÁO] HỆ THỐNG DOWNLOAD FILE", noticeDownLoad.toString());
	}

	public static void insertLog(String nameFile, int i) throws SQLException {
		Connection connectionDB1 = DBConnections.getConnection(url_mysql, userName_mysql, passWord_mysql);
		System.out.println("");

		String query = "INSERT INTO logs(file_name, status, time_download, id_config) VALUES(?,?,?,?)";
		PreparedStatement pre = connectionDB1.prepareStatement(query);

		pre.setString(1, nameFile);
		pre.setString(2, "ER");
		pre.setString(3, new Timestamp(System.currentTimeMillis()).toString().substring(0, 19));
		pre.setInt(4, i);

		pre.setString(1, nameFile);
		pre.execute();
		System.out.println("OKE");

	}

//	public static void main(String[] args) throws Exception {
//		Download dw = new Download(1);
//	}

//	public void run() {
//		try {
//			login();
//			getMail().sendMail("[THÔNG BÁO] ĐĂNG NHẬP VÀO WEB LẤY FILE", noticeLogin.toString());
//
//			download();
//			getMail().sendMail("[THÔNG BÁO] HỆ THỐNG DOWNLOAD FILE", noticeDownLoad.toString());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

}
