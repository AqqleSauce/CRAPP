package com.svgs;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.github.pires.obd.commands.fuel.FuelTrimCommand;
import com.github.pires.obd.commands.pressure.BarometricPressureCommand;
import com.github.pires.obd.commands.pressure.IntakeManifoldPressureCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.enums.FuelTrim;
import com.github.pires.obd.enums.ObdProtocols;

public class ObdReader {
    public static Socket socket;
    public static InputStream inputStream;
    public static OutputStream outputStream;

    public static void startobdRead(){
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            new EchoOffCommand().run(inputStream, outputStream);
            new LineFeedOffCommand().run(inputStream, outputStream);
            new SelectProtocolCommand(ObdProtocols.AUTO).run(inputStream, outputStream);
            //starts the connection

            System.out.println("Connected yo");
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e);
        }
    }

    public static float  getBoost(){
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
        return boostPressure;
    }

    public static String getFuelTrim(){
        //returns fuel trim in format "stft/ltft" 
        FuelTrimCommand stft = new FuelTrimCommand(FuelTrim.SHORT_TERM_BANK_1);
        FuelTrimCommand ltft = new FuelTrimCommand(FuelTrim.LONG_TERM_BANK_1);
        String shortFuelTrim = stft.getFormattedResult();
        String longFuelTrim = ltft.getFormattedResult();
        return (shortFuelTrim+"/"+longFuelTrim);
    }

}
