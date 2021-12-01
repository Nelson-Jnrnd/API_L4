package ch.heigvd.api.labo4.jeanrenaud_maier;

import ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client.Mail;
import ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client.Person;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PrankApplication {

    public Configs getConfig() {
        return config;
    }

    private static final int MIN_GROUP_SIZE = 3;

    private final Configs config;
    private static final Random randomGenerator = new Random();


    private List<List<Person>> groups;
    private List<Mail> mails;
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
            if(victims != null && victims.victims.size() >= MIN_GROUP_SIZE) {
                for (Person victim : victims.getVictims()) {
                    System.out.println("name : " + victim.getName() + "\nadresse : " + victim.getMailAdress());
                }
                if(victims.victims.size() / MIN_GROUP_SIZE < nbGroups){
                    nbGroups = victims.victims.size() / MIN_GROUP_SIZE;
                }
            } else{
                throw new IllegalArgumentException("Not enough victims, minimum " + MIN_GROUP_SIZE + " victims needed");
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

    private void createGroups(){
        if(config != null){
            int indexGroupDebut = 0;
            int indexGroupFin = config.getGroupSize();
            for (int idGroup = 0; idGroup < config.getNbGroups(); idGroup++) {
                groups.add(config.getVictims().victims.subList(indexGroupDebut, indexGroupFin));
                indexGroupDebut = indexGroupFin;
                indexGroupFin += config.getGroupSize();
                if(indexGroupFin + MIN_GROUP_SIZE > config.getVictims().victims.size()){
                    indexGroupFin = config.getVictims().victims.size();
                }
            }
        }
    }
   public void createMails(){
        if(config != null){
            if(groups == null || groups.isEmpty()){
                createGroups();
            }
            List<Message> msg = config.getMessages().getMessages();
            for (List<Person> group : groups) {
                Person sender = null;
                Person[] recepient = new Person[group.size() - 1];

                int idSender = randomGenerator.nextInt(group.size());
                int idPerson = 0;
                int idRecepient = 0;
                for (Person p: group) {
                    if(idPerson++ == idSender){
                        sender = p;
                    } else{
                        recepient[idRecepient++] = p;
                    }
                }
                if(sender != null){
                    Message toSend = msg.get(randomGenerator.nextInt(msg.size())); // maybe changer
                    mails.add(new Mail(toSend.getContent(), sender, toSend.getObject(), recepient));
                }

            }
        }
    }
    PrankApplication(String configs){
        groups = new ArrayList<>();
        mails = new ArrayList<>();
        config = readConfigs(configs);
    }

    public static void main(String[] args) {
        try{
            PrankApplication pa = new PrankApplication(args[0]);
            pa.createMails();
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

    public int getGroupSize() {
        return victims.victims.size() / nbGroups;
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
