# API - L4 - SMTP

This project is a Java application made during the API course of the third semester of the Computer Science Bachelor at the HEIG-VD.

The application allows the user to send fake emails to a number of groups from a list of victim.

# Usage

To use this application it is needed to create a config file that will contain the following informations in the JSON format :

- IP address and port number of the SMTP server
- Name and the mail address of the victims (senders and recepients)
- Number of groups to create
- Content of the mails (objects and body)

## Exemple

```json
{
  "ip": "localhost",
  "port": 25,
  "victims": [
    {
      "name": "Alain Terieur",
      "mailAddress": "AlainTerieur@gmail.com"
    },
    {
      "name": "Alex Terieur",
      "mailAddress": "AlexTerieur@gmail.com"
    },
    {
      "name": "Sara pafromage",
      "mailAddress": "cheese@gmail.com"
    }
  ],
  "nbGroups": 1,
  "messages": [
    {
      "object": "My message",
      "content": "My message"
    },
    {
      "object": "Hello",
      "content": "Hi"
    }
  ]
}
```

Once you have the config file ready you can run the application with the following command :
```
java -jar target/API_L4-1.0-SNAPSHOT-launcher.jar config.json
```
You need to specify the json config file as parameter when you run the application.

## Using our class in your own Java project

Using our class within another project is very easy, you'll only have to create a new PrankApplication giving it the configs in a string format and whether you want the application to log the outputs.

Then you only need to use the sendMails method to launch your fake mail campaign !

If you ever need to change the configs of an already existing PrankApplication you can call the readConfigs method with the config string in argument.

# Goals

- The objective of this lab was to develop a Java TCP client which will use a partial implementation of the SMTP protocol to communicate with a SMTP server.
- Understand how and why use a mock server in the tests of client-server applications
- Train the usage of docker in a project

# MockMock

MockMock is a "fake" SMTP server. From the point of view of an SMTP client, it behaves like a normal SMTP server that allows to send e-mails. But it does not actually send any e-mail, it just displays the e-mails that would be sent if it was a real SMTP server.

You can use MockMock to test our application while being sure that no e-mail will actually be sent.

## Running MockMock

In this repo we provide the necessary tools to run a Docker container that runs a MockMock server.

For this you will need to install Docker (you can find installation instructions for your OS [here](https://docs.docker.com/get-docker/)).

To run the Docker container, if you use Linux, you just have to open a terminal in the "docker" directory of this repo and type the command

```bash
chmod +x runMockMock.sh
./runMockMock.sh
```

This will automatically configure and build the docker image and start a new container based on it.

Alternatively, you can open a terminal in the "docker" directory of this repo and type the following command to build the image manually :

```bash
docker build --tag maierjeanrenaud/apil4 . 
```

Then you can run the image with :

```bash
run -p 8282:8282 -p 25:25 maierjeanrenaud/apil4
```

## Using MockMock to test our application

Once you have launched the Docker container, MockMock will wait for SMTP connections on port 25 of your localhost. You just need to make sure that the json config file of the prank application has the "ip" parameter set to "localhost" and the "port" parameter set to 25. Then run the prank application to make it send the mails to MockMock.

Then you can access the MockMock web interface by opening a web browser and typing [localhost](http://localhost):8282 in the address bar. Here you can see the mails received by MockMock.

# Our implementation

## Class Diagram

![uml.png](API%20-%20L4%20-%20SMTP%20ba8be2a426a04c1ba896a85a9493fc4e/uml.png)

## Packages

We decided to separate this project in packages to offer greater reusability to parts of the code, notably the SMTP client.

## SMTP_Client package

As said above, this package contains everything needed to send mails to an SMTP server.

### MailSender

This class implements a simple client able to send e-mails with SMTP using the commands described in RFC 5321.

When an instance of this class is created, it opens a TCP connection to the server and starts the SMTP communication by reading the server initial message and sending the EHLO command.

![Example of a TCP connection initiation from our application](API%20-%20L4%20-%20SMTP%20ba8be2a426a04c1ba896a85a9493fc4e/Untitled.png)

Example of a TCP connection initiation from our application

To send an e-mail, the client uses the MAIL FROM, RCPT TO and DATA commands with appropriate parameters. It generates and sends the headers for the message and then it sends the message body.

![Example a of messages exchanged between the server and the client when our application sends an e-mail](API%20-%20L4%20-%20SMTP%20ba8be2a426a04c1ba896a85a9493fc4e/Untitled%201.png)

Example a of messages exchanged between the server and the client when our application sends an e-mail

To allow correct displaying of non ASCII parameters in the "subject" header, the client uses an appropriate encoding as specified in RFC 2047. To allow correct displaying of non ASCII parameters in the message body, it encodes them with UTF-8 and uses an appropriate message header as specified in RFC 2045.

Each time the client sends a commend, it checks that the server replies as expected. If an unexpected reply is received from the server, the client simply throws an exception.

### ServerReplyCodeScanner

This class is a simple parser that identifies the reply codes sent by the server. It can process both single line and multi line server replies.

## PrankApplication

This class is the main application of the project, it contains the methods to read the config files, create the groups and uses a `MailSender` to send emails to the created groups.

### Configs

The prank application uses a config class as a structure to contain all the data fetched from the config file.

The configs are read in the `readConfigs` method which is itself called automatically at the start of the application

Then the JSON configs are deserialized using the [GSON library](https://github.com/google/gson) and if the data read are usable the configs object is populated.

### Json models

To deserialize the JSON objects correctly, we are using model classes for the messages and the victims.

### Group creation

To create the groups, the application will randomize the list of all the persons. Then it'll create `nbGroups` groups of the maximum size possible. If there's not enough persons to create a full group, they'll all be assigned to an existing group.

### Mail creation

For each groups, we use a random number generator to pick a message from the configs and a person from this group to be the sender, all the other persons are recepient of this email.
