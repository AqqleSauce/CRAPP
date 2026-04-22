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

    public static ArrayList<Runnable> gaugesToUse = new ArrayList<>();

    public static void startobdRead(){
        try {
            socket = SerialPort.getCommPort("COM6"); 
            socket.setBaudRate(9600);
            if(!socket.openPort()){
                System.out.println("Didn't open port!!");
                return;
            }
                System.out.println("buh");
                Thread.sleep(1000);

            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
                System.out.println("streams");

            rawCommand(inputStream, outputStream, "ATZ");
            rawCommand(inputStream, outputStream, "ATE0");
            rawCommand(inputStream, outputStream, "ATL0");
            rawCommand(inputStream, outputStream, "ATS0");
            rawCommand(inputStream, outputStream, "ATAT1");
            rawCommand(inputStream, outputStream, "ATSP0");
            rawCommand(inputStream, outputStream, "010C");

            // System.out.println("Starting pires stuff...");
            // new EchoOffCommand().run(inputStream, outputStream);
            // System.out.println("Echo off done");
            // new LineFeedOffCommand().run(inputStream, outputStream);
            // System.out.println("Line feed off done");
            // new TimeoutCommand(62).run(inputStream, outputStream);
            // System.out.println("Timeout set");
            // new SelectProtocolCommand(ObdProtocols.AUTO).run(inputStream, outputStream);
            // System.out.println("Protocol selected");

            // RPMCommand rpm = new RPMCommand();
            // rpm.run(inputStream, outputStream);
            // System.out.println(rpm.getRPM());

            //starts the connection

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

        StringBuilder response = new StringBuilder();
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
           // Thread.sleep(20);
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

    public static void getBoost(){ //rewrite bruh
        int boostKPA = 0;
        //manifold - barometric = boost
        try {
            String manifoldCMD = rawCommand(inputStream,outputStream, "010B");
            String baroCMD = rawCommand(inputStream, outputStream, "0133");

            Poop manifoldPoop = new Poop(manifoldCMD);
            Poop barometricPoop = new Poop(baroCMD);

            boostKPA = manifoldPoop.getA() - barometricPoop.getA();
            
        } catch (Exception e) {
            System.out.println(e);
        }
        
        double boostPSI = boostKPA/6.895;
        Platform.runLater(() -> boostValue.set(boostPSI));
    }

    public static void startBoostThread(){
        Thread boostThread = new Thread(() -> {
            while(true) {
                getBoost();
                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                    break;
                }
            }
        });
        boostThread.setDaemon(true);
        boostThread.start();
    }

    public static void getRevs(){
        try {
            String revCMD = rawCommand(inputStream, outputStream, "010C");

            Poop revPoop = new Poop(revCMD);

            double rpms = (256*revPoop.getA() + revPoop.getB())/4;
            Platform.runLater(() -> revValue.set(rpms));
        } catch (Exception e) {
            System.out.println(e);
        }
        
    }

    public static void startRpmsThread(){
        Thread rpmThread = new Thread(() -> {
            while(true){
                getRevs();
                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                    break;
                }
            }
        });
        rpmThread.setDaemon(true);
        rpmThread.start();
    }
    
    // public static String getFuelTrim(){
    //     //returns fuel trim in format "stft" 
    //     FuelTrimCommand stft = new FuelTrimCommand(FuelTrim.SHORT_TERM_BANK_1);
    //     FuelTrimCommand ltft = new FuelTrimCommand(FuelTrim.LONG_TERM_BANK_1); //ignore
    //     String shortFuelTrim = stft.getFormattedResult();
    //     String longFuelTrim = ltft.getFormattedResult();//ignore for now
    //     return (shortFuelTrim+"");
    // }

    // public static String timingPosition(){
    //     TimingAdvanceCommand timing = new TimingAdvanceCommand();
    //     String timingDegrees = "";
    //     try {
    //     timing.run(socket.getInputStream(),socket.getOutputStream());
    //     timingDegrees = timing.getCalculatedResult();
    //     System.out.println(timingDegrees);
    //     timingDegrees = timing.getFormattedResult();
    //     } catch (Exception e) {
    //         System.out.println(e);
    //     }
    //     return timingDegrees;
    // }


}
