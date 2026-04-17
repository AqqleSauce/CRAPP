package com.svgs;

public class Poop {
    public int A;
    public int B;
    public Poop(String input){
        String data = input.substring(4);
        if(data.length() > 2){
        A = Integer.parseInt(data.substring(0,2), 16);
        B = Integer.parseInt(data.substring(2,4), 16);
        }
        else{
            A = Integer.parseInt(data, 16);
        }
    }

    public int getA(){
        return A;
    }
    public int getB(){
        return B;
    }
}
