package SmartTrainTools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * Created by root on 19/3/17.
 */

public class SmartUtils {
    public static int getDayNumber(MyDate X) throws ParseException{
        System.out.println("For date: "+X.getDate());
        Calendar x=GregorianCalendar.getInstance();
        DateFormat f=new SimpleDateFormat("dd/MM/yyyy");
        Date y=f.parse(X.getDate());
        x.setTime(y);
        System.out.println(x);
        return x.get(Calendar.DAY_OF_WEEK);
    }

    public static boolean validTime(){

        if(Calendar.HOUR_OF_DAY>=11 && Calendar.HOUR_OF_DAY<13){
            //return false;
        }
        return true;

    }
    public static String getDayName(MyDate X) {
        try {
            DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
            Date y = f.parse(X.getDate());
            return new SimpleDateFormat("EEEE").format(y);
        }catch (ParseException E){
            return "";
        }
    }

    public static String getRunsOnString(int a[]){
        char[] x="SMTWTFS".toCharArray();
        String u="";
        for(int i=0;i<7;++i){
            if(a[i]==1)
                u+=x[i];
        }
        return u;
    }

    public static String getParsedDate(MyDate X,String u){
        try {
            DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
            Date y = f.parse(X.getDate());
            return new SimpleDateFormat(u).format(y);
        }catch (ParseException E){
            return "/"+X.getM();
        }
    }
}
