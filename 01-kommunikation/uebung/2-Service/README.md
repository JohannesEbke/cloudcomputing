# Übung "Kommunikation" Teil 2

Ziel dieser Übung ist es ...

## Vorbereitung

1. Zunächst muss ein Anwendungsrumpf für den Microservice und das REST API erstellt werden. Wir verwenden hierfür den Spring Boot Initializr. Rufen sie hierfür die folgende URL auf: https://start.spring.io
1. Passen Sie die Projekt-Metadaten nach Ihren Bedürfnissen an. Wählen Sie Java als Sprache. (Empfehlung: wählen Sie ein Gradle - Groovy Projekt)
1. Fügen sie die folgenden Dependencies hinzu: `Spring Web`

Ein Getting Started finden sie hier: https://spring.io/guides/gs/spring-boot/

# Aufgaben

Wir möchten nun die Anbindung die Lieferschnittstelle aus dem Praktikumsprojekt anbinden. 

(1) Initiale Anwendungslogik erstellen:
(1.1) Sie benötigen zusätzliche Abhängigkeiten (Dependencies). Fügen Sie diese in Ihrer `build.gradle` hinzu 

```groovy
	implementation 'software.amazon.awssdk:sns:2.30.28'
	implementation 'software.amazon.awssdk:sts:2.30.28'
	implementation 'software.amazon.awssdk:sqs:2.30.28'
```


(1.2) Implementieren Sie eine Listener-Klasse welche Nachrichten aus Ihrer AWS SQS liest und nach dem Lesen wieder löscht. 

*Hinweis* 
Ihnen ist bestimmt aufgefallen, dass Sie hier bisher keine AWS Zugangsdaten hinterlegt haben. Das AWS SDK funktioniert
hier in dieser Hinsicht wie die AWS CLI. Wenn sie die Umgebung Ihres Services richtig konfiguriert haben (Datei basierte 
Konfiguration oder Umgebungsvariablen), wird das SDK die Zugangsdaten automatisch korrekt verwenden. 

```java
@Configuration
public class AwsConfig {

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .region(Region.EU_CENTRAL_1)
                .build();
    }
}

```

```java
@Component
public class SqsListener {

    private final SqsClient sqsClient;
    private final String queueUrl = "your-queue-url";

    @Autowired
    public SqsListener(SqsClient sqsClient) {
      this.sqsClient = sqsClient;
    }

    @PostConstruct
    public void subscribeToQueue() {
        System.out.println("Subscribing to to queue");
        new Thread(() -> {
            while (true) {
                System.out.println("Trying to receive message");
                ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .maxNumberOfMessages(5)
                        .waitTimeSeconds(10)
                        .build();

                List<Message> messages = sqsClient.receiveMessage(request).messages();
                for (Message message : messages) {
                    System.out.println("Received message: " + message.body());
                    // TODO: Delete the message with the DeleteMessageRequest
                }
            }
        }).start();
    }
}
```

(1.3) Starten Sie doch mal Ihren Service und legen Sie ein paar Nachrichten in Ihre AWS SQS. 

## Kür: Nachrichten an die Queue senden.

*Hinweis* Sie können das Versenden von Nachrichten direkt an Ihre AWS SQS später für Testzwecke verwenden. 
Das eigentliche Ziel ist es natürlich am Ende das SNS Topic `arn:aws:sns:eu-central-1:941377120628:foobar-gmbh-lagerhaltung-anfragen`
anzubinden. 

* Vielleicht malen Sie sich nochmal eine Skizze, oder erweitern ein bereits von Ihnen erstelltes.
   * Wie verläuft die Kommunikation, wenn sie händisch eine Nachricht in die Queue legen?
   * Wie verläuft die Kommunikation, wenn sie eine Nachricht an das "lagerhaltung-anfragen"-Topic senden?
   * Vergleichen Sie die Skizze mit Ihren Kommilitonen.

```java
@RestController
@RequestMapping("/webshop")
public class ShopController {
    
    private final SqsService sqsService;

    @Autowired
    public ShopController(SqsService sqsService) {
        this.sqsService = sqsService;
    }

    @PostMapping("/publish")
    public String sendMessage(@RequestParam String message)  {
        // implement method.
    }
}
```

```java
@Service
public class SqsService {

    private final SqsClient sqsClient;
    private final String queueUrl = "";

    @Autowired
    public SqsService(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }
    
    public String publishMessage(String message) {
        // ... implement 
    }
}
```

```shell
curl -X POST "http://localhost:8080/sns/publish?message=CloudComputingRocks"
```

##