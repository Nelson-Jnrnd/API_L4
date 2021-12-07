package ch.heigvd.api.labo4.jeanrenaud_maier;

import ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client.Mail;
import ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client.MailSender;
import ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client.Person;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PrankApplication {

    private static final int MIN_GROUP_SIZE = 3;

    private final Configs config;
    private static final Random randomGenerator = new Random();


    private final List<List<Person>> groups;
    private final List<Mail> mails;

    /**
     * Deserialize the JSON String and load it into the application
     *
     * @param filename JSON String containing the configs
     * @return the config structure of the data deserialized
     */
    private Configs readConfigs(String filename) {
        try {
            Gson gson = new Gson();

            // Changer en bufferedStream
            FileReader reader = new FileReader(filename, StandardCharsets.UTF_8);
            JsonElement element = JsonParser.parseReader(reader);

            // Deserializing the different types of data
            Messages messages = gson.fromJson(element, Messages.class);
            Victims victims = gson.fromJson(element, Victims.class);
            int nbGroups = gson.fromJson(element, JsonObject.class).getAsJsonPrimitive("nbGroups").getAsInt();

            // Debug
            if (messages != null) {
                for (Message message : messages.getMessages()) {
                    System.out.println("object : " + message.getObject() + "\ncontent : " + message.getContent());
                }

            }
            if (victims != null && victims.victims.size() >= MIN_GROUP_SIZE) {
                for (Person victim : victims.getVictims()) {
                    System.out.println("name : " + victim.getName() + "\nadresse : " + victim.getMailAddress());
                }
                // Adjust the number of groups given to have correct group sizes
                if (victims.victims.size() / MIN_GROUP_SIZE < nbGroups) {
                    nbGroups = victims.victims.size() / MIN_GROUP_SIZE;
                }
            } else {
                throw new IllegalArgumentException("Not enough victims, minimum " + MIN_GROUP_SIZE + " victims needed");
            }
            System.out.println("nbGroups : " + nbGroups);
            reader.close();

            return new Configs(nbGroups, messages, victims);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
            return null;
        }
    }

    /**
     * Create nbGroups from the Person in the loaded config
     */
    private void createGroups() {
        if (config != null) {
            // Index on the first person of a group
            int indexGroupDebut = 0;
            // Index on the last person of the group + 1
            int indexGroupFin = config.getGroupSize();
            List<Person> victimsList = config.getVictims().victims;
            // Randomizing the order of the victims to have different groups in successive runs
            Collections.shuffle(victimsList);
            for (int idGroup = 0; idGroup < config.getNbGroups(); idGroup++) {
                // We create a group from the list of persons between the first and the last person of the group
                groups.add(victimsList.subList(indexGroupDebut, indexGroupFin));
                // Adjust the indexes
                indexGroupDebut = indexGroupFin;
                indexGroupFin += config.getGroupSize();
                // If we don't have enough people to make a full size group after, we include them in the next group
                if (indexGroupFin + MIN_GROUP_SIZE > victimsList.size()) {
                    indexGroupFin = victimsList.size();
                }
            }
        }
    }

    /**
     * Create mails from the messages and groups in the config files
     */
    private void createMails() {
        if (config != null) {
            createGroups();
            List<Message> msg = config.getMessages().getMessages();
            for (List<Person> group : groups) {
                Person sender = null;
                Person[] recipient = new Person[group.size() - 1];

                // The sender is a random person in the group, everyone else is a recipient
                int idSender = randomGenerator.nextInt(group.size());
                int idPerson = 0;
                int idRecipient = 0;
                for (Person p : group) {
                    if (idPerson++ == idSender) {
                        sender = p;
                    } else {
                        recipient[idRecipient++] = p;
                    }
                }
                // We pick a message from the list to assign to the group
                if (sender != null) {
                    Message toSend = msg.get(randomGenerator.nextInt(msg.size())); // maybe changer
                    mails.add(new Mail(toSend.getContent(), sender, toSend.getObject(), recipient));
                }

            }
        }
    }

    private void sendMails() {
        try (MailSender mailSender = new MailSender("localhost", 25)) {
            for (Mail mail : mails) {
                mailSender.sendMail(mail);
            }
        } catch (Exception e) {
            System.err.println("Error while sending mails");
            System.exit(-1);
        }
    }

    PrankApplication(String configs) {
        groups = new ArrayList<>();
        mails = new ArrayList<>();
        config = readConfigs(configs);
    }

    public static void main(String[] args) {
        try {
            PrankApplication pa = new PrankApplication(args[0]);
            pa.createMails();
            pa.sendMails();
        } catch (ArrayIndexOutOfBoundsException e) {
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
