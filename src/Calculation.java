import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
 
public class Calculation {
 
    public static void main(String[] args) {
        GetPropertyValues pv = new GetPropertyValues();
        try {
            pv.getWorkdays(pv.getPropValues());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
 
    public static class GetPropertyValues {
        InputStream inputStream;
        Logger LOGGER = LoggerFactory.getLogger(Calculation.class);
        
 
        private String[][] getPropValues() throws IOException {
        	LOGGER.info("Working days calculator in 2016\n");
        	
        	String[][] result = null;
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
                result = new String[prop.size()][2];
 
                for (int i = 0; e.hasMoreElements(); i++) {
                    result[i][0] = e.nextElement().toString();
                    result[i][1] = prop.getProperty(result[i][0]);
                }
                
 
            } catch (Exception e) {
                System.out.println("Exception: " + e);
            } finally {
                inputStream.close();
            }
            return result;
        }
 
        private void getWorkdays(String[][] localResult) {
        	
        	DateTimeFormatter vacFor = null, extraVacFor = null, extraWorkFor = null;
        	DateTime[] extraWorkdays = new DateTime[1];
        	DateTime[] extraVacations = new DateTime[1];
        	DateTime[] vacations = new DateTime[4];
            int[] workdays = new int[5]; 
            int tmpWorkday = 0, tmpVacation = 0, tmpExtraVacation = 0, tmpExtraWorkday = 0;
            //System.out.println(localResult[1][0].toString() + " --> " + localResult[1][1].toString());
            for (int i = 0; i < localResult.length; i++) { // format-ok kimentése - késõbbi felhasználásra
             	if (!localResult[i][0].contains(".")) {
             		if (localResult[i][0].equals("vacation-format"))
             			vacFor = DateTimeFormat.forPattern(localResult[i][1]); 
             		else if (localResult[i][0].equals("extra-vacation-format"))
             			extraVacFor = DateTimeFormat.forPattern(localResult[i][1]);
             		else if (localResult[i][0].equals("extra-workday-format"))
             			extraWorkFor = DateTimeFormat.forPattern(localResult[i][1]);
             		
            	} 
            }
            
            for (int i = 0; i < localResult.length; i++) { // konkrét lista amire szûrni kell majd
             	if (localResult[i][0].contains(".")) {
	                String[] strTmp = localResult[i][0].split("\\.");
	               
	                if (strTmp[0].equals("workday")) {
	                    workdays[tmpWorkday] = Integer.parseInt(localResult[i][1]); // tömbbe a számokat mentsük el
	                    tmpWorkday++;
	                } else if (strTmp[0].equals("vacation")) {
	                	vacations[tmpVacation] = vacFor.parseDateTime(localResult[i][1]);
	                	tmpVacation++;
	                } else if (strTmp[0].equals("extra-vacation")){
	                	extraVacations[tmpExtraVacation] = extraVacFor.parseDateTime(localResult[i][1]);
	                	tmpExtraVacation++;
	                } else if (strTmp[0].equals("extra-workday")){
	                	extraWorkdays[tmpExtraWorkday] = extraWorkFor.parseDateTime(localResult[i][1]);
	                	//System.out.println(extraWorkdays[tmpExtraWorkday].toString(extraWorkFor)); //Teszteléshez
	                	tmpExtraWorkday++;
	                } 
	                
            	}
            	
            }

            
            DateTime startDate = new DateTime(2016, 1, 1, 0, 0);
            DateTime endDate = new DateTime(2016, 12, 31, 0, 0);
            DateTimeFormatter tmpFor = DateTimeFormat.forPattern("e"); // e
            ArrayList<DateTime> finalWorkdays = new ArrayList<DateTime>();
            
            for (; !startDate.equals(endDate.plusDays(1)) ; ) { //collect working days
            	boolean isAdded = false;
            	for (int j = 0; j < workdays.length; j++) {
            		if (!isAdded && startDate.toString(tmpFor).contains(String.valueOf(workdays[j]))) { 
            			finalWorkdays.add(startDate);
            			isAdded = true;
            		} else {
            			for (int h = 0; h < extraWorkdays.length; h++) {
            				if (!isAdded && startDate.toString(extraWorkFor).contains(String.valueOf(extraWorkdays[h].toString(extraWorkFor)))) {
            					finalWorkdays.add(startDate);
            					isAdded = true;
            				}
            			}
            		}
            	}
                startDate = startDate.plusDays(1);
            }
            
            // remove vacations - extra vacations
            for (Iterator<DateTime> it = finalWorkdays.iterator(); it.hasNext(); ) {
                DateTime finalW = it.next();
                for (DateTime extraV : extraVacations) {
            		if (extraV.toString(extraVacFor).contains(String.valueOf(finalW.toString(extraVacFor)))) { 
            			it.remove();
            		}
            	}
                for (DateTime vac : vacations) {
            		if (vac.toString(vacFor).contains(String.valueOf(finalW.toString(vacFor)))) { 
            			it.remove();
            		}
            	}
            }
            
            // log out final list of working days in 2016
            for (DateTime d : finalWorkdays) {
            	LOGGER.info(d.toString("yyyy-MM-dd") + " / " + d.toString(tmpFor) + ". nap a héten");
            }
        }
 
    }
}