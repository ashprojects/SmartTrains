/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SmartTrainTools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author ashish
 */
public class TravelClass implements Serializable{
    private String classCode;
    private String classFull;
    public static HashMap<String, String> allClasses;

    static {
        allClasses=new HashMap<>();
        allClasses.put("1A", "FIRST AC");
        allClasses.put("2A", "SECOND AC");
        allClasses.put("3A", "THIRD AC");
        //allClasses.put("3E")
        allClasses.put("CC", "CHAIR CAR");
        allClasses.put("SL", "SLEEPER CLASS");
        allClasses.put("FC", "FIRST CLASS");
        allClasses.put("2S", "SECOND SITTING");
    }

    public TravelClass(String code){
        this.classCode=code;
        this.classFull=RailwayCodes.getClassName(code);
    }
/*
            '1A': "0",
            'FC': "1",
            '2A': "2",
            '3A': "3",
            '3E': "4",
            'CC': "5",
            'SL': "6",
            '2S': "7",
            'GN': "8"
 */

    public String getClassFull() {
        return classFull;
    }

    public void setClassFull(String classFull) {
        this.classFull = classFull;
    }
    
    public static ArrayList<TravelClass> getTravelClassesFrom(int[] av){
        ArrayList<TravelClass> tc=new ArrayList<>();
        for(int i=0;i<av.length;++i){
            switch(i){
                case 0:
                    if(av[i]==1)
                        tc.add(new TravelClass("1A"));
                    break;
                case 1:
                    if(av[i]==1)
                        tc.add(new TravelClass("FC"));
                    break;
                case 2:
                    if(av[i]==1)
                        tc.add(new TravelClass("2A"));
                    break;
                case 3:
                    if(av[i]==1)
                        tc.add(new TravelClass("3A"));
                    break;
                case 5:
                    if(av[i]==1)
                        tc.add(new TravelClass("CC"));
                    break;
                case 6:
                    if(av[i]==1)
                        tc.add(new TravelClass("SL"));
                    break;
                case 7:
                    if(av[i]==1)
                        tc.add(new TravelClass("2S"));
                    break;
            }
        }
        return tc;
    }
    
    private void fillClassName(){
        this.classFull=allClasses.get(classCode);
        
    }

    public String[] getAllClassesFull(){
        return (String[])this.allClasses.keySet().toArray(new String[0]);
    }

    public static ArrayList<TravelClass> getAllClassesObjects(){
        ArrayList<TravelClass> tc=new ArrayList<>();
        tc.add(new TravelClass("1A"));
        tc.add(new TravelClass("2A"));
        tc.add(new TravelClass("3A"));
        tc.add(new TravelClass("SL"));
        tc.add(new TravelClass("FC"));
        tc.add(new TravelClass("CC"));
        tc.add(new TravelClass("2S"));
        return tc;
    }

    public String getClassCode() {
        return classCode;
    }

    @Override
    public String toString() {
        return "TravelClass{" + "classCode=" + classCode + ", classFull=" + classFull + '}';
    }
}
