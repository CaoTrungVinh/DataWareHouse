package run;

import java.sql.SQLException;
import java.util.Scanner;

import datawarehouse.DataWarehouse;
import model.Download;
import staging.LocalToStaging;

public class Run {
	public static void main(String[] args) {
	
		System.out.println("SELECTION: 1 - sinh vien, 2 - mon hoc\nNhap config de RUN: ");
		Scanner sc = new Scanner(System.in);
		
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
			System.out.println("\t\t\t DOWNLOAD");
			try {
				Download dow = new Download("http://drive.ecepvn.org:5000/");
				dow.run();
			} catch (SQLException e) {
				e.printStackTrace();
			}	
			System.out.println("\t\t\t STAGING VS DATAWAREHOUSE");
			//Staging
			LocalToStaging staging = new LocalToStaging(1);
		
			
			
			break;
		case 2:
			System.out.println("chua co ");
			break;

		default:
			break;
		}
	}

}
