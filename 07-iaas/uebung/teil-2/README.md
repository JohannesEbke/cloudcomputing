# Übung: Infrastructure as Code mit Terraform auf AWS

Ziel dieser Übung ist das erlernen grundlegender Infrastructure as Code Fähigkeiten mit Terraform auf der AWS Cloud.
Hierzu werden Sie die Architektur aus der letzten Übung mit Terrform nachbauen.
Grundlegende Schritte sind hierfür schon vorbereitet.

1. Starten Sie den `iaas-container` und mounten Sie das Verzeichnis `uebung/teil-2` nach `/root/uebung/teil-2` im Container.
   Beispiel mit Bash aus dem Verzeichnis:

   ``` shell
   cd 07-iaas/uebung/teil-2
   docker run -it --rm -w /root/uebung/teil-2 --mount type=bind,source="$(pwd)",target=/root/uebung/teil-2 iaas-container
   ```

2. Konfigurieren Sie Ihren AWS Zungang mit `aws configure`.

   ``` text
   AWS Access Key ID [None]: AKIA...
   AWS Secret Access Key [None]: M0XY...
   Default region name [None]: eu-central-1
   Default output format [None]: json
   ```

3. Initialisieren Sie das Arbeitsverzeichnis mit `terraform init`.

4. Schauen Sie sich die bereits existierenden Terraform-Dateien an und machen Sie sich mit der grundlegenden Struktur vertraut.

5. Implementieren Sie alle Stellen die mit `#ToDo` annotiert sind.
   Definieren Sie sich selbst eine sinnvolle reihenfolge.
   In regelmäßigen Abständen sollten Sie ihre Implementierungsarbeiten auf die AWS-Cloud mit `terraform apply` anwenden.

6. Am Ende sollte die Terraform-Konfiguration einen Output-Parameter mit einer validen und funktionierenden URL enthalten.

7. Erzeugen Sie einen neuen Workspace mit `terraform workspace new dev` und wechseln zu diesem mit `terraform workspace select dev`.
   Überprüfen Sie, ob Sie mit `terraform apply` eine zweite Umgebung erzeugen können.
   Wenn nicht passen Sie ihre Konfigurationen so an, dass dies möglich ist.
   Machen Sie dafür insbesondere Benennungen von Ressourcen abhängig vom verwendeten Workspace.

8. Zerstören Sie alle erzeugten Ressourcen mit `terraform destroy` auf beiden Workspaces.
