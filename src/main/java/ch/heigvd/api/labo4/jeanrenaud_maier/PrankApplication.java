package ch.heigvd.api.labo4.jeanrenaud_maier;

import ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client.Person;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;

public class PrankApplication {

    private final Configs config;
    private Configs readConfigs(String filename){
        try {
            Gson gson = new Gson();

            // Changer en bufferedSttream
            FileReader reader = new FileReader(filename);
            JsonElement element = JsonParser.parseReader(reader);

            // Deserialize
            Messages messages = gson.fromJson(element, Messages.class);
            Victims victims = gson.fromJson(element, Victims.class);
            int nbGroups = gson.fromJson(element, JsonObject.class).getAsJsonPrimitive("nbGroups").getAsInt();

            // Debug
            if(messages != null) {
                for (Message message : messages.getMessages()) {
                    System.out.println("object : " + message.getObject() + "\ncontent : " + message.getContent());
                }
            }
            if(victims != null) {
                for (Person victim : victims.getVictims()) {
                    System.out.println("name : " + victim.getName() + "\nadresse : " + victim.getMailAdress());
                }
            }
            System.out.println("nbGroups : " + nbGroups);
            reader.close();

            return new Configs(nbGroups, messages, victims);

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
            return null;
        }
    }

    PrankApplication(String configs){
        config = readConfigs(configs);
    }

    public static void main(String[] args) {
        try{
            PrankApplication pa = new PrankApplication(args[0]);
        }catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid number of parameters");
            System.exit(-1);
        }

        System.exit(0);
    }
}

class Configs {
    public Configs(int nbGroups, Messages messages, Victims victims) {
        this.nbGroups = nbGroups;
        this.messages = messages;
        this.victims = victims;
    }

    public int getNbGroups() {
        return nbGroups;
    }

    public Messages getMessages() {
        return messages;
    }

    public Victims getVictims() {
        return victims;
    }

    private final int nbGroups;
    private final Messages messages;
    private final Victims victims;
}
