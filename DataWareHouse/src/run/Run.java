package run;

import java.sql.SQLException;
import java.util.Scanner;

import datawarehouse.DataWarehouse;
import model.Download;
import staging.LocalToStaging;

public class Run {
	public static void main(String[] args) throws Exception {

		LocalToStaging staging = null;
		Download dow = null;
		int selection =3 ;
//		try {
//			selection = Integer.parseInt(line);
//		} catch (Exception e) {
//			System.out.println("Ban nhap khong dung roi !");
//		}

		switch (selection) {
		case 1:
			System.out.println("\t\t\tDATA SINH VIEN");
			// Download
			System.out.println("\t\t\t DOWNLOAD");
			try {//
				dow = new Download(1);
				dow.download(1);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println("\t\t\t STAGING VS DATAWAREHOUSE");
			// Staging
			staging = new LocalToStaging(1);
			try {
				staging.loadData();
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		case 2:
			System.out.println("\t\t\tDATA MON HOC");
			// Download
			System.out.println("\t\t\t DOWNLOAD");
			try {
				dow = new Download(2);
				dow.download(2);
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
			System.out.println("\t\t\tDATA DANG KY");
			// Download
			System.out.println("\t\t\t DOWNLOAD");
			try {
				dow = new Download(3);
				dow.download(3);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println("\t\t\t STAGING VS DATAWAREHOUSE");
			// Staging
			staging = new LocalToStaging(3);
			try {
				staging.loadData();
				} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		case 4:
			System.out.println("\t\t\tDATA LOP HOC");
			// Download
			System.out.println("\t\t\t DOWNLOAD");
			try {
				dow = new Download(4);
				dow.download(4);
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
