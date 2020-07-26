package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import connections.GetConnection;


public class DateDim {

	public static List<LocalDate> getDatesBetweenUsingJava8(LocalDate startDate, LocalDate endDate) {

		long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);
		return IntStream.iterate(0, i -> i + 1).limit(numOfDaysBetween).mapToObj(i -> startDate.plusDays(i))
				.collect(Collectors.toList());
	}
	public void mainMehtod(List<LocalDate> list) {
		Connection conn = GetConnection.getConnection("datawarehouse");
		PreparedStatement pre = null;
		try {
			for (LocalDate l : list) {
			 pre = conn.prepareStatement(
						"insert into date_dim (Full_date, Day_Of_Week,"
								+ " CALENDAR_MONTH, CALENDAR_YEAR,Calendar_Year_Month,Day_OF_Month,Day_of_year)"
								 + " values(?,?,?,?,?,?,?)");

				pre.setDate(1, java.sql.Date.valueOf(l));// full date
			
				pre.setString(2, l.getDayOfWeek() + "");// Day_Of_Week
				pre.setString(3, l.getMonth() + "");// CALENDAR_MONTH
				pre.setInt(4, l.getYear());// CALENDAR_YEAR
				pre.setString(5, l.getYear() + "-" + l.getMonth());// Calendar_Year_Month
				pre.setInt(6, l.getDayOfMonth());// Day_OF_Month
				pre.setInt(7, l.getDayOfYear());// Day_of_year
				
				pre.executeUpdate();
				System.out.println(java.sql.Date.valueOf(l));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
			if(conn!=null) {
					conn.close();
				} 
			if(pre!=null) {
				pre.close();
			}
			}
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		LocalDate start = LocalDate.of(1996, 1, 1);//year - month- day
		LocalDate end = LocalDate.of(2001, 1, 1);
		
		List<LocalDate> list = getDatesBetweenUsingJava8(start, end);
		new DateDim().mainMehtod(list);
	}
}
