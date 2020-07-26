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
import java.util.Date;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import FileToData.GetConnection;
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

	public Download(String url) throws SQLException {
		this.url = url;
		this.folderPath = "/ECEP/song.nguyen/DW_2020/data";
		this.noticeLogin = new StringBuffer("[THÔNG BÁO] HỆ THỐNG TRUYỀN DỮ LIỆU");
		this.id_config = 0;
		this.noticeDownLoad = new StringBuffer("[THÔNG BÁO] HỆ THỐNG DOWNLOAD FILE");
		this.mail = new SendMailSSL();
		loadProps();
	}

	private void loadProps() throws SQLException {
		// assign db parameters
//		this.login_endpoint = "/webapi/auth.cgi?api=SYNO.API.Auth&version=3&method=login&session=FileStation&format=cookies";

		Connection connectionDB1 = DBConnections.getConnection(url_mysql, userName_mysql, passWord_mysql);
		System.out.println("ok");
		ResultSet rs;
		Statement stmt = connectionDB1.createStatement();
		rs = stmt.executeQuery("SELECT * FROM config");

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

	private void login() throws Exception {
		// url
		noticeLogin.append("\nBạn vùa mới đăng nhật vào hệ thống web :" + new Date() + " \n");
		System.out.println("-------------------Infor Connection -------------------");
//		login_endpoint += "&account=" + username + "&passwd=" + password;
//		URL urlForGetRequest = new URL(url + login_endpoint);

		URL urlForGetRequest = new URL(url + "/webapi/auth.cgi?api=SYNO.API.Auth&version=3&method=login&account="
				+ username + "&passwd=" + password + "&session=FileStation&format=cookie");
		HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
		conection.setRequestMethod("GET");
		int responseCode = conection.getResponseCode();
		System.out.println(responseCode);
		System.out.println(HttpURLConnection.HTTP_OK);
		if (responseCode == HttpURLConnection.HTTP_OK) {

			BufferedReader in = new BufferedReader(new InputStreamReader(conection.getInputStream()));

			// Có thể thay đổi dữ liệu
			StringBuffer response = new StringBuffer();
			String line = null;
			while ((line = in.readLine()) != null) {
				response.append(line);
			}
			in.close();
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(response.toString());
			this.sid = (String) ((JSONObject) jsonObject.get("data")).get("sid");
			// print result
			System.out.println("SID: " + sid);
			System.out.println("status success: " + jsonObject.get("success"));

			System.out.println("Login Successfull ! \n------------------------------------------------------");
			noticeLogin.append("\nSID: " + sid);
			noticeLogin.append("\nLogin Successfull ! \n------------------------------------------------------");
		} else {
			System.out
					.println("Login Faild please check again ! \n---------------------------------------------------");
			noticeLogin
					.append("Login Faild please check again ! \n---------------------------------------------------");
		}
	}

	public LinkedList<String> listFiles() throws Exception {
		LinkedList<String> result = new LinkedList<String>();
		if (sid != null) {

			URL urlForGetRequest = new URL(url + "/webapi/entry.cgi?api=SYNO.FileStation.List&version=1&method=list"
					+ "&folder_path=" + listFile + "&_sid=" + sid);
			HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
			conection.setRequestMethod("GET");
			int responseCode = conection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(conection.getInputStream()));

				// Có thể thay đổi dữ liệu
				StringBuffer response = new StringBuffer();
				String line = null;
				while ((line = in.readLine()) != null) {
					response.append(line);
				}
				in.close();

				JSONParser parser = new JSONParser();
				JSONObject jsonObject = (JSONObject) parser.parse(response.toString());

				JSONArray files = (JSONArray) ((JSONObject) jsonObject.get("data")).get("files");
				for (int i = 0; i < files.size(); i++) {
					result.push((String) ((JSONObject) files.get(i)).get("name"));
				}
			} else {
				System.out.println("Không thể truy cập được vào hệ thống");

			}
			System.out.println(result);
		}
		return result;
	}

	public String downloadFile() throws Exception {
		if (sid != null) {
			String src = listFile + "/" + fileName;
			URL urlForGetRequest = new URL(
					url + "/webapi/entry.cgi?api=SYNO.FileStation.Download&version=1&method=download&mode=open"
							+ "&path=" + src + "&_sid=" + sid);
			HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
			conection.setRequestMethod("GET");
			int responseCode = conection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream in = new BufferedInputStream((conection.getInputStream()));
				String line = null;
				String fileDes = folder_download + src.substring(src.lastIndexOf("/") + 1);
				System.out.println(fileDes);
//				PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileDes)));
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileDes));
				int numReadedBytes;
				byte[] buff = new byte[1024];
				while ((numReadedBytes = in.read(buff)) > -1) {
					out.write(buff, 0, numReadedBytes);
				}
				in.close();
				out.close();
				System.out.println("down file thanh cong");
				Connection connectionDB1 = DBConnections.getConnection(url_mysql, userName_mysql, passWord_mysql);
				System.out.println("");

				String query = "INSERT INTO logs(time_download, status, id_config) VALUES(?,?,?)";
				PreparedStatement pre = connectionDB1.prepareStatement(query);

				pre.setDate(1, date);
				pre.setString(2, "OK");
				pre.setInt(3, id_config);
				pre.execute();
				System.out.println("OKE");
				return fileDes;
			} else {
				return null;
			}
		}
		System.out.println("null");
		return null;
	}

	public void download() throws Exception {
		LinkedList<String> lisFile = listFiles();
		for (int i = 0; i < lisFile.size(); i++) {
			String srcNameFile = lisFile.get(i);
			String nameFile = srcNameFile.substring(srcNameFile.lastIndexOf("/") + 1);
			if (nameFile.contains(kieuFile)) {
				System.out.println(nameFile);
//				System.out.println(kieuFile);
				URL urlForGetRequest = new URL(
						url + "webapi/entry.cgi?api=SYNO.FileStation.Download&version=1&method=download&mode=open"
								+ "&path=" + listFile + "/" + srcNameFile + "&_sid=" + sid);
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
						out.write(buff, 0, readData);
					}
					in.close();
					out.close();
				}
				System.out.println("down file thanh cong");
//				Connection connectionDB1 = DBConnections.getConnection(url_mysql, userName_mysql, passWord_mysql);
//				System.out.println("");
//
//				String query = "INSERT INTO logs(time_download, status,id_config) VALUES(?,?,?)";
//				PreparedStatement pre = connectionDB1.prepareStatement(query);
//
//				pre.setDate(1, date);
//				pre.setString(2, "OK");
//				pre.setInt(id_config, 3);
//				pre.execute();
//				System.out.println("OKE");
			}
		}
	}

	public void downloadAllFile(LinkedList<String> listFile, String folderPathDownload) throws Exception {

		if (listFile.isEmpty()) {
			noticeDownLoad.append("\nThere is no file to download, please check again ");
			System.out.println("There is no file to download, please check again ");
		} else {
			noticeDownLoad.append("\nĐã DownLoad được các file : \n");
			for (int i = 0; i < listFile.size(); i++) {
				// Phải kiểm tra xem file đã tồn tại chưa nếu rồi thì không tải xuống nữa -- Nhớ
				// làm sau !
//				downloadFile(folderPathDownload + "/" + listFile.get(i));
//				noticeDownLoad.append(listFile.get(i) + " \n");

			}
		}
	}

	public static void insertLog(MyConfig myConfig, String status) {
		PreparedStatement statement = null;
		int id_log = myConfig.getId_log();

		String sql = "UPDATE  logs SET time_download= current_timestamp(),status ='" + status + "' WHERE id = "
				+ id_log;
		Connection connection = GetConnection.getConnection("control");
		try {
			statement = connection.prepareStatement(sql);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {

				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}

			} catch (SQLException e) {
				System.out.println("Khong the tao bang");
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Download dw = new Download("http://drive.ecepvn.org:5000/");
//		Download dw = new Download("/ECEP/song.nguyen/DW_2020/");
		dw.login();
		dw.download();
	}

	public void run() {
		try {
			login();
//			getMail().sendMail("[THÔNG BÁO] ĐĂNG NHẬP VÀO WEB LẤY FILE", noticeLogin.toString());

			downloadFile();
//			getMail().sendMail("[THÔNG BÁO] HỆ THỐNG DOWNLOAD FILE", noticeDownLoad.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
