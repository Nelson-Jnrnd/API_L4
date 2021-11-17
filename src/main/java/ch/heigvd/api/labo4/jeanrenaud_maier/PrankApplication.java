package ch.heigvd.api.labo4.jeanrenaud_maier;

import ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PrankApplication {

    public static void main(String[] args) {
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader(args[0]);
            //Person victim = gson.fromJson(reader, Person.class);

           /* Map<String, Victims> victims = gson.fromJson(reader,
                    new TypeToken<Map<String, Victims>>() {
                    }.getType());*/
            Victims victims = gson.fromJson(reader, Victims.class);
            for (Person victim : victims.getVictims()) {
                System.out.println("name : " + victim.getName() + "\nadresse : " + victim.getMailAdress());
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
