package com.svgs;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SaveManager {
    private static final Path saveDirectory = Paths.get("saves");
    private static BufferedWriter writer;
    
    public static void startDrive() throws IOException{
        Files.createDirectories(saveDirectory);
        String timeDate = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
         // got this cool way to get time stamps from stack overflow, it names saves by the timestamp they started at.
        Path file = saveDirectory.resolve("drive-"+timeDate+".csv");
        writer = Files.newBufferedWriter(file);
        writer.write("timeMillis,boost,rpm,fuelTrim,fuelPressure,coolant,load,speed,throttle,timing");
        writer.newLine();
    }

    public static void recordValues(){
        if (writer == null){
            return;
        }
        
        try {
            writer.write(System.currentTimeMillis()+",");
            writer.write(ObdReader.boostProperty().get()+","+
            ObdReader.revProperty().get()+","+
            ObdReader.trimProperty().get()+","+
            ObdReader.fuelPressureProperty().get()+","+
            ObdReader.coolantProperty().get()+","+
            ObdReader.loadProperty().get()+","+
            ObdReader.speedProperty().get()+","+
            ObdReader.throttleProperty().get()+","+
            ObdReader.timingProperty().get());
            writer.newLine();
        } catch (Exception e) {
            System.out.println(e);
        }
        
    }

    public static void stopRecord(){
        try {
            if (writer != null){
            writer.flush();
            writer.close();
            writer = null;
        }
        } catch (Exception e) {
            System.out.println(e);
        }
        
    }
}
