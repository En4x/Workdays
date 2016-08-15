import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.text.DateFormatter;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormatter;

import java.time.LocalDate;

public class Calculation {

	public static void main(String[] args) {
		System.out.println("Fut");
		GetPropertyValues pv = new GetPropertyValues();
			try {
				pv.getPropValues();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public static class GetPropertyValues {
		String[][] result;
		InputStream inputStream;
		
		
	 
		private void getPropValues() throws IOException {
	 
			try {
				Properties prop = new Properties();
				String propFileName = "workday.properties";
	 
				inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
	 
				if (inputStream != null) {
					prop.load(inputStream);
				} else {
					throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
				}
				Enumeration<?> e = prop.propertyNames();
				String[][] result = new String[prop.size()][2]; 
				
				for (int i = 0; e.hasMoreElements(); i++) {
				  result[i][0] = e.nextElement().toString();
				  result[i][1] = prop.getProperty(result[i][0]);
			      //System.out.println(result[i][0].toString() + " --> " + result[i][1].toString());
 			    }
				DateTime startDate = new DateTime(2016, 1, 1, 0, 0);
				DateTime endDate = new DateTime(2016, 12, 31, 0, 0);
				int workdays = getWorkingDaysBetweenTwoDates(startDate, endDate);
				System.out.println(workdays);
				
			} catch (Exception e) {
				System.out.println("Exception: " + e);
			} finally {
				inputStream.close();
			}
		}
		
		private int getWorkingDaysBetweenTwoDates(DateTime startDate, DateTime endDate) {
			int result = Days.daysBetween(startDate.toLocalDate(), endDate.toLocalDate()).getDays();
			System.out.println(endDate.toString() + " " + startDate.toString());
		//	java.time.format.DateTimeFormatter formatter = DateTimeFormatter( "e");
			//startDate = LocalDate.parse(startDate.toString(), formatter);
			
			for (int i = 0; i< result; i++) {
				org.joda.time.LocalDate tmp = startDate.toLocalDate();
				tmp = tmp.plusDays(i);
				
				System.out.println(tmp.toString());
			}
			
			return result;
		}

		@Override
		public String toString() {
			return "GetPropertyValues [result=" + Arrays.toString(result) + "]";
		}
		
	}
}
