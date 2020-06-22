package model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Download {
	private String sid = null;
	private String url;

	public Download(String url) {
		this.url = url;
	}

	private void login(String username, String password) throws Exception {
		// url
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
			System.out.println("sid: " + sid);
			System.out.println("Đăng nhập thành công");
		} else {
			System.out.println("Đăng nhập thất bại");
		}
	}

	public LinkedList<String> listFiles(String folderPath) throws Exception {
		if (sid != null) {
			LinkedList<String> result = new LinkedList<String>();

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
					result.push((String) ((JSONObject) files.get(i)).get("path"));
				}
			} else {
				System.out.println("Truy cập sai");
			}
			System.out.println(result);
		}
		return null;
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

	public static void main(String[] args) throws Exception {
		Download dw = new Download("http://drive.ecepvn.org:5000");
		dw.login("guest_access", "123456");

//		dw.listFiles("/ECEP/song.nguyen/DW_2020/data");
		dw.download("/ECEP/song.nguyen/DW_2020/data/17130276_Sang_Nhom4.xlsx", "F:\\Downloads\\");
	}

}
