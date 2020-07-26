package run;

import java.sql.SQLException;
import java.util.Scanner;

import datawarehouse.DataWarehouse;
import model.Download;
import staging.LocalToStaging;

public class Run {
	public static void main(String[] args) {
	
		System.out.println("SELECTION: 1 - sinh vien, 2 - mon hoc,3 - dang ky, 4- lop hoc\nNhap config de RUN: ");
		Scanner sc = new Scanner(System.in);
		LocalToStaging staging = null;
		String line = sc.nextLine();
		int selection = 0;
		try {
			selection = Integer.parseInt(line);
		} catch (Exception e) {
			System.out.println("Ban nhap khong dung roi !");
		}
		
		
		
		switch (selection) {
		case  1:
			//Download 
//			System.out.println("\t\t\t DOWNLOAD");
//			try {
//				Download dow = new Download("http://drive.ecepvn.org:5000/");
//				dow.run();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}	
			System.out.println("\t\t\t STAGING VS DATAWAREHOUSE");
			//Staging
			 staging = new LocalToStaging(1);
			try {
				staging.run();
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			
			
			break;
		case 2:
			//Download 
//			System.out.println("\t\t\t DOWNLOAD");
//			try {
//				Download dow = new Download("http://drive.ecepvn.org:5000/");
//				dow.run();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}	
			System.out.println("\t\t\t STAGING VS DATAWAREHOUSE");
			//Staging
			 staging = new LocalToStaging(2);
			try {
				staging.run();
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
		case 3:
			//Download 
//			System.out.println("\t\t\t DOWNLOAD");
//			try {
//				Download dow = new Download("http://drive.ecepvn.org:5000/");
//				dow.run();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}	
			System.out.println("\t\t\t STAGING VS DATAWAREHOUSE");
			//Staging
			 staging = new LocalToStaging(3);
			try {
				staging.run();
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
		case 4:
			//Download 
//			System.out.println("\t\t\t DOWNLOAD");
//			try {
//				Download dow = new Download("http://drive.ecepvn.org:5000/");
//				dow.run();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}	
			System.out.println("\t\t\t STAGING VS DATAWAREHOUSE");
			//Staging
			 staging = new LocalToStaging(4);
			try {
				staging.run();
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
