package ch.heigvd.api.labo4.jeanrenaud_maier;

import ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class PrankApplication {

    public static void main(String[] args) {
        try {
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(args[0]));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Invalid number of parameters");
            System.exit(-1);
        }
        System.exit(0);
    }
}
