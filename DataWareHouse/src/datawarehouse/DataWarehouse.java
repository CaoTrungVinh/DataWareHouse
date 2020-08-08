package datawarehouse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.mysql.cj.jdbc.result.ResultSetMetaData;

import connections.GetConnection;
import mail.SendMailSSL;
import model.MyConfig;

public class DataWarehouse {
	SendMailSSL sendMail = null;
	int id_config = 0;

	public DataWarehouse(int id_config) {
		sendMail = new SendMailSSL();
		this.id_config = id_config;
	}

	public void start() {
		// 1. MỞ KẾT NỐI DATABASE Control_Data
		Connection connect_control = GetConnection.getConnection("control");

		// KIỂM TRA KẾT NỐI
		// NHÁNH 1: NẾU KẾT NỐI KHÔNG THÀNH CÔNG --> 2. ĐÓNG KẾT NỐI
		if (connect_control == null) {
			try {
				connect_control.close();

				// 3. GỬI MAIL THÔNG BÁO LỖI
				sendMail.sendMail("[ERROR] CONNECT TO CONTROL FAILED", "Không thể kết nối tới database Control_data");
				System.out.println("[ERROR] CONNECT TO CONTROL FAILED");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			// NHÁNH 2 NẾU KẾT NỐI THÀNH CÔNG --> 2.LẤY DỮ LIỆU TỪ BẢNG CONFIG DỰA TRÊN ID
			// MUỐN LẤY VÀ CÓ STATUS ='TR'
			// TRẢ VỀ MỘT RESULTSET --> 4.1 LƯU LIỆU VÀO MỘT CONFIG
			MyConfig myConfig = getValuesFromConfig(connect_control, id_config);

			if (myConfig == null) {
				System.out.println("There is no data to load");
			} else {

				// 5. MỞ KẾT NỐI DATABASE Staging VÀ DATABASE DataWarehouse
				Connection connect_staging = GetConnection.getConnection("staging");
				Connection connect_warehouse = GetConnection.getConnection("datawarehouse");

				// NHÁNH 1: NẾU KẾT NỐI KHÔNG THÀNH CÔNG --> 6. ĐÓNG KẾT NỐI
				if (connect_staging == null || connect_warehouse == null) {
					try {
						connect_staging.close();
						connect_warehouse.close();
						connect_control.close();

						// 7. GỬI MAIL THÔNG BÁO LỖI
						sendMail.sendMail("[ERROR] CONNECT DATAWAREHOUSE AND STAGING FAILED",
								"Không thể kết nối tới database Staging hoặc datawarehouse");
						// IN RA CONSOLE
						System.out.println("[ERROR] CONNECT DATAWAREHOUSE AND STAGING FAILED");

					} catch (SQLException e) {
						e.printStackTrace();
					}

				} else {
					// NHÁNH 2: KẾT NỐI THÀNH CÔNG

					// KIỂM TRA ĐÃ TỒN TẠI TABLE CHƯA?
					// NHÁNH 1: CHƯA TỒN TẠI DỮ LIỆU
					if (checkTableExist(connect_warehouse, myConfig.getDatawarehouse_table(), "datawarehouse") == 0) {

						// 7. BẮT ĐẦU TẠO BẢNG
						if (createTable(myConfig, connect_warehouse)) {

							// 8. LẤY DỮ LIỆU ---> 9. INSERT DỮ LIỆU TỪ STAGING QUA DATAWAREHOUSE
							tranferDataToDatawarehouse(connect_staging, connect_warehouse, myConfig);
						}

					}
					// NHÁNH 2: ĐÃ TỒN TẠI BẢNG HOẶC DỮ LIỆU TỪ TRƯỚC
					else {

						// TIẾN HÀNH UPDATE DỮ LIỆU
						updateData(connect_staging, connect_warehouse, myConfig);
					}
				}
				// 13.ĐÓNG TẤT CẢ CÁC KÊT NỐI DATABASE
				try {
				if (connect_control != null) {
						connect_control.close();
				}
				if (connect_staging != null) {
					connect_staging.close();
				}
				if (connect_warehouse != null) {
					connect_warehouse.close();
				}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

	}



	private MyConfig getValuesFromConfig(Connection connection, int id) {

		// CÂU SQL LẤY DỮ LIỆU TỪ CONFIG
		String sql = "SELECT log.id,log.id_config, conf.staging_table, conf.field_name,"
				+ "conf.number_cols,conf.datawarehouse_table,conf.cols_date, conf.field_name_dwh,"
				+ "conf.number_cols_dwh \r\n" + "FROM config conf join logs log on conf.id = log.id_config\r\n"
				+ "WHERE   log.status = 'TR' AND log.id_config=" + id + ";";

		MyConfig myConfig = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			statement = connection.prepareStatement(sql);
			// 3. TRẢ VỀ MỘT resultSet
			resultSet = statement.executeQuery();

			if (resultSet.next()) {
				myConfig = new MyConfig();

				// 4.1 LƯU DỮ LIỆU VÀO MyConfig
				myConfig.setId(resultSet.getInt("id_config"));
				myConfig.setStaging_table(resultSet.getString("staging_table"));
				myConfig.setVariabless(resultSet.getString("conf.field_name"));
				myConfig.setDatawarehouse_table(resultSet.getString("datawarehouse_table"));
				myConfig.setId_log(resultSet.getInt("id"));
				myConfig.setCols_date(resultSet.getString("cols_date"));
				myConfig.setField_name_dwh(resultSet.getString("field_name_dwh"));
				myConfig.setNumber_cols_dwh(resultSet.getInt("number_cols_dwh"));

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {

				if (statement != null) {
					statement.close();
				}
				if (resultSet != null) {

					resultSet.close();
				}

			} catch (SQLException e) {
				System.out.println("Khong the tao bang");
			}
		}
		// TRẢ VỀ MỘT CONFIG
		return myConfig;
	}

	private void tranferDataToDatawarehouse(Connection connec_Stag, Connection connect_warehouse, MyConfig config) {
		int cols = config.getNumber_cols_dwh();// -- Số lượng cột trong datawarehouse

		String field_dwh[] = config.getField_name_dwh().split(",");
		boolean isDate_Dim = false; // -- Kiểm tra xem bảng insert có cần tìm sk_date_dim không

		// TẠO CÂU SQL LẤY DỮ LIỆU TỪ STAGING
		String sqlSelectStaging = "SELECT ";
		for (int i = 0; i < field_dwh.length; i++) {

			if (field_dwh[i].equalsIgnoreCase("sk_date_dim")) {
				cols = cols - 1;
				isDate_Dim = true;
				continue;
			} else if (i == field_dwh.length - 1) {
				sqlSelectStaging += field_dwh[i];
			} else {
				if (field_dwh[i + 1].equalsIgnoreCase("sk_date_dim") && (i + 1) == field_dwh.length - 1) {
					sqlSelectStaging += field_dwh[i];
				} else {
					sqlSelectStaging += field_dwh[i] + ",";
				}

			}

		}
		sqlSelectStaging += " FROM " + config.getStaging_table();
		// ** KẾT THÚC VIỆC TẠO CÂU SQL LẤY DỮ LIỆU

		PreparedStatement statement = null;
		ResultSet resultStaging = null;

		PreparedStatement statementWareHouse = null;
		ResultSet resultWareHouse = null;

		try {
			// 8. LẤY DỮ LIỆU TRONG TABLE CỦA DATABASE Staging
			statement = connec_Stag.prepareStatement(sqlSelectStaging);
			resultStaging = statement.executeQuery();
			ResultSetMetaData resMetaData = (ResultSetMetaData) resultStaging.getMetaData();

			// 9 .XỬ LÍ DỮ LIỆU TRƯỚC KHI INSERT
			while (resultStaging.next()) {
				String date = "";// format date từ staging
				int sk_date_dim = -1;// sk_date_dim mặc định = -1;
				// Tạo câu sql insert
				String sqlInsert = "INSERT INTO " + config.getDatawarehouse_table() + " VALUES(";
				for (int i = 1; i <= cols; i++) {

					// KIỂM TRA NẾU KHÓA CHÍNH KHÔNG CÓ THÌ BỎ QUA KHÔNG CHO DỮ LIỆU TRANSFER
					if (resultStaging.getString(1) == null) {
						continue;

					}
					// KIỂM TRA NẾU LÀ CỘT CHỨA KIỂU DỮ LIỆU DATE THÌ FORMAT VÀ LẤY ID DATEDIM ĐỂ
					// INSERT
					else if (resMetaData.getColumnName(i).equalsIgnoreCase(config.getCols_date())) {

						// FORMAT DỮ LIỆU DATE
						date = formatDate(resultStaging.getString(i));
						// TÌM RA sk_date_dim TỪ TABLE DATEDIM
						sk_date_dim = getIdDateDim(date, connect_warehouse);

						if (i == cols) {
							sqlInsert += "'" + date + "');";
						} else {
							sqlInsert += "'" + date + "',";
						}

					}
					// NẾU LÀ CÁC CỘT BÌNH THƯỜNG THÌ TIẾP TỤC TẠO CÂU INSERT SQL
					else if (i == 1) {
						sqlInsert += "" + resultStaging.getString(i) + ",";
					} else if (i == cols) {

						// INSERT sk_date_dim
						if (isDate_Dim) {
							sqlInsert += "'" + resultStaging.getString(i) + "',";
							sqlInsert += "" + sk_date_dim + ");";
						} else {
							sqlInsert += "'" + resultStaging.getString(i) + "');";
						}

					} else {
						sqlInsert += "'" + resultStaging.getString(i) + "',";
					}

				}

				statementWareHouse = connect_warehouse.prepareStatement(sqlInsert);

//				10. INSERT DỮ LIỆU TỪ STAGING QUA DATAWAREHOUSE
				statementWareHouse.executeUpdate();
				System.out.println("Successfully insert the row(s)");

			}
			// IN RA CONSOLE
			System.out.println("DATA TRANSFER FROM Staging TABLE " + config.getStaging_table()
					+ " TO datawarehouse SUCCESSFUL." + config.getDatawarehouse_table() + "\n\n");

			// 11. GỬI MAIL THÔNG BÁO INSERT THÀNH CÔNG
			sendMail.sendMail("[SUCCESS] DATA TRANSFER TO DATAWAREHOUSE", "Transform data thành công từ Staging table."
					+ config.getStaging_table() + " sang datawarehouse." + config.getDatawarehouse_table() + "\n\n");

			// 12. CẬP NHẬT LOGS STATUS='SUCCESS'
			insertLog(config, "SUCCESS", 0);

		} catch (SQLException e) {

			// 11. GỬI MAIL THÔNG BÁO LỖI
			sendMail.sendMail("[ERROR] DATA TRANSFER TO DATAWAREHOUSE",
					config.getDatawarehouse_table() + " KHÔNG THÊ INSERT DỮ LIỆU");

			// 12.CẬP NHẬT LOGS STATUS='ERROR_TRANSFER'
			insertLog(config, "ERROR_TRANSFER", 0);
			e.printStackTrace();
		} finally {
			// 4.2 ĐÓNG KẾT NỐI DATABASE control_data
			try {

				if (statement != null) {
					statement.close();
				}
				if (resultStaging != null) {
					resultStaging.close();
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

		boolean isCreated = false; // true: Tạo thành công , false: Tạo bảng thất bại
		PreparedStatement statement = null;
		ResultSet res = null;

		// LẤY TÊN CỘT TỪ CONFIG
		String[] fields = config.getField_name_dwh().split(",");

		// KHỞI TẠO CÂU TẠO BẢNG
		String sql = "Create table " + config.getDatawarehouse_table() + " (";
		int count_date = 0;
		// CHẠY VÒNG FOR ĐỂ TAO CÂU SQL
		for (int i = 0; i < fields.length; i++) {
			// TẠO KHÓA CHÍNH CHO BẢNG
			if (i == 0) {
				sql += fields[i] + " INT PRIMARY KEY,";
			}
			// NẾU LÀ CỘT DỮ LIỆU CUỐI THÌ THÊM ')'
			else if (i == fields.length - 1) {

				if (fields[i].equalsIgnoreCase(config.getCols_date())) {
					sql += fields[i] + " date);";
				}
				// NẾU CÓ CỘT sk_date_dim TẠO VỚI KIỂU DỮ LIỆU LÀ INT
				else if (fields[i].equalsIgnoreCase("sk_date_dim")) {
					sql += fields[i] + " int);";
				} else
				// TẠO TẤT CẢ CÁC CỘT CÒN LẠI KIỂU DỮ LIỆU LÀ VARCHAR
				{
					sql += fields[i] + " varchar(225));";
				}

			} else {
				if (fields[i].equalsIgnoreCase(config.getCols_date())) {
					sql += fields[i] + " date,";
				} else if (fields[i].equalsIgnoreCase("sk_date_dim")) {
					sql += fields[i] + " int,";
				} else {
					sql += fields[i] + " varchar(255),";
				}

			}
		}

		// TIẾN HÀNH THỰC HIỆN CÂU SQL
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
	public static void insertLog(MyConfig myConfig, String status, int rows_update_datawarehouse) {
		PreparedStatement statement = null;
		int id_log = myConfig.getId_log();

		String sql = "UPDATE logs SET\n status='" + status
				+ "',time_Datawarehouse=current_timestamp(),rows_update_datawarehouse=" + rows_update_datawarehouse
				+ " \n WHERE id=" + id_log;
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
//					connection.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public String formatDate(String resDate) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		String result = "";
		java.util.Date parsedate;
		try {
			parsedate = format.parse(resDate);
			java.sql.Date date = new java.sql.Date(parsedate.getTime());
			result = date.toString();
		} catch (ParseException e) {
			result = resDate;
		}
		return result;

	}

	public int getIdDateDim(String date, Connection connec_DatWare) {
		String sql = "Select Date_SK\r\n" + "from date_dim d\r\n" + "where d.full_date = '" + date + "';";
		int date_SK = -1;
		try {
			PreparedStatement statement = connec_DatWare.prepareStatement(sql);
			ResultSet res = statement.executeQuery();
			while (res.next()) {
				date_SK = res.getInt(1);
			}
			return date_SK;
		} catch (SQLException e) {
			System.out.println("WARRING: Wrong date format");
		}
		return date_SK;
	}

	private void updateData(Connection connect_staging, Connection connect_warehouse, MyConfig config) {
		int cols = config.getNumber_cols_dwh();

		String primaryKeySta = "";//
		int rows_update_datawarehouse = 0;// ĐẾM SỐ HÀNG ĐƯỢC CẬP NHẬT

		// TẠO CÂU SQL LẤY DỮ LIỆU TỪNG DÒNG TỪ STAGING ĐỂ SO SÁNH
		String sqlSelectStaging = "SELECT ";
		String field_dwh[] = config.getField_name_dwh().split(",");
		boolean isDate_Dim = false;

		for (int i = 0; i < field_dwh.length; i++) {
			if (field_dwh[i].equalsIgnoreCase("sk_date_dim")) {
				cols = cols - 1;
				isDate_Dim = true;
				continue;
			} else if (i == field_dwh.length - 1) {
				sqlSelectStaging += field_dwh[i];
			} else {
				if (field_dwh[i + 1].equalsIgnoreCase("sk_date_dim") && (i + 1) == field_dwh.length - 1) {
					sqlSelectStaging += field_dwh[i];
				} else {
					sqlSelectStaging += field_dwh[i] + ",";
				}

			}

		}

		sqlSelectStaging += " FROM " + config.getStaging_table();

		PreparedStatement statement = null;
		ResultSet result = null;
		PreparedStatement statementWareHouse = null;
		ResultSet resultWareHouse = null;

		try {

			statement = connect_staging.prepareStatement(sqlSelectStaging);
			result = statement.executeQuery();
			while (result.next()) {
				// 7.KIỂM TRA KHÓA CHÍNH NÀY DÃ TỒN TẠI TRONG BẢNG DATAWAREHOUSE
				String sqlCheckExist = "SELECT COUNT(*)\r\n FROM " + config.getDatawarehouse_table() + "\r\t WHERE "
						+ field_dwh[0] + "='";
				ResultSetMetaData rsMeta = (ResultSetMetaData) result.getMetaData();
				primaryKeySta = result.getString(1);
				
				if (primaryKeySta == null || primaryKeySta.equals("")) {
					continue;
				}

				sqlCheckExist += primaryKeySta + "';";

				statementWareHouse = connect_warehouse.prepareStatement(sqlCheckExist);
				resultWareHouse = statementWareHouse.executeQuery();

				while (resultWareHouse.next()) {

					// KIỂM TRA TỪNG PK( RESULT TRẢ VỀ 1 NẾU TỒN TẠI NGƯỢC LẠI TRẢ VỀ 0)
					// NHÁNH 1: PK ĐÃ TỒN TẠI RỒI
					if (resultWareHouse.getInt(1) == 1) {
						// 8.LẤY DỮ LIỆU CỦA TỪNG DÒNG BÊN BẢNG DATAWAREHOUSE
						String sqlGet = "Select * from " + config.getDatawarehouse_table() + " where " + field_dwh[0]
								+ " = '" + primaryKeySta + "';";

						PreparedStatement statementGet = connect_warehouse.prepareStatement(sqlGet);
						ResultSet resutlSetGet = statementGet.executeQuery();
						ResultSetMetaData reMeta = (ResultSetMetaData) resutlSetGet.getMetaData();
						
						//9.XỬ LÍ DỮ LIỆU
						while (resutlSetGet.next()) {

							for (int i = 2; i <= cols; i++) {
								// SO SÁNH DỮ LIỆU DATE
								if (reMeta.getColumnName(i).equalsIgnoreCase(config.getCols_date())) {
									// FORMAT DATE TỪ STAGING
									String date = formatDate(result.getString(i));

									// 10. NẾU DỮ LIỆU DATE KHÁC NHAU THÌ TIẾN HÀNH THAY THẾ VÀ TÌM LẠI sk_date_dim
									if (!date.equalsIgnoreCase(resutlSetGet.getDate(i).toString())) {
										String sqlUpadte = "";
										int sk_date_dim = getIdDateDim(date, connect_warehouse);

										sqlUpadte = "Update " + config.getDatawarehouse_table() + " SET "
												+ reMeta.getColumnName(i) + "='" + formatDate(result.getString(i))
												+ "',sk_date_dim=" + sk_date_dim + " WHERE " + reMeta.getColumnName(1)
												+ "=" + result.getInt(1) + "";

										System.out.println(sqlUpadte);
										PreparedStatement statementUpdate = connect_warehouse
												.prepareStatement(sqlUpadte);
										statementUpdate.execute();

										System.out.println(result.getString(i) + " ----> " + resutlSetGet.getString(i));
									}
								}
								// SO SÁNH DỮ LIỆU BÌNH THƯỜNG (VARCHAR)
								// ĐIỀU KIỆN CẢ HAI PHẢI ĐỀU KHÁC NULL
								else if (result.getString(i) != null && resutlSetGet.getString(i) != null) {
									
									//  10. NẾU DỮ LIỆU KHÁC NHAU TIẾN HÀNH THÌ THAY THẾ 
									if (!result.getString(i).equalsIgnoreCase(resutlSetGet.getString(i))) {
										String sqlUpadte = "";

										sqlUpadte = "Update " + config.getDatawarehouse_table() + " SET "
												+ reMeta.getColumnName(i) + "='" + result.getString(i) + "' WHERE "
												+ reMeta.getColumnName(1) + "=" + result.getInt(1) + "";

										System.out.println(sqlUpadte);
										PreparedStatement statementUpdate = connect_warehouse
												.prepareStatement(sqlUpadte);
										statementUpdate.execute();

										System.out.println(result.getString(i) + " ----> " + resutlSetGet.getString(i));
									}
								}

							}

						}

					}
					// NHÁNH 2: SK CHƯA TỒN
					else {
						// INSERT DỮ LIỆU MƠÍ VÀO
						System.out.println(rsMeta.getColumnName(1) + ": " + primaryKeySta
								+ " PREPARING TO ADD NEW DATA TO THE TABLE " + "" + config.getDatawarehouse_table());

						String date = "";
						int sk_date_dim = -1;
						String sqlInsert = "INSERT INTO " + config.getDatawarehouse_table() + " VALUES(";
						for (int i = 1; i <= cols; i++) {
							if (result.getString(1) == null) {
								continue;
								// KIỂM TRA NẾU LÀ CỘT DATE CẦN FORMAT LẠI VÀ LẤY ID DATEDIM
							} else if (rsMeta.getColumnName(i).equalsIgnoreCase(config.getCols_date())) {
								date = formatDate(result.getString(i));
								sk_date_dim = getIdDateDim(date, connect_warehouse);
								if (i == cols) {
									sqlInsert += "'" + date + "');";
								} else {
									sqlInsert += "'" + date + "',";

								}
								// INSERT SK_date_dim VÀO BẢNG
							} else if (i == 1) {
								sqlInsert += "" + result.getString(i) + ",";
							} else if (i == cols) {
								if (isDate_Dim) {
									sqlInsert += "'" + result.getString(i) + "',";
									sqlInsert += "" + sk_date_dim + ");";
								} else {
									sqlInsert += "'" + result.getString(i) + "');";
								}

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
			// GHI LOG NẾU CÓ ROW UPDATE
			if (rows_update_datawarehouse > 0) {
				//IN RA CONSOLE SỐ ROW DẪ UPDATE 
				System.out.println(config.getDatawarehouse_table() + " \r\n" + "updated row "
						+ rows_update_datawarehouse + " ROW" + "\n\n");
				// 11.GỬI MAIL THÔNG BÁO THÀNH CÔNG
				sendMail.sendMail("[SUCCESS] UPDATE DATA DATAWAREHOUSE",
						config.getDatawarehouse_table() + " update " + rows_update_datawarehouse + " rows");
				// 12.GHI VÀO LOGS
				insertLog(config, "SUCCESS", rows_update_datawarehouse);

			} else {
				// 11.GỬI MAIL THÔNG BÁO THÀNH CÔNG
				sendMail.sendMail("[SUCCESS] TRANSFORM DATAWAREHOUSE",
						config.getDatawarehouse_table() + " KHÔNG CÓ THAY ĐỔI " + "\n\n");
				// 12.GHI VÀO LOGS
				insertLog(config, "SUCCESS", rows_update_datawarehouse);
			}

		} catch (SQLException e) {
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

	public static void main(String[] args) {
		DataWarehouse dataWarehouse = new DataWarehouse(3);
		dataWarehouse.start();
	}

}
