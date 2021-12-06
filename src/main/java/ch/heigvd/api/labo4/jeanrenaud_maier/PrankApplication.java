package ch.heigvd.api.labo4.jeanrenaud_maier;

import ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client.*;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;

public class PrankApplication {

    public static void main(String[] args) {
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader(args[0]);
            Victims victims = gson.fromJson(reader, Victims.class);
            for (Person victim : victims.getVictims()) {
                System.out.println("name : " + victim.getName() + "\nadresse : " + victim.getMailAddress());
            }
            reader.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid number of parameters");
            System.exit(-1);
        }
        System.exit(0);
    }
}
