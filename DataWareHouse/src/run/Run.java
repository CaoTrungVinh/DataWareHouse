package run;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

import model.Download;
import nghia.stagingtowarehouse.DataWarehouse;
import nghia.stagingtowarehouse.SimulatorStaging;

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
			System.out.println("\t\t\t STAGING");
			//Staging
			SimulatorStaging staging = new SimulatorStaging();
			try {
				staging.run();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("\t\t\t DATAWAREHOUSE");
			//DatAwarehouse
			DataWarehouse dataWareHouse = new DataWarehouse(selection);
			dataWareHouse.run();
		
			
			
			break;
		case 2:
			System.out.println("chua co ");
			break;

		default:
			break;
		}
	}

}
