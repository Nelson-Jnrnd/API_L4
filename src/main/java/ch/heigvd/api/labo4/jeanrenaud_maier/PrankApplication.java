package ch.heigvd.api.labo4.jeanrenaud_maier;

import ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client.Mail;
import ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client.MailSender;
import ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client.Person;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PrankApplication that sends fake emails to a number of victims
 * This class requires a config string in the JSON format
 * @see <a href="https://github.com/Nelson-Jnrnd/API_L4">https://github.com/Nelson-Jnrnd/API_L4</a>
 * @author Damien Maier
 * @author Nelson Jeanrenaud
 */
public class PrankApplication {

    private static final int MIN_GROUP_SIZE = 3;
    private static final Logger LOG = Logger.getLogger(PrankApplication.class.getName());
    private static final Random RANDOM_GENERATOR = new Random();

    private final Configs config;
    private final List<List<Person>> groups;
    private final List<Mail> mails;
    private final boolean isLogging;

    /**
     * Deserialize the JSON String and load it into the application
     *
     * @param filename JSON String containing the configs
     * @return the config structure of the data deserialized
     */
    public Configs readConfigs(String filename) {
        try {
            if (isLogging)
                LOG.log(Level.INFO,"Reading config file...");
            Gson gson = new Gson();

            BufferedReader reader = new BufferedReader(new FileReader(filename, StandardCharsets.UTF_8));
            JsonElement element = JsonParser.parseReader(reader);

            // Deserializing the different types of data
            JsonObject jsonObject = gson.fromJson(element, JsonObject.class);
            Messages messages = gson.fromJson(element, Messages.class);
            Victims victims = gson.fromJson(element, Victims.class);
            int nbGroups = jsonObject.getAsJsonPrimitive("nbGroups").getAsInt();
            String ipAddress = jsonObject.getAsJsonPrimitive("ip").getAsString();
            int noPort = jsonObject.getAsJsonPrimitive("port").getAsInt();

            // Error management
            if(messages == null){
                throw new IllegalArgumentException("messages format in config file is incorrect");
            } else if(victims == null){
                throw new IllegalArgumentException("victims format in config file is incorrect");
            }else if(victims.victims.size() < MIN_GROUP_SIZE){
                throw new IllegalArgumentException("Not enough victims, minimum " + MIN_GROUP_SIZE + " victims needed");
            }else if(nbGroups <= 0){
                throw new IllegalArgumentException("nbGroups format in config file is incorrect");
            }else if(noPort <= 0){
                throw new IllegalArgumentException("noPort format in config file is incorrect");
            } else if(ipAddress == null) {
                throw new IllegalArgumentException("ipAddress format in config file is incorrect");
            } else if(victims.victims.size() / MIN_GROUP_SIZE < nbGroups){
                throw new IllegalArgumentException("nbGroups value in config file is too high");
            }

            // Debug
            if(isLogging){
                for (Message message : messages.getMessages()) {
                    LOG.log(Level.INFO,"object : " + message.getObject() + "\ncontent : " + message.getContent());
                }
                for (Person victim : victims.getVictims()) {
                    LOG.log(Level.INFO,"name : " + victim.getName() + "\nadresse : " + victim.getMailAddress());
                }
                LOG.log(Level.INFO,"nbGroups : " + nbGroups);
                LOG.log(Level.INFO,"ipAddress : " + ipAddress);
                LOG.log(Level.INFO,"noPort : " + noPort);
            }

            reader.close();
            return new Configs(nbGroups, messages, victims, noPort, ipAddress);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage());
            System.exit(-1);
            return null;
        }
    }

    /**
     * Create nbGroups from the Person in the loaded config
     */
    private void createGroups() {
        if(isLogging)
            LOG.log(Level.INFO,"Creating groups...");
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
        if(isLogging)
            LOG.log(Level.INFO,"Creating mails...");
        if (config != null) {
            createGroups();
            List<Message> msg = config.getMessages().getMessages();
            for (List<Person> group : groups) {
                Person sender = null;
                Person[] recipient = new Person[group.size() - 1];

                // The sender is a random person in the group, everyone else is a recipient
                int idSender = RANDOM_GENERATOR.nextInt(group.size());
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
                    Message toSend = msg.get(RANDOM_GENERATOR.nextInt(msg.size())); // maybe changer
                    mails.add(new Mail(toSend.getContent(), sender, toSend.getObject(), recipient));
                }

            }
        }
    }

    /**
     * Send mails to the victims from the loaded config
     */
    public void sendMails() {
        createMails();
        if(isLogging)
            LOG.log(Level.INFO,"Sending mails...");
        try (MailSender mailSender = new MailSender(config.getIpAddress(), config.getNoPort())) {
            for (Mail mail : mails) {
                mailSender.sendMail(mail);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error while sending mails");
            System.exit(-1);
        }
    }

    /**
     *
     * @param configs config string to load into the application, this need to be in the JSON format.
     *                For more information, @see <a href="https://github.com/Nelson-Jnrnd/API_L4">https://github.com/Nelson-Jnrnd/API_L4</a>
     * @param logging the class will log the results, errors are always logged.
     */
    PrankApplication(String configs, boolean logging) {
        isLogging = logging;
        groups = new ArrayList<>();
        mails = new ArrayList<>();
        config = readConfigs(configs);
    }

    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");
        try {
            PrankApplication pa = new PrankApplication(args[0], true);
            //pa.createMails();
            pa.sendMails();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid number of parameters");
            System.exit(-1);
        }
        System.exit(0);
    }
}

class Configs {
    public Configs(int nbGroups, Messages messages, Victims victims, int noPort, String ipAddress) {
        this.nbGroups = nbGroups;
        this.messages = messages;
        this.victims = victims;
        this.noPort = noPort;
        this.ipAddress = ipAddress;
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

    public int getNoPort() {
        return noPort;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    private final int nbGroups;
    private final Messages messages;
    private final Victims victims;
    private final int noPort;
    private final String ipAddress;

}
