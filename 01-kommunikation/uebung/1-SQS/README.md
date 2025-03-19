# Übung "Kommunikation"

Ziel der Übung heute ist es, eine [SQS](https://docs.aws.amazon.com/sqs/) aufzusetzen, die sich auf eine bereits existierendes SNS Topic abonnieren wird.

## Vorbereitung

Diese Übung benötigt Zugriff auf AWS mit vorhandenen Zugangsdaten sowie eine installierte AWS CLI (Version 2).

## Übung

Machen Sie sich zunächst mit dem Cloud-Computing-Praktikumsprojekt vertraut, insbesondere mit dem Abschnitt „Hinweise und Hilfestellungen“. 
Im Abschnitt „Lieferschnittstelle“ gibt es zwei [AWS SNS](https://docs.aws.amazon.com/sns/latest/dg/sns-getting-started.html)-Topics:
Eines für Anfragen zur Lagerhaltung und ein weiteres für die Antworten.

1. Wie sieht die Kommunikation hier aus? Wie unterscheidet sie sich von einer klassischen Request/Response-Kommunikation?
1. Vielleicht hilft es Ihnen, sich eine Skizze zu zeichnen?

*Hinweis* Was ist ein Amazon Resource Name (ARN)?
Eine Amazon Resource Name (ARN) ist ein eindeutiger Bezeichner (Identifier) für AWS-Ressourcen. Er besteht aus mehreren Teilen, zum Beispiel:
`arn:aws:sns:eu-central-1:941377120628:foobar-gmbh-lagerhaltung-antworten`
* `arn:aws`: – legt fest, dass es sich um eine ARN im AWS-Kontext handelt.
* `sns`: – bezeichnet den AWS-Dienst, hier Simple Notification Service.
* `eu-central-1`: – die Region, in der die Ressource liegt.
* `941377120628`: – die Konto-ID, der die Ressource gehört.
* `foobar-gmbh-lagerhaltung-antworten` – der Name der Ressource (hier das SNS-Topic).

### AWS CLI Authentication

Sie müssen der AWS CLI ihre Zugangsdaten bereitstellen, dafür gibt es mehrere Wege: 

* Der einfachste Weg ist mit: `aws configure` - hier können sie interaktiv ihre Zugangsdaten eingeben. Wir sind in `eu-central-1`. 
  * Sehen Sie sich die angelegten Konfigurationen an:  
    * Windows `%userprofile%\.aws\`
    * Linux `$HOME\.aws\`
    * Mac `$HOME\.aws\`
* Sie können es auch [Umgebungsvariablen](https://docs.aws.amazon.com/cli/v1/userguide/cli-configure-envvars.html) verwenden
* Sie können ihre Zugangsdaten in Dateien hinterlegen: [AWS CLI anzumelden](https://docs.aws.amazon.com/cli/v1/userguide/cli-configure-files.html) *Hinweis* aws configure erledigt genau das. 

Testen Sie, ob die AWS CLI ihre Zugangsdaten findet: 
`aws sts get-caller-identity`

### Erstellung der SQS 

*Hinweis* Die AWS CLI ist gut dokumentiert. Häufig finden Sie dort auch Beispiele unter `help` am Ende der Dokumentation. 

1. Schauen Sie sich die Hilfe für den Befehl `create-queue` an: `aws sqs create-queue help`
1. Erstellen Sie eine neue Queue und verwenden Sie einen sinnvollen Namen: `aws sqs create-queue --queue-name <name>`
1. Notieren Sie sich die Queue-URL.


### Nachrichten senden, empfangen und auch löschen

1. Senden Sie ein paar **unterschiedliche** Nachrichten `aws sqs send-message --queue-url "<Ihre-Queue-Url>" --message-body "Hallo Cloud Computing"`
1. Empfangen Sie alle Nachrichten `aws sqs receive-message  --queue-url <Ihre-Queue-Url>`
1. Was passiert? Und warum passiert das?
1. Löschen Sie eine Nachricht. `aws sqs delete-message`
1. Löschen Sie die restlichen Nachrichten. 

### Berechtigungen 

Zukünftig soll das AWS-SNS-Topic `arn:aws:sns:eu-central-1:941377120628:foobar-gmbh-lagerhaltung-antworten` Nachrichten an die Queue übergeben dürfen.
Dafür müssen Sie eine sogenannte Richtlinie (Policy) setzen. Mit der AWS CLI erledigen Sie das über den Befehl: `aws sqs set-queue-attributes`
1. Speichern Sie die Policy auf Ihre Festplatte und setzen Sie die korrekten Werte ein. 
    * Ersetzen Sie <account-id> mit der AWS Account ihres Zugangs. 
    * Ersetzen Sie <name-ihrer-queue> mit dem Namen ihrer Queue. 
```json
{
  "Policy": "{\"Version\":\"2012-10-17\",\"Id\":\"__default_policy_ID\",\"Statement\":[{\"Sid\":\"__owner_statement\",\"Effect\":\"Allow\",\"Principal\":{\"AWS\":\"arn:aws:iam::<account-id>:root\"},\"Action\":\"SQS:*\",\"Resource\":\"arn:aws:sqs:eu-central-1:<account-id>:<name-ihrer-queue>\"},{\"Effect\":\"Allow\",\"Principal\":{\"Service\":\"sns.amazonaws.com\"},\"Action\":\"sqs:SendMessage\",\"Resource\":\"arn:aws:sqs:eu-central-1:<account-id>:<name-ihrer-queue>\",\"Condition\":{\"ArnEquals\":{\"aws:SourceArn\":\"arn:aws:sns:eu-central-1:941377120628:foobar-gmbh-lagerhaltung-antworten\"}}}]}"
}
```
2. Setzen Sie die Queue-Attribute `aws sqs set-queue-attributes --queue-url <Ihre-Queue-Url> --attributes file://set-queue-attributes.json`
3. Lesen Sie die Attribute doch mal aus `aws sqs get-queue-attributes --queue-url <Ihre-Queue-Url> --attribute-names All`

### Abonnieren 

Sie sind jetzt in der Lage auf ihrer Queue Nachrichten zu empfangen, lesen und zu löschen. 

1. Abonnieren Sie das Topic `arn:aws:sns:eu-central-1:941377120628:foobar-gmbh-lagerhaltung-antworten`
2. Was müssen Sie hier eintragen? Vielleicht hilft Ihnen `aws sns subscribe help`
```shell
aws sns subscribe \
  --topic-arn <queue-arn> \ 
  --protocol <welches Protokol?> \
  --notification-endpoint <welcher endpoint>
```
