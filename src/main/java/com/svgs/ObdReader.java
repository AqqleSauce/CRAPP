package com.svgs;

import java.io.InputStream;
import java.io.OutputStream;

import com.fazecast.jSerialComm.SerialPort;
import com.github.pires.obd.commands.control.TimingAdvanceCommand;
import com.github.pires.obd.commands.fuel.FuelTrimCommand;
import com.github.pires.obd.commands.pressure.BarometricPressureCommand;
import com.github.pires.obd.commands.pressure.IntakeManifoldPressureCommand;
import com.github.pires.obd.enums.FuelTrim;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;

public class ObdReader {
//  public static Socket socket;
    public static SerialPort socket;
    public static InputStream inputStream;
    public static OutputStream outputStream;

    public static void startobdRead(){
        try {
            socket = SerialPort.getCommPort("COM6");
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
                    System.out.println(cmd + " -> " + result);
                    return result;
                }
            }
            Thread.sleep(20);
        }

        throw new RuntimeException("Timeout waiting for response to " + cmd +
                ". Partial: " + response);
    }

    public static FloatProperty getBoost(){
        String formattedResult;
        float barometricValue = 0;
        float manifoldValue =0;
        try {
            BarometricPressureCommand beep = new BarometricPressureCommand();
            IntakeManifoldPressureCommand boop = new IntakeManifoldPressureCommand();
            beep.run(socket.getInputStream(), socket.getOutputStream());
            boop.run(socket.getInputStream(), socket.getOutputStream());
            manifoldValue = boop.getImperialUnit();
            formattedResult = beep.getFormattedResult();
            barometricValue = beep.getImperialUnit();
            System.out.println(formattedResult);
        } catch (Exception e) {
            System.out.println(e);
        }
        float boostPressure = manifoldValue-barometricValue;
        FloatProperty observableFloat = new SimpleFloatProperty();
        observableFloat.set(boostPressure);
        return observableFloat;
    }

    public static String getFuelTrim(){
        //returns fuel trim in format "stft" 
        FuelTrimCommand stft = new FuelTrimCommand(FuelTrim.SHORT_TERM_BANK_1);
        FuelTrimCommand ltft = new FuelTrimCommand(FuelTrim.LONG_TERM_BANK_1);
        String shortFuelTrim = stft.getFormattedResult();
        String longFuelTrim = ltft.getFormattedResult();//ignore for now
        return (shortFuelTrim+"");
    }

    public static String timingPosition(){
        TimingAdvanceCommand timing = new TimingAdvanceCommand();
        String timingDegrees = "";
        try {
        timing.run(socket.getInputStream(),socket.getOutputStream());
        timingDegrees = timing.getCalculatedResult();
        System.out.println(timingDegrees);
        timingDegrees = timing.getFormattedResult();
        } catch (Exception e) {
            System.out.println(e);
        }
        return timingDegrees;
    }
}
