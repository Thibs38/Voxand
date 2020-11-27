package com.thibsworkshop.voxand.debugging;

import com.thibsworkshop.voxand.io.Time;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Timing {

    //IMPORTANT: One record at a time for each method of each class, otherwise timing wont be correct

    //TODO: Maybe change the List of data to an array of fixed size, like 100, to avoid deleting the first element of the list
    private static Map<String, Map<String, List<Long>>> data = new HashMap<>(); //The data
    private static Map<String, Map<String, Boolean>> enabled = new HashMap<>(); //Is the method enabled for recording?

    //Add a class to measure, and a list of methods to measure
    public static void add(String name, String[] methods){
        Map<String, List<Long>> map = new HashMap<>();
        Map<String, Boolean> mapEnabled = new HashMap<>();
        for(String s : methods){
            map.put(s,new ArrayList<>());
            mapEnabled.put(s,false);
        }
        data.put(name,map);
        enabled.put(name,mapEnabled);

    }

    //Start recording time for the desired method
    public static void start(String name, String method){
        if(enabled.get(name).get(method))
            get(name, method).add(Time.getMicroTime());
    }

    //Stop the recording and store the result
    public static void stop(String name, String method){
        if(enabled.get(name).get(method)){
            long e = Time.getMicroTime();
            List<Long> values = get(name,method);
            long s = values.remove(values.size()-1);
            values.add(e-s);
        }
    }

    //Enable the recording of a specific method
    public static void enable(String name, String method){
        Map<String, Boolean> map = enabled.get(name);
        map.put(method,!map.get(method));
    }
    //Enable the recording of all methods
    public static void enable(String name){
        Map<String, Boolean> map = enabled.get(name);

        for(String method : map.keySet())
            map.put(method,!map.get(method));
    }

    //Clear the data of the specified name and method
    public static void clear(String name, String method){
        get(name,method).clear();
    }

    //returns the list of data of the specified name and method
    public static List<Long> get(String name, String method){
        return data.get(name).get(method);
    }

    //prints the data of the given method in milliseconds with precision number of digit
    public static void print(String name, String method, int precision){
        System.out.println("Timing of: " + name + " | " + method);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(precision);
        for(float val : get(name,method)){
            System.out.println(nf.format(val/1000f));
        }
    }
}
