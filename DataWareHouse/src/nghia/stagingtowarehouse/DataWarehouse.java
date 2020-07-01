package nghia.stagingtowarehouse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.jdbc.result.ResultSetMetaData;

import FileToData.GetConnection;
import mail.SendMailSSL;
import model.MyConfig;

public class DataWarehouse {
	SendMailSSL sendMail = null;

	public DataWarehouse() {
		sendMail = new SendMailSSL();
	}

	// HÀM RUN
	@SuppressWarnings("null")
	public void run() {
		// 1. MỞ KẾT NỐI DATABASE Control_Data
		Connection connect_control = GetConnection.getConnection("control");

		// NẾU KẾT NỐI KHÔNG THÀNH CÔNG --> 2. ĐÓNG KẾT NỐI
		if (connect_control == null) {
			try {
				connect_control.close();

				// 3. GỬI MAIL THÔNG BÁO LỖI
				sendMail.sendMail("[ERROR] TRANSFORM DATAWAREHOUSE", "Không thể kết nối tới database Control_data");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			// NẾU KẾT NỐI THÀNH CÔNG --> 2.LẤY DỮ LIỆU CÓ STATUS ='TR'--> 3.TRẢ VỀ MỘT
			// RESULTSET --> 4.1 LƯU LIỆU TRONG LIST VÀ ĐÓNG KẾT NỐI
			List<MyConfig> listConfig = getValuesFromConfig(connect_control);

			for (MyConfig myConfig : listConfig) {

				// 5. MỞ KẾT NỐI DATABASE Staging
				Connection connect_warehouse = GetConnection.getConnection("datawarehouse");
				Connection connect_staging = GetConnection.getConnection("staging");

				// NẾU KẾT NỐI KHÔNG THÀNH CÔNG --> 6. ĐÓNG KẾT NỐI
				if (connect_staging == null || connect_warehouse == null) {
					try {
						connect_staging.close();
						connect_warehouse.close();

						// 7. GỬI MAIL THÔNG BÁO LỖI
						sendMail.sendMail("[ERROR] TRANSFORM DATAWAREHOUSE",
								"Không thể kết nối tới database Staging hoặc datawarehouse");
					} catch (SQLException e) {
						e.printStackTrace();
					}

				} else {
					// KIỂM TRA ĐÃ TỒN TẠI TABLE CHƯA?
					// NHÁNH 1: CHƯA TỒN TẠI DỮ LIỆU
					if (checkTableExist(connect_warehouse, myConfig.getDatawarehouse_table(), "datawarehouse") == 0) {
						// 7 TẠO BẢNG
						if (createTable(myConfig, connect_warehouse)) {
							// 8. LẤY DỮ LIỆU ---> 9. INSERT DỮ LIỆU TỪ STAGING QUA DATAWAREHOUSE
							insertDatawarehouse(connect_staging, connect_warehouse, myConfig);

						}

					}
					// NHÁNH 2: ĐÃ TỒN TẠI DỮ LIỆU
					else {
						// 8. KIỂM TRA TỪNG PK TRONG TABLE CÓ TỒN TẠI KHÔNG --> CHƯA TỒN TẠI THÌ THÊM ROW MỚI VÀO
						compareData(connect_staging, connect_warehouse, myConfig);

					}
				}

				// *7

			}
		}

	}

	private ArrayList<MyConfig> getValuesFromConfig(Connection connection) {
		ArrayList<MyConfig> listConfig = new ArrayList<MyConfig>();
		String sql = "SELECT log.id_config, conf.staging_table, conf.variabless,conf.number_cols,conf.datawarehouse_table\r\n"
				+ "FROM config conf join logs log on conf.id = log.id_config\r\n" + "WHERE log.status = 'TR';";
		MyConfig myConfig = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			statement = connection.prepareStatement(sql);
			// 3. TRẢ VỀ MỘT resultSet
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				myConfig = new MyConfig();
				myConfig.setId(resultSet.getInt("id_config"));
				myConfig.setStaging_table(resultSet.getString("staging_table"));
				myConfig.setVariabless(resultSet.getString("variabless"));
				myConfig.setNumber_cols(resultSet.getString("number_cols"));
				myConfig.setDatawarehouse_table(resultSet.getString("datawarehouse_table"));

				// 4.1 LƯU DỮ LIỆU VÀO MỘT CÁI LIST
				listConfig.add(myConfig);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 4.2 ĐÓNG KẾT NỐI DATABASE control_data
			try {

				if (statement != null) {
					statement.close();
				}
				if (resultSet != null) {
					resultSet.close();
				}
				if (connection != null) {
					connection.close();
				}

			} catch (SQLException e) {
				System.out.println("Khong the tao bang");
				e.printStackTrace();
			}
		}

		return listConfig;
	}

	private void compareData(Connection connect_staging, Connection connect_warehouse, MyConfig config) {
		int cols = Integer.parseInt(config.getNumber_cols());
		String fields[] = config.getVariabless().split(",");

		String primaryKeySta = "";
		
		int rows_update_datawarehouse =0;

		String sql = "SELECT * FROM " + config.getStaging_table();
		PreparedStatement statement = null;
		ResultSet result = null;
		PreparedStatement statementWareHouse = null;
		ResultSet resultWareHouse = null;

		try {
			
			statement = connect_staging.prepareStatement(sql);
			result = statement.executeQuery();
			while (result.next()) {
				String sqlCheckExist = "SELECT COUNT(*)\r\n FROM " + config.getDatawarehouse_table() + "\r\t WHERE "
						+ fields[0] + "='";
				ResultSetMetaData rsMeta = (ResultSetMetaData) result.getMetaData();
				primaryKeySta = result.getString(1);

				// LẤY KHÓA CHÍNH CỦA TỪNG ROW RA SO SÁNH
				sqlCheckExist += primaryKeySta + "';";

				statementWareHouse = connect_warehouse.prepareStatement(sqlCheckExist);
				resultWareHouse = statementWareHouse.executeQuery();

				while (resultWareHouse.next()) {
					//8. KIỂM TRA TỪNG PK 
					//RESULT TRẢ VỀ 1 NẾU TỒN TẠI NGƯỢC LẠI TRẢ VỀ 0
					if (resultWareHouse.getInt(1) == 1) {

					} else {
						System.out.println(rsMeta.getColumnName(1) + ": " + primaryKeySta + " chưa tồn tại trong table "
								+ config.getDatawarehouse_table());
						String sqlInsert = "INSERT INTO " + config.getDatawarehouse_table() + " VALUES(";
						for (int i = 1; i <= cols; i++) {
							if (i == cols) {
								sqlInsert += "'" + result.getString(i) + "');";
							} else {
								sqlInsert += "'" + result.getString(i) + "',";
							}

						}
						statementWareHouse = connect_warehouse.prepareStatement(sqlInsert);
//						 INSERT DỮ LIỆU TỪ STAGING QUA DATAWAREHOUSE
						statementWareHouse.executeUpdate();
						rows_update_datawarehouse++;
						System.out.println("Successfully insert the row(s)");
					}
				}

			}
			if(rows_update_datawarehouse>0) {
				System.out.println(config.getDatawarehouse_table()+" đã cập nhật thêm "+ rows_update_datawarehouse+" dòng" +"\n\n");
				//10.GỬI MAIL THÔNG BÁO THÀNH CÔNG
				sendMail.sendMail("[SUCCESS] TRANSFORM DATAWAREHOUSE", config.getDatawarehouse_table()+" đã cập nhật thêm "+ rows_update_datawarehouse+" dòng");
				//11.GHI VÀO LOGS
				insertLog(config, "SUCCESS",rows_update_datawarehouse);
				
			}else {
				System.out.println("table "+config.getDatawarehouse_table()+" không có thay đổi"+"\n\n");
				//10.GỬI MAIL THÔNG BÁO THÀNH CÔNG
				sendMail.sendMail("[SUCCESS] TRANSFORM DATAWAREHOUSE", config.getDatawarehouse_table()+" KHÔNG CÓ THAY ĐỔI "+"\n\n");
				//11.GHI VÀO LOGS
				insertLog(config, "SUCCESS",rows_update_datawarehouse);
			}
			

		} catch (SQLException e) {
			//THÔNG BÁO LỖI
			sendMail.sendMail("[ERROR] TRANSFORM DATAWAREHOUSE", config.getDatawarehouse_table()+" KHÔNG THÊ INSERT DỮ LIỆU");
			//GHI LOGS LỖI
			insertLog(config, "SUCCESS",rows_update_datawarehouse);
			e.printStackTrace();
		} finally {
			// 4.2 ĐÓNG KẾT NỐI DATABASE control_data
			try {

				if (statement != null) {
					statement.close();
				}
				if (result != null) {
					result.close();
				}
				if (statementWareHouse != null) {
					statementWareHouse.close();
				}
				if (resultWareHouse != null) {
					resultWareHouse.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void insertDatawarehouse(Connection connec_Stag, Connection connect_warehouse, MyConfig config) {
		int cols = Integer.parseInt(config.getNumber_cols());

		String sql = "SELECT * FROM " + config.getStaging_table();
		PreparedStatement statement = null;
		ResultSet result = null;
		PreparedStatement statementWareHouse = null;
		ResultSet resultWareHouse = null;

		try {
			// 8. LẤY DỮ LIỆU TRONG TABLE CỦA DATABASE Staging
			statement = connec_Stag.prepareStatement(sql);
			result = statement.executeQuery();
			while (result.next()) {
				String sqlInsert = "INSERT INTO " + config.getDatawarehouse_table() + " VALUES(";
				for (int i = 1; i <= cols; i++) {
					if (i == cols) {
						sqlInsert += "'" + result.getString(i) + "');";
					} else {
						sqlInsert += "'" + result.getString(i) + "',";
					}

				}
				statementWareHouse = connect_warehouse.prepareStatement(sqlInsert);
//				9. INSERT DỮ LIỆU TỪ STAGING QUA DATAWAREHOUSE
				statementWareHouse.executeUpdate();
				System.out.println("Successfully insert the row(s)");

			}
			System.out.println("Transform data thành công từ Staging table." + config.getStaging_table()+" sang datawarehouse."+ config.getDatawarehouse_table()+"\n\n");
			// 10. GỬI MAIL THÔNG BÁO INSERT THÀNH CÔNG
			sendMail.sendMail("[SUCCESS] TRANSFORM DATAWAREHOUSE", "Transform data thành công từ Staging table." + config.getStaging_table()+" sang datawarehouse."+ config.getDatawarehouse_table()+"\n\n");
			// 11. CẬP NHẬT STATUS='SUCCESS'
			insertLog(config, "SUCCESS",0);

		} catch (SQLException e) {
			
			//THÔNG BÁO LỖI
			sendMail.sendMail("[ERROR] TRANSFORM DATAWAREHOUSE", config.getDatawarehouse_table()+" KHÔNG THÊ INSERT DỮ LIỆU");
			//GHI LOGS LỖI
			insertLog(config, "ERROR_TRANSFORM",0);
			e.printStackTrace();
		} finally {
			// 4.2 ĐÓNG KẾT NỐI DATABASE control_data
			try {

				if (statement != null) {
					statement.close();
				}
				if (result != null) {
					result.close();
				}
				if (statementWareHouse != null) {
					statementWareHouse.close();
				}
				if (resultWareHouse != null) {
					resultWareHouse.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	private int checkTableExist(Connection connection, String table_name, String db_name) {
		String sql = "SELECT COUNT(*)\r\n" + "FROM information_schema.tables \r\n" + "WHERE table_schema = '" + db_name
				+ "' \r\n" + "AND table_name = '" + table_name + "';";
		PreparedStatement statement = null;
		ResultSet res = null;
		try {
			statement = connection.prepareStatement(sql);
			res = statement.executeQuery();
			while (res.next()) {
				return res.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (res != null) {
					res.close();
				}
				if (statement != null) {
					statement.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	public static boolean createTable(MyConfig config, Connection connectionStaging) {
		boolean isCreated = false;
		PreparedStatement statement = null;
		ResultSet res = null;
		// Tạo ra một mảng từng field trong table
		String[] fields = config.getVariabless().split(",");

		// Khỏi tạo câu lệnh sql create table
		String sql = "Create table " + config.getDatawarehouse_table() + " (";

		// Chạy vòng lặp để hoàn thiện câu lệnh sql
		for (int i = 0; i < fields.length; i++) {
			if (i == 0) {
				sql += fields[i] + " varchar(10) PRIMARY KEY,";
			} else if (i == fields.length - 1) {
				sql += fields[i] + " varchar(225));";
			} else {
				sql += fields[i] + " varchar(255),";
			}
		}

		// Thực hiện câu lệnh sql
		try {
			statement = connectionStaging.prepareStatement(sql);
			statement.execute();
			System.out.println("Create completed " + config.getDatawarehouse_table());
			isCreated = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null) {
					res.close();
				}
				if (statement != null) {
					statement.close();
				}

			} catch (SQLException e) {
				System.out.println("Khong the tao bang");
				e.printStackTrace();
			}
		}

		return isCreated;

	}

	// HÀM GHI LOGS
	public static void insertLog(MyConfig myConfig, String status,int rows_update_datawarehouse) {
		PreparedStatement statement = null;
		int id_config = myConfig.getId();

		String sql = "UPDATE logs SET\n status='" + status
				+ "',time_Datawarehouse=current_timestamp(),rows_update_datawarehouse="+rows_update_datawarehouse+" \n WHERE id_config=" + id_config;
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
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		DataWarehouse dataWarehouse = new DataWarehouse();
		dataWarehouse.run();
	}

}
