package com.svgs;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.fazecast.jSerialComm.SerialPort;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class ObdReader {
//  public static Socket socket;
    public static SerialPort socket;
    public static InputStream inputStream;
    public static OutputStream outputStream;
    public static final DoubleProperty boostValue = new SimpleDoubleProperty(0);
    public static final DoubleProperty revValue = new SimpleDoubleProperty(0);
    public static final DoubleProperty trimValue = new SimpleDoubleProperty(0);
    public static final DoubleProperty fuelPressureValue = new SimpleDoubleProperty(0);
    public static final DoubleProperty coolantValue = new SimpleDoubleProperty(0);
    public static final DoubleProperty loadValue = new SimpleDoubleProperty(0);
    public static final DoubleProperty speedValue = new SimpleDoubleProperty(0);
    public static final DoubleProperty throttleValue = new SimpleDoubleProperty(0);
    public static final DoubleProperty timingValue = new SimpleDoubleProperty(0);
// need them declared + initialized at the start so I can record the data properly as it runs. 

    public static ArrayList<Runnable> gaugesToUse = new ArrayList<>();

    public static void startobdRead(){
        try {
            socket = SerialPort.getCommPort("COM6"); //outputstream
            socket.setBaudRate(9600);
            if(!socket.openPort()){
                System.out.println("Didn't open port!!");
                return;
            }
                System.out.println("buh");

            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
                System.out.println("streams");

            rawCommand(inputStream, outputStream, "ATZ");
            rawCommand(inputStream, outputStream, "ATE0");
            rawCommand(inputStream, outputStream, "ATL0");
            rawCommand(inputStream, outputStream, "ATS0");
            rawCommand(inputStream, outputStream, "ATAT1");
            rawCommand(inputStream, outputStream, "ATSP0");
           

            System.out.println("Connected yo");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void clearInput(InputStream in) throws Exception {
        while (in.available() > 0) {
            in.read();
        }
    }

    private static String rawCommand(InputStream in, OutputStream out, String cmd) throws Exception {
        clearInput(in);

        out.write((cmd + "\r").getBytes());
        out.flush();

        StringBuilder response = new StringBuilder(); //found this thingy on google, but it's basically just a string
        long end = System.currentTimeMillis() + 5000;

        while (System.currentTimeMillis() < end) {
            while (in.available() > 0) {
                char c = (char) in.read();
                response.append(c);
                if (c == '>') {
                    String result = response.toString();
                  //  System.out.println(cmd + " -> " + result);
                    return result;
                }
            }
           Thread.sleep(50);
        }

        throw new RuntimeException("Timeout waiting for response to " + cmd +
                ". Partial: " + response);
    }

    public static DoubleProperty boostProperty(){
        return boostValue;
    }

    public static DoubleProperty revProperty(){
        return revValue;
    }

    public static DoubleProperty trimProperty(){
        return trimValue;
    }
    public static DoubleProperty fuelPressureProperty(){
        return fuelPressureValue;
    }
    public static DoubleProperty coolantProperty(){
        return coolantValue;
    }
    public static DoubleProperty loadProperty(){
        return loadValue;
    }
    public static DoubleProperty speedProperty(){
        return speedValue;
    }
    public static DoubleProperty throttleProperty(){
        return throttleValue;
    }
    public static DoubleProperty timingProperty(){
        return timingValue;
    }

    public static void getBoost(){ 
        double boostKPA = 0;
        //manifold - barometric = boost
        try {
            String manifoldCMD = rawCommand(inputStream,outputStream, "010B");
            Parser manifoldParser = new Parser(manifoldCMD,1);
            boostKPA = manifoldParser.getA() - 14.7346; //couldnt get active baro, so just going off a constant.
        } catch (Exception e) {
            System.out.println(e);
        }
        
        double boostPSI = boostKPA/6.895;
        Platform.runLater(() -> boostValue.set(boostPSI));
    }

    public static void getRevs(){
        try {
            String revCMD = rawCommand(inputStream, outputStream, "010C");

            Parser revParser = new Parser(revCMD,2);

            double rpms = (256*revParser.getA() + revParser.getB())/4;
            Platform.runLater(() -> revValue.set(rpms));
        } catch (Exception e) {
            System.out.println(e);
        }
        
    }

// I called it "dodat" because it "does dat (that)". If I change it now, I would have to change a lot of names everywhere.
// it's basically just a polling method that gets all the info and records it too.
//utilizes a thread for it.
    public static void dodat(){
        Thread dodatThread = new Thread(() -> {
            while(true){
                for(Runnable x: gaugesToUse){
                    x.run();
                }
                SaveManager.recordValues();
                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                    break;
                }
            }
        });
        dodatThread.setDaemon(true);
        dodatThread.start();
    }

    public static void getFuelTrim(){
        try {
          String trimCMD = rawCommand(inputStream, outputStream, "0106");  
          Parser trimParser = new Parser(trimCMD,1);
          double fuelTrim = (100.0/128.0)*trimParser.getA()-100;
          Platform.runLater(() -> trimValue.set(fuelTrim));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void getCoolantTemp(){
        try {
            String coolantCMD = rawCommand(inputStream, outputStream, "0105");
            Parser coolantParser = new Parser(coolantCMD,1);
            double coolantTemp = coolantParser.getA()-40.0;
            Platform.runLater(() -> coolantValue.set(coolantTemp));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void getFuelPressure(){
        try {
            String fuelPressureCMD = rawCommand(inputStream, outputStream, "010A");
            Parser pressureParser = new Parser(fuelPressureCMD,1);
            double fuelPressure = pressureParser.getA()*3.0;
            Platform.runLater(() -> fuelPressureValue.set(fuelPressure));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void getEngineLoad(){
        try {
            String engineLoadCMD = rawCommand(inputStream, outputStream, "0104");
            Parser loadParser = new Parser(engineLoadCMD,1);
            double engineLoad = 100.0/255.0 * loadParser.getA();
            Platform.runLater(() -> loadValue.set(engineLoad));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void getVehicleSpeed(){
        try {
            String vehicleSpeedCMD = rawCommand(inputStream, outputStream, "010D");
            Parser speedParser = new Parser(vehicleSpeedCMD,1);
            double vehicleSpeed = speedParser.getA();
            Platform.runLater(() -> loadValue.set(vehicleSpeed));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void getThrottlePosition(){
        try {
            String throttlePositionCMD = rawCommand(inputStream, outputStream, "0111");
            Parser posParser = new Parser(throttlePositionCMD,1);
            System.out.println(throttlePositionCMD);
            double throttlePosition = 100.0/255.0 * posParser.getA();
            Platform.runLater(() -> loadValue.set(throttlePosition));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void getTimingPosition(){
        try {
            String timingCMD = rawCommand(inputStream, outputStream, "010E");
            Parser timingParser = new Parser(timingCMD,1);
            double engineTiming = (timingParser.getA()/2.0)-64;
            Platform.runLater(() -> loadValue.set(engineTiming));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
