package run;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import connections.GetConnection;
import datawarehouse.DataWarehouse;
import model.Download;
import staging.LocalToStaging;

public class MainClass extends Thread {
	public static void main(String[] args) throws Exception {
		LocalToStaging staging = null;
		DataWarehouse dataWareHouse = null;
		Download dow = null;

		int selection = Integer.parseInt(args[0]);
		int process = 0;
		System.out.println(selection);
		try {
			selection = Integer.parseInt(args[0]);
		} catch (Exception e) {
			System.out.println("Ban nhap khong dung roi !");

		}

		switch (selection) {
		case 1:
			// Download
			System.out.println("\t\t\t DOWNLOAD");
			try {//
				dow = new Download(1);
				dow.login();
				dow.download();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
<<<<<<< .mine
			System.out.println("\t\t\t STAGING VS DATAWAREHOUSE");
			// Staging
			staging = new LocalToStaging(1);
			try {
				staging.loadData();
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}















=======
			while(true) {
				
				switch (process) {
				case 1:
					break;
				case 2:
					//Staging
					 staging = new LocalToStaging(selection);
					
					try {
							staging.loadDatat();
					} catch (ClassNotFoundException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
					break;
				case 3:
					//Dowload
					//Staging
					 dataWareHouse = new DataWarehouse(selection);
					 dataWareHouse.start();
						
					break;
>>>>>>> .theirs

			break;
		case 2:
			// Download
			System.out.println("\t\t\t DOWNLOAD");
			try {
				dow = new Download(2);
				dow.login();
				dow.download();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println("\t\t\t STAGING VS DATAWAREHOUSE");
			// Staging
			staging = new LocalToStaging(2);
			try {
				staging.loadData();
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		case 3:
			// Download
			System.out.println("\t\t\t DOWNLOAD");
			try {
				dow = new Download(3);
				dow.login();
				dow.download();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println("\t\t\t STAGING VS DATAWAREHOUSE");
			// Staging
			staging = new LocalToStaging(3);
			dow.login();
			try {
				staging.loadData();
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		case 4:
			// Download
			System.out.println("\t\t\t DOWNLOAD");
			try {
				dow = new Download(4);
				dow.login();
				dow.download();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println("\t\t\t STAGING VS DATAWAREHOUSE");
			// Staging
			staging = new LocalToStaging(4);
			try {
				staging.loadData();
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		default:
			break;
		}
	}
}
