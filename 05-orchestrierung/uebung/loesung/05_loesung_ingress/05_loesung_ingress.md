# Lösung Bonus Übung 5. Ingress

Aufgaben:

1. Legt für den `Hello-Service` einen Ingress an.

siehe `04_ingress.yaml`

2. Prüft, ob euer Service von außerhalb des Clusters erreichbar ist

```shell script
http $(minikube ip)/hello
```

Eine hilfreiche Anleitung für die Konfiguration eines Ingress in einem `kind` Cluster bietet https://kind.sigs.k8s.io/docs/user/ingress/#ingress-nginx .
