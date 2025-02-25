<!-- markdownlint-disable MD033 -->

# Übung: IaaS mit AWS

## Ziel

Ziel dieser Übung ist, dass Sie ein grundsätzliches Verständnis der AWS Cloud und ihrer grundlegendsten IaaS Komponenten erlangen. Ihre Aufgabe ist es ein virtuelles Netz, eine Auto Scaling Group mit mehreren Maschinen und einen Load Balancer zu erzeugen um eine minimale Webanwendung bereitzustellen. Im Anschluss werden Sie die AWS CLI verwenden um Ihre Anwendung zu skalieren und per SSH zu verändern.

## Voraussetzungen

Im zweiten Teil dieser Übung verwenden wir einen Docker Container zur Verwendung der CLI. Daher brauchen Sie eine eine lokale Docker Installation. Da die Erstellung / der Download des Containers länger dauern kann starten Sie bitte mit der Vorbereitung für Übung zwei.

Sie benötigen einen AWS Account um die Übung zu absolvieren.
Wenn Sie awseducate.com verwenden, loggen Sie sich auf der Webseite ein und klicken Sie auf AWS Account und erzeugen Sie einen [Educate Starter Account](https://www.geeksforgeeks.org/aws-educate-starter-account/).

## Aufgaben

### Erzeugen einer AWS Infrastruktur per UI

In dieser Übung erzeugen Sie eine einfache IaaS Architektur mit der AWS Web Konsole.

Hinweise:

* Folgen Sie der Anleitung, sofern nicht anders genannt, belassen Sie andere Einstellungen unverändert.
* Stellen Sie sicher, dass die AWS Region _eu-central-1 (Frankfurt)_ (rechter, oberer Bildschirmrand) ist und die Sprache auf _English_ (linker, unterer Bildschirmrand) steht.
Falls nach der Anmeldung die Sprache weiterhin Deutsch ist, melden Sie sich wieder ab und wählen Sie unterhalb der Login-Maske die Sprache Englisch aus und melden Sie sich erneut an.
* Wichtig wenn Sie Sich einen Account mit anderen Übungsteilnehmern teilen: Denken Sie sich einen eindeutigen Namen für die Benennung/Tags Ihrer Ressourcen aus.
Da alle Teilnehmer den gleichen Account verwenden werden ist davon auszugehen, dass wenn Sie Ihre Ressourcen nicht wiedererkennbar bennenen Sie und Ihre Kommolitonen Schwierigkeiten haben werden Ihre Ressourcen wiederzufinden.
Sie können beispielsweise eine Kombination aus Ihrem echten Namen verwenden z.B. `akowasch` wenn ihr Name _Andreas Kowasch_ ist oder Sie wählen einen beliebigen anderen Namen wie z.B. `anonymeameise`.
Verwenden Sie bei der Namenswahl keine Sonderzeichen außer `-` und vermeiden Sie Umlaute.

#### Ein VPC erstellen

In diesem Teil der Übung erstellen Sie ein virtuelles Netzwerk, mit einem einzelnen Subnetz mit Internet Zugriff.

1. Klicken Sie in der Menubar auf _Services_, wählen Sie _VPC_ > _Your VPCs_ > _Create VPC_.
    * Vergeben Sie einen eindeutigen Namen, mit dem Sie ihr Netzwerk wiederfinden z.B. _akrause_.
    * Verwenden Sie den IPV4 Adressbereich `10.0.0.0/16`.
    * Klicken Sie auf _Create VPC_.
      Nach dem Erstellen werden Sie feststellen, dass über das Netz hinaus weitere Standard Cloud Ressourcen angelegt wurden z.B. eine Routing Tabelle.

2.
    * a) Klicken Sie in der Seitenleiste auf _Subnets_, wähle Sie _Create subnet_.
        * Wählen Sie das von Ihnen erstellte VPC als Ziel für ihr Subnetz.
        * Verwenden Sie wieder Ihren eindeutigen Namen.
        * Wählen Sie als _Availability Zone_ die Zone _eu-central-1a_.
        * Wählen Sie den Adressbereich kleiner als den Ihres VPC z.B. die Hälfte: `10.0.0.0/17`.
        * Klicken Sie auf _Create subnet_.
        * Nach der Erstellung, markieren Sie ihr Netz und klicken Sie nun bei _Actions_ die Option _Edit subnet settings_ und aktivieren Sie den Haken bei _Enable auto-assign public IPv4 address_.
    * b) Wiederholen Sie die Schritte in 2a um ein Netz in _eu-central-1b_ mit dem Adressbereich `10.0.128.0/17` zu erzeugen.

3. Erzeugen Sie nun einen Zugang zum Internet indem Sie in der Seitenleiste auf _Internet gateways_ klicken und dann auf _Create internet gateway_ Klicken.
    * Verwenden Sie wieder Ihren eindeutigen Namen.
    * Nach dem Erstellen klicken Sie oben rechts auf _Actions_ und wählen Sie _Attac to VPC_.
    * Wählen Sie ihr VPC und bestätigen Sie.

4. Klicken Sie nun in der Seitenleiste auf _Route tables_ und fügen Sie an die Tabelle für Ihr VPC eine weitere Regel ein, die allen Traffic (`0.0.0.0/0`) zu dem von Ihnen erstellten Internet Gateway routed.
   Die Reihenfolge der Regeln ist hierbei irrelevant, spezifischere greifen zuerst.

#### Einen Load Balancer erstellen

In diesem Teil der Übung erstellen Sie einen Load Balancer der die Anfragen über die aktuell service-fähigen Instanzen verteilt.

1. Wechseln Sie nun zum Service _EC2_.
   Klicken Sie in der Seitenleiste auf _Security Groups_ und dann auf _Create security group_.
    * Benennen Sie die Gruppe nach dem Schema `loadbalancer-<Ihr Eindeutiger Name>`.
    * Als Beschreibung ist _"Security Group fuer den Load Balancer."_ geeignet.
    * Unter _VPC_ Wählen Sie ihr VPC aus.
    * Fügen Sie eine Inbound Rule ein, die den Zugriff per Port `80`, also HTTP, von `0.0.0.0/0`, d.h. beliebiger IP, erlaubt.
    * Klicken Sie auf _Create security group_.

2. Klicken Sie auf der Seitenleiste auf _Load Balancers_ und wählen Sie _Create Load Balancer_.
    * Wählen Sie den Typ _Application Load Balancer_.
    * Verwenden Sie wieder Ihren eindeutigen Namen.
    * Wählen Sie unter _Network mapping_ Ihr VPC und Ihre Subnetze aus.
    * Wählen Sie unter _Security groups_ nur Ihre `loadbalancer-<Ihre Eindeutiger Name>` Security Group aus.
    * Erstellen Sie unter _Listeners and routing_ eine neue Target Group und verwenden Sie dafür wieder Ihren eindeutigen Namen.
      * Unter _Basic configuration_
        * Wählen Sie den Typ `Instances`.
        * Wählen Sie das Protokoll `HTTP`.
        * Wählen Sie den Port `8080`.
      * Unter _Health checks_
        * Wählen Sie das Protokol `HTTP`.
        * Wählen Sie den Pfad `/`.
      * Unter _Advanced health check settings_
        * Wählen Sie den Port `Traffic port`.
        * Setzen Sie den _Healthy threshold_ auf `2`.
        * Setzen Sie den _Unhealthy threshold_ auf `2`.
        * Setzen Sie das _Interval_ auf `10 seconds`.
      * Es ist nicht notwendig _Targets_ zu registrieren, da dies die Auto Scaling Group übernimmt.
      * Schließen Sie den Vorgang ab, indem sie auf _Create target group_ klicken.
    * Wählen Sie die zuvor erstellte Target Group aus.
    * Klicken Sie abschließend auf _Create load balancer_.

#### Eine Auto Scaling Group einrichten

In diesem Teil der Übung erstellen Sie eine Auto Scaling Group, welche Ihnen erlaubt Ihre Instanzen bequem zu skalieren und auszutauschen.
Darüber hinaus meldet Ihre Auto Scaling Group Ihre Instanzen bei der Target Group des Load Balancers an.

1. Erstellen Sie eine weitere Security Group.
    * Benennen Sie die Gruppe nach dem Schema `app-<Ihr Eindeutiger Name>`.
    * Als Beschreibung ist _"Security Group fuer die Applikationsserver."_ geeignet.
    * Unter _VPC_ Wählen Sie ihr VPC aus.
    * Fügen Sie eine Inbound Rule ein, die den Zugriff per Port `22`, also SSH, von `0.0.0.0/0`, d.h. beliebiger IP, erlaubt.
    * Fügen Sie eine weitere Inbound Rule ein, die den Zugriff per Port `8080`, von der Security Group des Load Balancers erlaubt.
    * Klicken Sie auf _Create security group_.

2. Klicken Sie in der Seitenleiste auf den Eintrag _Launch Templates_ > _Create Launch Template_.
    * Verwenden Sie wieder Ihren eindeutigen Namen.
    * Als Beschreibung ist "Launch Template fuer eine einfache Web Anwendung." geeignet.
    * Wählen Sie unter _Application and OS Images_ ein Amazon Linux 2023 als _AMI_. Zum Beispiel `ami-07cb013c9ecc583f0`.
    * Wählen Sie unter _Instance type_ `t2.micro`.
    * Wählen Sie unter _Network settings_ Ihre Security Group für die Applikation.
    * Verwenden Sie unter _Advanced details_ das folgende Skript als _User Data_:

      ``` shell
      #!/bin/bash
      set -euxo pipefail
      
      dnf update -y
      
      dnf install -y cowsay
      dnf install -y nginx
      dnf clean all
      
      cat > /usr/share/nginx/html/index.html <<EOF
      <pre>
      $(/usr/bin/cowsay -f dragon "Hello World")
      </pre>
      EOF
      
      # Modify nginx configuration to listen on port 8080 instead of 80.
      sed -i 's/listen\s\+80;/listen 8080;/' /etc/nginx/nginx.conf
      
      systemctl enable nginx
      systemctl restart nginx
      ```

    * Schließen Sie den Vorgang über _Create launch template_ ab.

3. Gehen Sie nun in der Seitenleiste auf den Eintrag _Auto Scaling Groups_ und klicken danach auf _Create Auto Scaling group_.
    * Verwenden Sie wieder Ihren eindeutigen Namen.
    * Wäheln Sie das von Ihnen erstellte Launch Template.
    * Wählen Sie Ihr VPC, sowie **beide** Subnetz aus.
    * Zum verwenden Ihrer Target Group setzen Sie unter _Load Balancing_ den Haken bei _Attach to an existing load balancer_ und wählen Ihre Target Group.
    * Setzen Sie unter _Health Checks_ den Haken bei _Turn on Elastic Load Balancing health checks_.
    * Setzen Sie die maximale Kapazität auf `4`, die minimale auf `0` und die gewünschte auf `2`.
    * Schließen Sie den Vorgang nun ab.

Nach Erstellung sollten nun nach einiger Zeit zwei Instanzen nach Ihren Spezifikationen erzeugt werden.

#### Funktionstest

> [!IMPORTANT]
> Falls Sie MacOS verwenden, dieser Schritt funktioniert nicht mit Safari.
> Nehmen Sie für diesen Schritt bitte einen anderen Browser.

1. Gehen Sie nun auf Ihre Target Group und klicken Sie auf den Reiter _Targets_.
   Ihre Instanzen sollte hier nach kurzer Zeit als _healthy_ auftauchen.
   Sollten die Instanzen nicht auftauchen oder über 5 Minuten hinaus _unhealthy_ sein, haben Sie wahrscheinlich einen Fehler gemacht.

2. Gehen Sie auf die Ansicht des Load Balancers und kopieren Sie sich den _DNS name_ heraus und öffnen Sie die Adresse in einem neuen Tab.
   Wenn Sie einen Drachen sehen Sie die HTTP Antwort einer Ihrer Instanzen.

3. Verbinden Sie sich nun mit einer _Ihrer_ laufenden Instanzen per EC2 Instance Connect.
    * Gehen Sie dazu auf _Instances_ und Markieren (nicht auf die Detail-Ansicht Klicken) Sie einer Ihrer Instanzen.
    * Gehen Sie nun oben rechts auf _Actions_ und wählen Sie _Connect_ und in der folgenden Ansicht nochmals auf _Connect_.
    * Sie sind jetzt mit einer Shell Session auf dem Host verbunden.
    * Auf dem Host läuft ein `nginx` service der die Webseite hostet.
      Stoppen Sie den Prozess.
    * Was können Sie an der Target Group, Auto Scaling Group und an Ihren Instanzen beobachten? Wie verhält sich die Webseite währenddessen?

Lösung Prozess stoppen:

<details>
<p>

> Finden Sie als erstes die Prozess Id heraus z.B. mit `ps`:
>
> ``` shell
> ps aux | grep nginx
> ```
>
> Beispiel Ergebnis:
>
> ``` text
> root        8203  0.0  0.1  15068  1600 ?        Ss   10:53   0:00 nginx: master process /usr/sbin/nginx
> nginx       8208  0.0  0.3  15504  3308 ?        S    10:53   0:00 nginx: worker process
> ec2-user   26156  0.0  0.2 222316  2036 pts/0    S+   10:56   0:00 grep --color=auto nginx
> ```
>
> Sie können den Prozess `8203` beenden, hierfür brauchen Sie in diesem Fall Root Rechte:
>
> ``` shell
> sudo kill 8203
> ```
>
> Sie können das Fenster schließen.

</p>
</details>

Lösung Beobachtungen:

<details>
<p>

> Als erstes wird Ihre Instanz in der Target Group _Unhealthy_ werden, da die Anwendung nicht mehr antwortet.
> Dadurch wird der Load Balancer jeglichen Traffic nur noch an die noch funktionierende Instanz schicken.
> Der Betrieb der Webseite ist ungestört, sie funktioniert weiter.
> Nach einiger Zeit wird die Auto Scaling Gruppe auf den vom Load Balancer gemeldeten Zustand reagieren und die kaputte Instanz gegen eine neue in der gleichen Zone austauschen.
> Das System heilt sich also selbst.
> Für kurze Zeit wird es daher drei Instanzen geben.
> Die kaputte Instanz wird rückstandslos terminiert.
> Der gesamte Prozess dauert normalerweise maximal 10 Min.

</p>
</details>

### Arbeiten mit der AWS CLI

#### Vorbereitung

Bauen Sie das Container Image unter `iaas-container` mit:

``` shell
cd 07-iaas/uebung/iaas-container
docker build \
  --build-arg IMAGE_CREATED="$(date -u +'%Y-%m-%dT%H:%M:%SZ')" \
  --build-arg IMAGE_REVISION="$(git rev-parse --abbrev-ref HEAD)" \
  --tag iaas-container \
  .
```

Starten Sie den Container mit:

``` shell
docker run -it --rm iaas-container
```

Nun sollte eine Bash Session vor Ihnen geöffnet sein.
Geben Sie `aws configure`.
In der folgenden Abfrage geben Sie Ihre AWS Zugangsdaten ein, wählen `eu-central-1` als Standard-Region und `json` als Standard-Ausgabeformat.
Testen Sie anschließend mit `aws sts get-caller-identity` Ihre Konfiguration.
Es sollte eine Antwort ähnlich dieser auf der Konsole erscheinen:

``` json
{
  "UserId": "AIDAIVMF6UC5ZJA4LA2QU",
  "Account": "264524865537",
  "Arn": "arn:aws:iam::264524865537:user/akrause"
}
```

#### Arbeiten mit der Kommandozeile

In Ihrer Docker-Umgebung ist das Kommandozeilen-Programm (`aws`) installiert, welches es Ihnen das Verbinden mit einer EC2-Instanz per SSH erlaubt.

Stellen Sie sich nun vor Sie sind für den zuverlässigen Betrieb des von Ihnen heute erstellten _Dragon Service_ im Auftrag eines Geldgebers verantwortlich.
Ihr Geldgeber stellt nun fest, dass die Nutzerzahlen Ihres Services zurückgehen und will daher Geld sparen - er ist bereit eine geringere Verfügbarkeit in Kauf zu nehmen.
Reduzieren Sie daher die Anzahl der Instanzen in der Auto Scaling Group auf `1` statt `2`.
Nutzen Sie `aws autoscaling help` um ein Kommando zusammenzustellen.

Lösung:

<details>
<p>

> ``` shell
> aws autoscaling set-desired-capacity \
>   --auto-scaling-group-name <IHR EINDEUTIGER NAME> \
>   --desired-capacity 1
> ```

</p>
</details>

Um die Nutzerzahlen wieder zu erhöhen möchte Ihr Geldgeber ein neues Format ausprobieren - Pinguine, so hat er gehört, sollen unter Informatikern sehr beliebt sein.
Verbinden Sie sich mit der verbleibenden Instanz.
Achten Sie darauf, sich nicht mit der Instanz zu verbinden, die eventuell noch am herunterfahren ist.

Setzen Sie Ihren Eindeutigen Namen in folgendes Kommando ein und führen Sie es aus, um Ihre Instanz zu finden:

``` shell
aws ec2 describe-instances \
  --filters "Name=tag:aws:autoscaling:groupName,Values=<IHR EINDEUTIGER NAME>" "Name=instance-state-name,Values=running" \
  --query 'Reservations[].Instances[].InstanceId' \
  --output text
```

Sie können die Instanz Id mit folgendem Kommando verwenden, um sich mit der Instanz zu verbinden:

``` shell
aws ec2-instance-connect ssh \
  --instance-id <INSTANZ ID> \
  --os-user ubuntu
```

Editieren Sie die `index.html` unter `/` als, so dass ein Pinguin erscheint - hierzu brauchen Sie root Rechte.
Wenn Sie nicht wissen wie Sie einen Pinguin erzeugen ändern Sie nur die Nachricht von _Hello World_ zu _Ich bin ein Pinguin_.

Lösung:

<details>
<p>

> Editieren Sie die Datei beispielsweise mit `nano`:
> <pre>
> sudo nano /index.html
> </pre>
> Je nach Betriebssystem speichern Sie Ihre Änderung mit STRG+O (Windows) oder Control+O (MacOS).

</p>
</details>

Gehen Sie wieder im Browser auf die Adresse Ihres Load Balancers um Ihre Änderung zu überprüfen.

Der Pinguin kommt gut an und die Nutzer Zahlen steigen.
Ihr Geldgeber möchte die Last mit einer weiteren Instanz abfangen.
Skalieren Sie mit der Kommandozeile Ihre Auto Scaling Group auf `2`.

Lösung:

<details>
<p>

> ``` shell
> aws autoscaling set-desired-capacity \
>   --auto-scaling-group-name <IHR EINDEUTIGER NAME> \
>   --desired-capacity 2
> ```

</p>
</details>

Was werden Ihre Nutzer sehen, sobald die zusätzliche Instanz nach einigen Minuten erfolgreich beim Load Balancer angemeldet wurde?

<details>
<p>

> Die neue Instanz wurde mit dem unveränderten Launch Template gestartet und ist daher mit dem Drachen konfiguriert.
> Die Webseite antwortet abwechselnd mit der alten und neuen Version des Dienstes.
> Der Load Balancer verteilt die Last abwechselnd auf die Instanzen.
>
> Wenn Sie Instanzen in einer Auto Scaling Group ändern wollen sollten Sie zunächst das Launch Template anpassen und dann die Instanzen rollierend austauschen.
>
> 1. Erst eine neue Instanz hinzufügen.
> 2. Warten bis Sie beim Load Balancer getestet und in die Rotation aufgenommen wurde.
> 3. Eine alte Instanz aus der Rotation nehmen (keine neuen Verbindungen) und warten bis alle noch laufenden Anfragen abgearbeitet wurden.
> 4. Die alte Instanz herunterfahren.
> 5. Wiederholen bis keine alten Instanzen mehr vorhanden sind.
>
> Idealerweise haben Sie im neuen Launch Template bereits ein mit Packer erstelltes Image konfiguriert, so dass die Installationsphase für Abhängigkeiten entfällt und immer die gleichen Pakete vorhanden sind.
> Einen rollierenden Austausch ohne Ausfall des Services können Sie auch vollautomatisch starten:
>
> ``` shell
> aws autoscaling start-instance-refresh \
>   --auto-scaling-group-name <IHR EINDEUTIGER NAME>
> ```

</p>
</details>
