package model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

	public Download(String url) {
		this.url = url;
		this.folderPath ="/ECEP/song.nguyen/DW_2020/data";
		this.noticeLogin= new StringBuffer("[THÔNG BÁO] HỆ THỐNG TRUYỀN DỮ LIỆU");
		this.noticeDownLoad = new StringBuffer("[THÔNG BÁO] HỆ THỐNG DOWNLOAD FILE");
		this.mail = new SendMailSSL();
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


	private void login(String username, String password) throws Exception {
		// url
		noticeLogin.append("\nBạn vùa mới đăng nhật vào hệ thống web :" + new Date()+" \n");
		System.out.println("-------------------Infor Connection -------------------");
		URL urlForGetRequest = new URL(url + "/webapi/auth.cgi?api=SYNO.API.Auth&version=3&method=login&account="
				+ username + "&passwd=" + password + "&session=FileStation&format=cookie");
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
			this.sid = (String) ((JSONObject) jsonObject.get("data")).get("sid");
			// print result
			System.out.println("SID: " + sid);
			System.out.println("status success: " + jsonObject.get("success"));
			
			System.out.println("Login Successfull ! \n------------------------------------------------------");
			noticeLogin.append("\nSID: " + sid);
			noticeLogin.append("\nLogin Successfull ! \n------------------------------------------------------");
		} else {
			System.out.println("Login Faild please check again ! \n---------------------------------------------------");
			noticeLogin.append("Login Faild please check again ! \n---------------------------------------------------");
		}
	}

	public LinkedList<String> listFiles(String folderPath) throws Exception {
		LinkedList<String> result = new LinkedList<String>();
		if (sid != null) {

			URL urlForGetRequest = new URL(url + "/webapi/entry.cgi?api=SYNO.FileStation.List&version=1&method=list"
					+ "&folder_path=" + folderPath + "&_sid=" + sid);
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
//			System.out.println(result);
		}
		return result;
	}

	public String download(String src, String des) throws Exception {
		if (sid != null) {
			URL urlForGetRequest = new URL(
					url + "/webapi/entry.cgi?api=SYNO.FileStation.Download&version=1&method=download&mode=open"
							+ "&path=" + src + "&_sid=" + sid);
			HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
			conection.setRequestMethod("GET");
			int responseCode = conection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream in = new BufferedInputStream((conection.getInputStream()));
				String line = null;
				String fileDes = des + src.substring(src.lastIndexOf("/") + 1);
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
				return fileDes;
			} else {
				return null;
			}
		}
		System.out.println("null");
		return null;

	}
	public void downloadAllFile(LinkedList<String> listFile,String folderPathDownload,String des) throws Exception {
		if(listFile.isEmpty()) {
			noticeDownLoad.append("\nThere is no file to download, please check again ");
			System.out.println("There is no file to download, please check again ");
		}else {
			noticeDownLoad.append("\nĐã DownLoad được các file : \n");
			for (int i = 0; i < listFile.size(); i++) {
				//Phải kiểm tra xem file đã tồn tại chưa nếu rồi thì không tải xuống nữa -- Nhớ làm sau !
				download(folderPathDownload+"/"+listFile.get(i),des);
				noticeDownLoad.append(listFile.get(i) +" \n");
				
			}
			
		}
	}

	public static void main(String[] args) throws Exception {
		String desSaveFiles = "D:\\GitHub\\DataWareHouse\\DataWareHouse\\ListFileDownload\\";
		Download dw = new Download("http://drive.ecepvn.org:5000");
		dw.login("guest_access", "123456");
		dw.getMail().sendMail("[THÔNG BÁO] ĐĂNG NHẬP VÀO WEB LẤY FILE", dw.getNotice().toString());
		
		
		
		dw.downloadAllFile(dw.listFiles(dw.getFolderPath()),dw.getFolderPath(),desSaveFiles);
		dw.getMail().sendMail("[THÔNG BÁO] HỆ THỐNG DOWNLOAD FILE", dw.getNoticeDownLoad().toString());

//		dw.listFiles("/ECEP/song.nguyen/DW_2020/data");
//		dw.download("/ECEP/song.nguyen/DW_2020/data/SinhVien.txt", "D:\\GitHub\\DataWareHouse\\DataWareHouse\\ListFileDownload\\");
	}
}
