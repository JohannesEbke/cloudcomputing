# Übung: Serverless Computing mit FaaS

In dieser Übung wollen wir FaaS auf AWS sowie verschiedene FaaS Frameworks auf Kubernetes kennenlernen.

## FaaS auf AWS: AWS Lambda mit Terraform

Um AWS Lambda aus dem Web ohne Zugriff auf die AWS API aufzurufen, ist es nötig, den API Gateway 
zu nutzen. Folgen Sie dazu dem folgenden Tutorial:

https://learn.hashicorp.com/tutorials/terraform/lambda-api-gateway

Hinweis: Dieses Tutorial zeigt auch die typische Organisation von Konfiguration, Variablen und Ressourcen in 
Terraform-Modulen - dies kann für die Arbeit zur ZV nützlich sein.

Zusatzaufgabe: Wechseln Sie die eingesetzte Sprache, z.B. auf Python.

## Knative

Diese Übung beschäftigt sich mit Knative ( https://knative.dev/ ), einer Serverlessplattform 
für Kubernetes oder OpenShift.

1. Installieren sie zunächst auf Ihrem Kubernetes Knative. Folgen sie hierzu den Anweisungen der
Dokumentation zum Quickstart: https://knative.dev/docs/getting-started/quickstart-install/

2. Folgen sie anschließend den Anweisungen von https://knative.dev/docs/getting-started/build-run-deploy-func/
Dort können sie eine einfache Funktion in Go oder Python schreiben.

Achtung:
Da DockerHub nicht mehr kostenfrei nutzbar ist, müssen Sie das GitLab-LRZ verwenden, um Ihre Docker Images zu speichern.
Sie können dafür ein individuelles Projekt anlegen, auf dem Sie dann die "Docker Registry" in den Einstellungen aktivieren.
Nutzen Sie dann https://docs.openfaas.com/reference/private-registries/ mit einem neuen GitLab-Token, so dass Knative
auch auf diese Registry zugreifen kann.

## Serverless Framework

Diese Übung beschäftigt sich mit dem Serverless Framework (https://www.serverless.com/open-source/),
einem CLI Tool zur einfachen und schnellen Entwicklung von von Event-getriebenen Funktionen.

Schreiben und deployen sie eine einfache Funktion in einer Sprache ihrer Wahl.

Zusatzaufgabe: Deployen Sie die gleiche Funktion auf Minikube mit z.B. Knative.
