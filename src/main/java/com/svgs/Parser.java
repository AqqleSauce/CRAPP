package com.svgs;

public class Parser {
    public int A;
    public int B;
    public Parser(String input, int byteAmt){
        int hexDigits = byteAmt *2;
        String data = input.replaceAll("[^0-9A-Fa-f]","");
        // learned this in cybersecurity, regex to delete all non-hex characters in string.
    
        data = data.substring(data.length()- hexDigits);
        A = Integer.parseInt(data.substring(0,2), 16);
        if(byteAmt == 2){
            B = Integer.parseInt(data.substring(2,4), 16);
        }
    }

    public int getA(){
        return A;
    }
    public int getB(){
        return B;
    }

    
}
