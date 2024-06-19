# Übung 1 - Continuous Delivery

In dieser Übung wollen wir eine Continuous Integration und Deployment Pipeline mit GitHub Actions schreiben.

Alle Befehle in dieser Übung lassen sich am einfachsten auf einer Unix Bash ausführen.
Verwenden Sie unter Windows nach Möglichkeit das Windows Subsystem für Linux (WSL) oder eine VM.

## Repo und Java-Anwendung

Suchen Sie sich eine jar-basierte Java-Anwendung Ihrer Wahl, die Sie gerne deployen möchten
oder verwenden Sie die fertige Anwendung (Java 17) im Ordner [bookstore](bookstore).

Alternativ können Sie sich auch unter https://start.spring.io eine neue Anwendung 
mit Dependencies `spring-boot-starter-web` und `spring-boot-starter-actuator` erzeugen.

Melden Sie sich unter https://github.com an, erzeugen Sie ein neues Repo und pushen Sie Ihren Java-Service.

## CI-Pipeline

### GitHub Workflow

Schreiben Sie einen GitHub Workflow, der Ihre Anwendung auscheckt und baut.
Wenn Sie in GitHub auf den Actions Tab klicken, finden Sie viele praktische Beispiele.

<details>
<summary>Cheat: .github/workflows/build.yaml</summary>

```yaml
name: Java CI with Maven
on:
  push:
    branches: [ "main" ]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'zulu'
          cache: maven

      - name: Build with Maven
        run: ./mvnw -B package --file pom.xml
```
</details>

### Erzeugen des Docker-Images

Schreiben Sie ein Dockerfile für Ihre Anwendung.
Als Basis können Sie zum Beispiel das Image `azul/zulu-openjdk-alpine` mit einem geeigneten tag verwenden.
Kopieren Sie die fertige jar-Datei in das Image und führen Sie sie mittels `java -jar my.jar` aus.

<details>
<summary>Cheat: Beispiel-Dockerfile</summary>

```Dockerfile
FROM azul/zulu-openjdk-alpine:17-latest
COPY target/bookstore-*.jar bookstore.jar
ENTRYPOINT [ "java", "-jar", "/bookstore.jar" ]
```
</details>

### Docker Build & Push

Erweitern Sie Ihre CI-Pipeline um den Build des Docker Images und pushen Sie das Image nach GitHub Packages.

<details>
<summary>Cheat: .github/workflows/build.yaml</summary>

```yaml
env:
  REGISTRY: ghcr.io
  IMAGE_NAME: ${{ github.repository }}

jobs:
  build:
    runs-on: ubuntu-latest
    # Pushing to GitHub packages requires extra permissions
    permissions:
      contents: read
      packages: write
      id-token: write
  steps:
    # ... Previous build steps ...

    - name: Set up Docker Buildx
      uses: docker/setup-buildx-action@v3

    - name: Log into registry ${{ env.REGISTRY }}
      if: github.event_name != 'pull_request'
      uses: docker/login-action@v3
      with:
        registry: ${{ env.REGISTRY }}
        username: ${{ github.actor }}
        password: ${{ secrets.GITHUB_TOKEN }}

    - name: Build and push Docker image
      id: build-and-push
      uses: docker/build-push-action@v5
      with:
        context: .
        push: ${{ github.event_name != 'pull_request' }}
        tags: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:latest
        cache-from: type=gha
        cache-to: type=gha,mode=max
```
</details>

Auf Ihrer Repo-Seite in GitHub sollte in der rechten Seitenleiste nun ein Abschnitt "Packages" auftauchen.


## Deployment-Pipeline

Als nächsten wollen wir die Möglichkeit schaffen, das latest Image automatisiert im HM-Kube zu deployen.
Da wir keinen direkten Zugriff auf das Cluster von außen haben, installieren wir einen self-hosted 
GitHub Runner im Cluster und deployen unsere Anwendung von dort.

### Kubernetes Deployment beschreiben

Schreiben Sie eine Kubernetes-Deployment-Konfiguration, also eine YAML-Datei mit `kind: Deployment`,
die mindestens eine Instanz Ihrer Anwendung im Cluster deployed. Der Einfachheit halber können Sie als
Image Tag immer `latest` verwenden, also z.B. `image: ghcr.io/NAME/REPO:latest`

Legen Sie die Datei unter `k8s/deployment.yaml` ab.

<details>
<summary>Cheat: k8s/deployment.yaml</summary>

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  namespace: bookstore
  name: bookstore
  labels:
    app: bookstore
spec:
  replicas: 1
  selector:
    matchLabels:
      app: bookstore
  strategy:
    type: RollingUpdate
  revisionHistoryLimit: 10
  template:
    metadata:
      namespace: bookstore
      labels:
        app: bookstore
    spec:
      restartPolicy: Always
      containers:
        - name: bookstore
          image: ghcr.io/0xqab/cloud-computing-bookstore:latest
          imagePullPolicy: IfNotPresent
          startupProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            periodSeconds: 1
            failureThreshold: 300
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            periodSeconds: 2
            failureThreshold: 5
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            periodSeconds: 10
            failureThreshold: 3
          ports:
            - name: http
              containerPort: 8080
          resources:
            limits:
              memory: 1024Mi
            requests:
              cpu: 250m
              memory: 512Mi
```
</details>


### Installation des GitHub Runners

#### Vorbereitung

Installieren Sie `kubectl` und `helm` auf ihrem Rechner.

Starten Sie eduVPN, melden Sie sich am https://kube.cs.hm.edu/accounts/login/ an und laden Sie eine aktuelle
kubeconfig.yaml für ihr Cluster herunter.

Testen Sie den Zugriff auf den HM Cluster:
```shell
export KUBECONFIG=kubeconfig.yaml
kubectl get pods --all-namespaces
```
Beispiel-Ausgabe:
```text
NAMESPACE     NAME                                     READY   STATUS    RESTARTS   AGE
kube-system   coredns-78c44c9b7d-kkh9x                 1/1     Running   0          21h
```

#### Installation des ARC Systems

Installieren Sie den Controller für das GitHub Runner Set:
```shell
NAMESPACE="arc-systems"
helm install arc \
    --namespace "${NAMESPACE}" \
    --create-namespace \
    oci://ghcr.io/actions/actions-runner-controller-charts/gha-runner-scale-set-controller
```

#### Installation des ARC Runner Sets

Erzeugen Sie sich ein neues Token (classic) mit scope `repo` unter https://github.com/settings/tokens
und setzen Sie das Token als Wert für `GITHUB_PAT` im Script unten.

Hinterlegen Sie als `GITHUB_CONFIG_URL="https://github.com/USERNAME/REPO"` ihre Repo-URL.

Installieren Sie nun den das GitHub Runner Set:
```shell
INSTALLATION_NAME="arc-runner-set"
NAMESPACE="arc-runners"
GITHUB_CONFIG_URL=""
GITHUB_PAT=""
helm install "${INSTALLATION_NAME}" \
    --namespace "${NAMESPACE}" \
    --create-namespace \
    --set githubConfigUrl="${GITHUB_CONFIG_URL}" \
    --set githubConfigSecret.github_token="${GITHUB_PAT}" \
    oci://ghcr.io/actions/actions-runner-controller-charts/gha-runner-scale-set
```

#### Testen der Installation

**Test 1:** Sie sollten in Kubernetes nun zwei weitere Pods sehen:
```shell
kubectl get pods --all-namespaces
```
Ausgabe:
```text
NAMESPACE     NAME                                     READY   STATUS    RESTARTS   AGE
kube-system   coredns-78c44c9b7d-kkh9x                 1/1     Running   0          21h
arc-systems   arc-gha-rs-controller-5996cccf95-82ktk   1/1     Running   0          68m
arc-systems   arc-runner-set-754b578d-listener         1/1     Running   0          68m
```

**Test 2:** Außerdem sollte unter https://github.com/NAME/REPO/settings/actions/runners
nun ein arc-runner-set auftauchen.

### Kubeconfig als Secret hinterlegen
Hinterlegen Sie den modifizierten Inhalt der `kubeconfig.yaml` in den Einstellungen Ihres 
GitHub Repositories als Repository Secret mit Namen `KUBE_CONFIG`.
Die zugehörigen Seite befindet sich unter Settings -> Secrets and Variables -> Actions.

**WICHTIG:** Ändern Sie vorher den Server des Clusters in der `kubeconfig.yaml`
von `https://NAME-cloud-computing.api.kube.cs.hm.edu` auf `https://kubernetes.default.svc`,
da kube.cs.hm.edu nicht von innerhalb des Clusters aufgerufen werden kann.
```yaml
apiVersion: v1
clusters:
  - cluster:
      server: https://kubernetes.default.svc
# ...
```
Ihre lokale kubeconfig.yaml sollte unverändert bleiben, die Änderung ist nur für das Secret relevant.


### Deployment Workflow

Schreiben Sie einen zweiten Workflow, der die `deployment.yaml` mittels `kubectl apply` im Cluster anwendet.

<details>
<summary>Cheat: .github/workflows/deploy.yaml</summary>

```yaml
name: Deploy on HM-Kube

on:
  workflow_dispatch:

jobs:
  deploy:
    runs-on: arc-runner-set

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Setup kubectl
        uses: azure/setup-kubectl@v3

      - name: Setup kubeconfig
        run: |
          echo '${{ secrets.KUBE_CONFIG }}' > kubeconfig.yaml
          chmod 600 kubeconfig.yaml

      - name: Apply kubernetes files
        env:
          KUBECONFIG: kubeconfig.yaml
        run: |
          kubectl apply -f k8s/deployment.yaml

      - name: Post kubeconfig
        if: always()
        run: rm -f kubeconfig.yaml
```
</details>

### Deployment testen

Prüfen Sie die Ausgabe auf GitHub im Actions Tab.

Prüfen Sie, ob Ihr Service im Cluster deployed worden ist:
```shell
kubectl get pods --all-namespaces
```
Ausgabe:
```text
NAMESPACE     NAME                                     READY   STATUS    RESTARTS   AGE
kube-system   coredns-78c44c9b7d-kkh9x                 1/1     Running   0          21h
arc-systems   arc-gha-rs-controller-5996cccf95-82ktk   1/1     Running   0          68m
arc-systems   arc-runner-set-754b578d-listener         1/1     Running   0          68m
bookstore     bookstore-68bb4d5df-tw7c9                1/1     Running   0          10m
```
