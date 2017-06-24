/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SmartTrainTools;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author ashish
 */
public class MyDate implements Serializable{

    public int d,m,y;

    public int getD() {
        return d;
    }

    public int getM() {
        return m;
    }

    public int getY() {
        return y;
    }

    public String putZeros(int d,int m,int y){
        return String.valueOf(d)+"/"+((m>9)?String.valueOf(m):"0"+String.valueOf(m)) +"/"+String.valueOf(y);
    }

    public MyDate(int d, int m, int y) {
        smf=new SimpleDateFormat("dd/MM/yyyy");
        calendar=Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        try{
            dateObj=smf.parse(putZeros(d, m, y));
            calendar.setTime(dateObj);
        } catch (ParseException E){
            System.out.println("ParseEx");
        }
        this.d=d;
        this.m=m;
        this.y=y;

    }

    public MyDate(MyDate M){
        this(M.getD(),M.getM(),M.getY());
    }

    public String getDateUnParsed(){
        return this.d+"/"+this.m+"/"+ this.y;
    }
    public String getDate() {
        return putZeros(this.d, this.m, this.y);
    }

    public Date date() {
        return new Date(y, m, d);
    }

    public void decrement() {

        calendar.add(Calendar.DATE,-1);
        dateObj=calendar.getTime();

        this.d=dateObj.getDate();
        this.m=1+dateObj.getMonth();
        this.y=1900+dateObj.getYear();


    }
    public int getDayNumber(){
        int y= calendar.get(Calendar.DAY_OF_WEEK);
        if(y==7)
            return 0;
        else
            return y;
    }

    public void increment(int o){
        /*if(d+o>31){
            d=o;
            m=m+1;
            date=d+"/"+m+"/"+y;
        } else {
            d+=o;
            date=d+"/"+m+"/"+y;
        }*/
        //System.out.println(calendar.getTime());

        calendar.add(Calendar.DATE,o);
        dateObj=calendar.getTime();
        //System.out.println(dateObj.toString());
        //System.out.println(dateObj.getDate());
        this.d=dateObj.getDate();
        this.m=1+dateObj.getMonth();
        this.y=1900+dateObj.getYear();
    }
    @Override
    public String toString(){
        return putZeros(this.d, this.m, this.y);

    }


    public String getBeautifiedDate(){
        return ""+d+" "+(new SimpleDateFormat("MMM").format(calendar.getTime()).toString()) +" "+y;
    }

    SimpleDateFormat smf;
    Date dateObj;
    Calendar calendar;

    public static MyDate parseMyDate(String date, String format) throws ParseException {
        SimpleDateFormat smf = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        cal.setTime(smf.parse(date));
        return new MyDate(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
    }

    public String format(String format) {
        SimpleDateFormat smf = new SimpleDateFormat(format);
        return smf.format(date());
    }

}
