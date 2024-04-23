# Übung: Cloud Architektur

## Aufgabe 1: Twelve Factor Apps

Die [Twelve Factor Apps](https://12factor.net/) beschreiben Methoden bzw. Empfehlungen zur Entwicklung von 
Cloud-Anwendungen.

Recherchieren Sie in Ihrer Gruppe die angegebenen Faktoren. Sie können dafür diese 
[Slides](https://www.slideshare.net/Alicanakku1/12-factor-apps)
nutzen oder frei recherchieren.

Bereiten Sie gemeinsam jeweils einen kurzen Foliensatz vor (1 Slide je Factor), in dem Sie
* die Idee hinter dem jeweiligen Factor benennen
* die Empfehlung erläutern

Bearbeiten Sie in den Gruppen die folgenden Punkte:

* Gruppe 1:
    * Codebase
    * Dependencies
    * Configuration
* Gruppe 2:
    * Backing Services
    * Build, release, run
    * Processes
* Gruppe 3:
    * Port binding
    * Concurrency
    * Disposability
* Gruppe 4:
    * Dev/Prod Parity
    * Logs
    * Admin Processes

Finden Sie einen Vertreter Ihrer Gruppe, der die Vorstellung übernimmt.

## Aufgabe 2: Das Raft-Konsens-Protokoll

Erarbeiten Sie die Funktionsweise vom Raft Protokoll mithilfe folgender
[Demo](http://thesecretlivesofdata.com/raft/).

## Aufgabe 3: etcd

Siehe Verzeichnis "etcd".

## Aufgabe 4: Erste Erfahrungen mit Traefik und Consul sammeln

Ziel dieser Übung ist es, erste praktische Erfahrungen mit Traefik und Consul zu machen.
Dabei wird ein einfacher Spring Cloud REST Service zusammen mit Consul
für Service Discovery und Configuration und Traeffik als Edge Server aufgesetzt.

Diese Übung orientiert sich an diesem [Tutorial](https://m.mattmclaugh.com/traefik-and-consul-catalog-example-2c33fc1480c0).

## Vorbereitung

* Das Aufsetzen eines Spring Cloud Microservice ist in Übung 1 beschrieben. Die Lösung dieser
  Übung dient als Startpunkt und wird in dieser Übung erweitert.

## Aufgaben

### Testen sie den vorgefertigten Container

Zuerst müssen sie den Book-Service im Unterverzeichnis `book-service` bauen. Benutzen Sie dazu den Befehl:
```shell
./mvnw package
```

Nun können Sie den Container mit dem Docker-Compose File in diesem Verzeichnis bauen und starten:
```shell
docker-compose up --build
```
Testen Sie, dass der Book-Service korrekt gestartet wird.
Nach dem Start sollten sie den Book-Service direkt auf Port `18080` erreichbar sein. 
Sie können dies z.B. mit
```
curl http://localhost:18080/api/books
```
überprüfen. 

### Consul Cluster (Single Node) mit Docker Compose

Erweitern Sie das Docker Compose File um einen Consul Service (im Single Node Betrieb)
Verwenden Sie das dafür aktuellste offizielle Docker Image von Hashicorp.: [hub.docker.com/_/consul](https://hub.docker.com/_/consul)
 
Stellen Sie sicher, dass die Consul UI gestartet wird, und alle benötigen Ports exponiert werden.

<details>
<summary>Orientieren Sie sich an folgendem Abschnitt, falls Sie nicht weiterkommen:</summary>

```
  consul:
    image: hashicorp/consul
    command: consul agent -server -dev -client=0.0.0.0 -ui -bootstrap -log-level warn
    ports:
      - "8400:8400"
      - "8500:8500"
      - "8600:53/udp"
    networks:
      - cloud-architecture
```
</details>

Starten sie den Consul Service. Sie sollten nun mit die UI mit [http://localhost:8500/ui/](http://localhost:8500/ui/) aufrufen können.

### Spring Cloud Microservice

Erweitern Sie den book-service so, dass sich dieser

* beim Start bei der Consul Service Discovery anmeldet,
* beim Start seine Konfigurationswerte bei Consul abholt,
* die Service-Schnittstellen (nicht die Admin Schnittstellen) über Traefik aufgerufen werden können

Die folgenden Dependencies müssen der `pom.xml` hinzugefügt werden:

```xml
<!-- required for Consol discovery and configuration -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-config</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-discovery</artifactId>
</dependency>
```

Danach muss unter `src/main/resources` die Datei `bootstrap.properties` angelegt werden:

```properties
spring.application.name=book-service

# specify Consul host and port
# we use the CONSUL_HOST and CONSUL_PORT env variables
# later set in docker compose as well as Kubernetes
spring.cloud.consul.host=${consul.host:consul}
spring.cloud.consul.port=${consul.port:8500}

spring.cloud.consul.config.enabled=true
spring.cloud.consul.config.prefix=configuration
spring.cloud.consul.config.default-context=application

# do not fail at startup if Consul is not there
spring.cloud.consul.config.fail-fast=false

# store properties as blob in property syntax
# e.g. configuration/book-service/data
spring.cloud.consul.config.format=properties
spring.cloud.consul.config.data-key=data
```

Im gleichen Verzeichnis müssen sie noch die Datei `application.properties` erweitern:
```properties
# assign a unique instance ID
spring.cloud.consul.discovery.instance-id=${spring.application.name}:${spring.application.instance_id:${random.value}}

# required by Docker compose and Consul to run the health check
# register IP address and heartbeats
spring.cloud.consul.discovery.prefer-ip-address=true
spring.cloud.consul.discovery.heartbeat.enabled=true
```


Falls Sie nicht weiterkommen, verwenden Sie folgenden Abschnitt:
```yaml
 book-service:
    build: ./book-service
    image: book-service:1.1.0
    ports:
      - 18080:18080
    depends_on:
      - consul
    networks:
      - cloud-architecture
    environment:
      - SPRING_CLOUD_CONSUL_HOST=consul
```

Zuletzt müssen sie dafür sorgen, dass der book-service nach Consul gestartet wird. 
Erweitern sie dazu die book-service docker-compose Konfiguration um den folgenden Block:
```yaml
    depends_on:
      - consul

```

### Traefik Edge Service mit Consul Backend

Betreiben Sie den Traefik Edge Service mittels Docker Compose und verwenden Sie
Consul als Discovery Backend für Traefik.

Benutzen sie dazu das offizielle Traefik Image: [https://hub.docker.com/_/traefik](https://hub.docker.com/_/traefik)
* Konfigurieren sie die API (= Traefik Dashboard) mit dem CLI Parameter `--api.insecure=true`.
* Konfigurieren sie Consul als Catalog, siehe dazu: [https://doc.traefik.io/traefik/providers/consul-catalog/](https://doc.traefik.io/traefik/providers/consul-catalog/)
* Exponieren Sie folgenden Ports: `80`, `8080` (für die API)

Starten Sie den Container ebenfalls über Docker Compose und übergeben Sie beim Start die nötigen Konfigurationen zur
Interaktion mit Consul.

<details>
<summary>Falls Sie nicht weiterkommen, verwenden Sie folgenden Abschnitt:</summary>

```yaml
  reverse-proxy:
    image: traefik
    command: --providers.consulcatalog.endpoint.address="consul:8500" --api.insecure=true
    ports:
      - 80:80
      - 8080:8080
    depends_on:
      - consul
    links:
      - consul
    networks:
      - cloud-architecture
```
</details>

Starten Sie die Services erneut. Wenn alles geklappt hat, sollten Sie nun den Book-Service in der Traefik-UI sehen.
Sie sollten nun den Book-Service unter Angabe eines Host-Headers aufrufen können:
```shell
curl -H 'Host: book-service' 'http://localhost/api/books'
```

### Umstellung auf Path-based Routing

In der `application.properties` muss folgende Property angelegt werden:

```properties
spring.cloud.consul.discovery.tags=traefik.http.routers.book-service.rule=Host(`book-service`) || PathPrefix(`/book-service`),traefik.http.middlewares.book-service-stripprefix.stripprefix.prefixes=/book-service,traefik.http.routers.book-service.middlewares=book-service-stripprefix
```

Starten Sie die Services erneut. Wenn alles geklappt hat, sollten sie nun eine zusätzliche Middleware Traefik-UI sehen.
Sie sollten den Book-Service nun auch mit einem Pfad-Präfix aufrufen können:
```shell
curl 'http://localhost/book-service/api/books'
```

### Testen

* Die Anwendung sollte nun mit Header `Host: book-service` unter `http://localhost/api/books` erreichbar sein.
* Die Anwendung sollte nun unter `http://localhost/book-service/api/books` erreichbar sein.
* Die Consul UI sollte unter `http://localhost:8500/ui` erreichbar sein.
* Die Traefik UI sollte unter `http://localhost:8080` erreichbar sein.
