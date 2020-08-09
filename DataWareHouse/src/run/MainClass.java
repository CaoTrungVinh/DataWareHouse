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
	public static void main(String[] args) {
			LocalToStaging staging = null;
			DataWarehouse dataWareHouse = null;
			Download dow =null;
			
			int selection = 0;
			int process  = 0;
			System.out.println(selection +"---------"+process);
			try {
				selection = Integer.parseInt(args[0]);
				process = Integer.parseInt(args[1]);
			} catch (Exception e) {
				System.out.println("Ban nhap khong dung roi !");
				
			}
			while(true) {
				
				switch (process) {
				case 1:
					break;
				case 2:
					//Staging
					 staging = new LocalToStaging(selection);
					
					try {
							staging.run();
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

				default:
					break;
				}
			}

			
		
//		
//		switch (selection) {
//		case  1:
//			//Download 
//			System.out.println("\t\t\t DOWNLOAD");
////			try {
////				dow = new Download("http://drive.ecepvn.org:5000/",1);
////				dow.run();
////			} catch (SQLException e) {
////				e.printStackTrace();
////			}	
//			System.out.println("\t\t\t STAGING VS DATAWAREHOUSE");
//			//Staging
//			 staging = new LocalToStaging(1);
//			 dataWareHouse = new DataWarehouse(1);
//			try {
//				if(stagingReady) {
//					staging.run();
//					stagingReady = false;
//					datawarehouseReady = true;
//				}
//				if(datawarehouseReady) {
//					dataWareHouse.run();
//				}
//				
//				
//			} catch (ClassNotFoundException | SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		
//			
//			
//			break;
//		case 2:
//			//Download 
//			System.out.println("\t\t\t DOWNLOAD");
//			try {
//				dow = new Download("http://drive.ecepvn.org:5000/",2);
//				dow.run();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}	
//			System.out.println("\t\t\t STAGING VS DATAWAREHOUSE");
//			//Staging
//			 staging = new LocalToStaging(2);
//			try {
//				staging.run();
//			} catch (ClassNotFoundException | SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			break;
//		case 3:
//			//Download 
//			System.out.println("\t\t\t DOWNLOAD");
//			try {
//				dow = new Download("http://drive.ecepvn.org:5000/",3);
//				dow.run();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}	
//			System.out.println("\t\t\t STAGING VS DATAWAREHOUSE");
//			//Staging
//			 staging = new LocalToStaging(3);
//			try {
//				staging.run();
//			} catch (ClassNotFoundException | SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			break;
//
//		default:
//			break;
//		}
	}
	private static int checkDataInsert(int id_config) {
		Connection log = GetConnection.getConnection("control");
		PreparedStatement statement = null;
		ResultSet resutlSet = null;
		int count =0;
		try {
			statement = log.prepareStatement("SELECT COUNT(*) FROM logs WHERE status ='ER' OR status ='TR' AND id_config="+id_config);
			resutlSet = statement.executeQuery();
			while(resutlSet.next()) {
				count = resutlSet.getInt(1);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return count;
	}


}
