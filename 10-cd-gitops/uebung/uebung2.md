# Übung 2 – GitOps

In dieser Übung wollen wir GitOps mit ArgoCD und Flux in Kubernetes kennenlernen. 
Wir werden ArgoCD und Flux lokal in den Kind-Cluster installieren und dann auf dem GitOps Weg Applikationen deployen.

## ArgoCD

Installieren Sie ArgoCD in ihrem lokalen Kind Cluster.
Folgen Sie dazu der [ArgoCD Anleitung](https://argo-cd.readthedocs.io/en/stable/getting_started/)
und verwenden Sie eine der vorgeschlagenen Methoden, um ArgoCD lokal erreichbar zu machen.

Sie erreichen ArgoCD dann unter https://localhost/ bei Load-Balancer oder https://localhost:8080/ mit Port-Forward.

Das Passwort zum Einloggen können Sie direkt aus dem Secret auslesen 
oder sich über `argocd admin initial-password -n argocd` auf der Konsole ausgeben lassen.

### PodInfo deployen

Legen Sie in der ArgoCD-UI eine neue Application an. Füllen Sie die Felder dazu wie folgt aus:

* General 
  * Application Name: podinfo 
  * Project Name: default
  * Sync Policy: Automatic
      * Prune Resources: yes
      * Self Heal: yes
* Source 
  * Repository URL: https://github.com/stefanprodan/podinfo
  * Revision: HEAD 
  * Path: kustomize 
* Destination 
  * Cluster-URL: https://kubernetes.default.svc
  * Namespace: default

Klicken Sie dann oben auf Create und wählen Sie die neue Applikation aus. Machen Sie sich mit der UI vertraut.

### Ihre eigene Anwendung deployen

Legen Sie in Ihrem Service aus Aufgabe 1 im Verzeichnis `k8s` eine Datei `kustomization.yaml` mit folgendem Inhalt an:
```yaml
resources:
  - deployment.yaml
```

Legen Sie in der ArgoCD-UI eine neue Application an. Füllen Sie die Felder dazu wie folgt aus:

* General
    * Application Name: bookstore
    * Project Name: default
    * Sync Policy: Automatic
      * Prune Resources: yes
      * Self Heal: yes
* Source
    * Repository URL: https://github.com/USER/REPO
    * Revision: HEAD
    * Path: kustomize
* Destination
    * Cluster-URL: https://kubernetes.default.svc
    * Namespace: default

Klicken Sie dann oben auf Create und wählen Sie die neue Applikation aus. Machen Sie sich mit der UI vertraut.

### Deinstallation

Deinstallieren Sie ArgoCD wie folgt:
```shell
kubectl delete -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml` wieder deinstallieren.
kubectl delete deployment podinfo
kubectl delete deployment bookstore
```

## Flux

Flux hat im Gegensatz zu ArgoCD keine UI, ist dafür aber mächtiger. Es gibt einige Drittanbieter-UIs, 
die beliebte flux UI ist mit dem unklaren Fortbestand von Weaveworks allerdings ohne Maintainer. 
Als Alternative bietet sich Capacitor an.

### Voraussetzungen

Erstellen Sie ein [Personal Access Token](https://github.com/settings/tokens) mit Scope `repo`.
Flux wird damit automatisch ein Repository auf Github anlegen. Sie können das Token und Repo nach der Übung wieder löschen.

### Flux CLI installieren
Flux besteht aus einer CLI und mehreren Server-Komponenten im Kubernetes.
Für die Installation wird die Flux CLI benötigt. 
Folgen Sie der [Anleitung](https://fluxcd.io/flux/get-started/#install-the-flux-cli) und installieren Sie die CLI. 
Folgen Sie nachfolgend dem Flux [Getting Started Guide](https://fluxcd.io/flux/get-started/#export-your-credentials). 
Sie sollten nach Abschluss dieses Abschnitts 
- die Flux CLI installiert haben
- Flux in Kind installiert haben
- ihr Repository als Cluster State verwenden

### PodInfo deployen

Legen Sie dazu unter clusters/my-cluster folgende zwei Dateien mit dem vorgegebenen Inhalt an:

<details>
<summary>podinfo-kustomization.yaml</summary>

```yaml
---
apiVersion: kustomize.toolkit.fluxcd.io/v1
kind: Kustomization
metadata:
  name: podinfo
  namespace: flux-system
spec:
  interval: 30m0s
  path: ./kustomize
  prune: true
  retryInterval: 2m0s
  sourceRef:
    kind: GitRepository
    name: podinfo
  targetNamespace: default
  timeout: 3m0s
  wait: true
```
</details>

<details>
<summary>podinfo-source.yaml</summary>

```yaml
---
apiVersion: source.toolkit.fluxcd.io/v1
kind: GitRepository
metadata:
  name: podinfo
  namespace: flux-system
spec:
  interval: 1m0s
  ref:
    branch: master
  url: https://github.com/stefanprodan/podinfo
```
</details>

Committen Sie die Änderungen und warten Sie zwei Minuten, bis flux die Änderungen angezogen hat.


### Ihre eigene Anwendung deployen

Deployen Sie die Anwendung aus Übung 1 im Cluster.

Legen Sie in Ihrem Service aus Aufgabe 1 im Verzeichnis `k8s` eine Datei `kustomization.yaml` mit folgendem Inhalt an:
```yaml
resources:
  - deployment.yaml
```

Legen Sie im Repo fleet-infra unter clusters/my-cluster die Dateien `bookstore-kustomization.yaml` und `bookstore-source.yaml` an
und füllen Sie sie mit passendem Inhalt.

### Bonusaufgaben

Versuchen Sie beispielsweise herauszufinden, warum der PodInfo HorizontalPodAutoscaler nicht richtig funktioniert.

### Deinstallation

Deinstallieren Sie flux wie folgt:
```shell
flux uninstall --namespace=flux-system
kubectl delete deployment podinfo
kubectl delete deployment bookstore
```
