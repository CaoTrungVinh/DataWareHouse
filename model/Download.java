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
	private String url, login_endpoint, listfile_endpoint, download_endpoint;

	public Download(String url) {
		this.url = url;
	}

	private void login(String username, String password) throws Exception {
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
		} else {
			System.out.println("GET NOT WORKED");
		}
	}

	public static void main(String[] args) throws Exception {
		Download test = new Download("http://drive.ecepvn.org:5000");
		test.login("guest_access", "123456");
	}

}
