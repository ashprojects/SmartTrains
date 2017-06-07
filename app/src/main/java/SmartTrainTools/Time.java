/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SmartTrainTools;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author jan
 */
public class Time implements Serializable {
    private String hh,mm;

    public Time(String hh, String mm) {
        int h=Integer.parseInt(hh);
        int m=Integer.parseInt(mm);
        if(h<0||h>23||m<0||m>59){
            //throw new IllegalArgumentException();
        }
        this.hh = hh;
        this.mm = mm;
    }

    public static Time returnTimeInstanceFromString(String T){
        String a=T.split(" ")[1];
        String hh=a.equals("AM")?(T.split(":")[0]):(Integer.parseInt(T.split(":")[0])+12+"");
        if(hh.equals(12) && a.equals("AM"))
            hh="00";
        return new Time(hh,T.split(":")[1].split(" ")[0]);
    }
    public static Time parseTime(String s){

        String[] split = s.split(":");
        if(split.length!=2){

            throw new IllegalArgumentException();
        }

        try {
            return new Time(split[0],split[1]);
        } catch (NumberFormatException e) {

            throw new IllegalArgumentException();
        }
    }




    public String getHh() {
        return hh;
    }

    public void setHh(String hh) {
        this.hh = hh;
    }

    public String getMm() {
        return mm;
    }

    public void setMm(String mm) {
        this.mm = mm;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.hh);
        hash = 97 * hash + Objects.hashCode(this.mm);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Time other = (Time) obj;
        if (!Objects.equals(this.hh, other.hh)) {
            return false;
        }
        if (!Objects.equals(this.mm, other.mm)) {
            return false;
        }
        return true;
    }



    
    /*public String toString(){
        return hh+":"+mm;
    }
    */
    public String toString(){
        if(Integer.parseInt(hh)>11){
            return ((Integer.parseInt(hh)-12)==0?""+12:Integer.parseInt(hh)-12)+":"+mm+" PM";
        } else {
            return ((Integer.parseInt(hh)==0?""+12:hh)+":"+mm+" AM");
        }
    }
}
