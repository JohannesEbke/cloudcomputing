# Observability

Lesen Sie sich die Datei README.md durch, um die Quarkus-Services und die Grafana-Dienste zu starten.
Sie finden Grafana anschließend unter folgender URL: http://localhost:3000

## Übung 1: Logs

1. Die Services loggen im Quellcode verschiedene Dinge auf allen möglichen Leveln. Warum sehen Sie nicht alle auf der Konsole? Korrigieren Sie ggf.
   Einstellungen dafür.
2. Um auch komplizierte Logs, wie z.B. Stacktraces, in einem Stück zu Loki zu bringen, werden diese als JSON formatiert. Warum sehen Sie davon nichts auf der
   Konsole?
3. Konfigurieren Sie Quarkus so, dass die Logs der Services abgezogen und zu Loki gesendet werden.
4. Kontrollieren Sie die Logs im Explore-Fenster von Grafana. Was müssen Sie tun, damit statt JSON lesbare Logs angezeigt werden?
5. Bonus: Erstellen Sie ein Grafana-Dashboard für die Logs.

<details> 
<summary>Tipp</summary>

Es gibt verschiedene Möglichkeiten, Logs nach Loki zu senden. Da der OTEL-Collector jedoch bereits so konfiguriert ist, dass er Logs über OTLP an Loki
weiterleitet, sollten wir diesen Weg bevorzugen.
Siehe dazu: https://quarkus.io/guides/opentelemetry-logging. Verwenden Sie entweder den HTTP- oder den gRPC-Endpunkt des OTEL-Collectors.

Hinweis: Die Übertragung von Logs über OTLP ist ein relativ neues Feature in der Observability-Welt und befindet sich bei Quarkus derzeit noch im
Preview-Status.
Die Cloud-Native-Community bewegt sich jedoch zunehmend in Richtung einer Vereinheitlichung aller Telemetriedatenströme über OTLP.
</details>

## Übung 2: Metriken

1. Quarkus kann Metriken, Logs und Traces entweder aktiv über OTLP pushen oder passiv über einen Scrape-Endpunkt bereitstellen. Entscheiden Sie sich für einen
   der beiden Ansätze – wichtig ist, dass am Ende mindestens die JVM-Metriken über Prometheus in Grafana sichtbar sind.
2. Mit einem schönen Dashboard lassen sich die Metriken viel besser visualisieren. Im Grafana ist bereits ein Dashboard konfiguriert - wie kommt das da rein?
3. Bonus: Informieren Sie sich, wie Sie selbst definierte Metriken in eine Quarkus-Anwendung einbauen können. Visualisieren Sie Ihre eigene Metrik in einem
   Dashboard.

<details> 
<summary>Tipp</summary>

* Startpunkt Metriken über OTLP: https://quarkus.io/guides/opentelemetry-metrics
* Startpunkt Metrik scrapen: https://exceptionly.com/2022/01/18/monitoring-quarkus-with-prometheus-and-grafana/

</details>

## Übung 3: Traces

1. Traces sind in OTLP First-Class Citizens und daher besonders gut integriert.
   Verwenden Sie die quarkus-opentelemetry-Extension, um Traces direkt an den OTEL-Collector zu senden.
   Dieser ist bereits so konfiguriert, dass er die Traces automatisch an Tempo weiterleitet.
2. Machen Sie ein paar Aufrufe gegen den laufenden sky-map-Service (http://localhost:8088/`) und prüfen Sie die Traces in Grafana.
   Wie kommen Sie an die Trace-Id?
3. Informieren Sie sich, wie Sie zusätzliche (Meta-)Daten zu den Traces hinzufügen können und Testen Sie das an einem der Services.

Quarkus-Konfiguration: https://quarkus.io/guides/opentelemetry-tracing

## Bonus-Übung:

Die Orbit-Darstellung sowie die Position der Satelliten in der Datei
`09-observability/uebung/sky-map/src/main/resources/META-INF/resources/js/skymap.js`
scheinen nicht korrekt zu sein.
Untersuchen Sie den Fehler und verwenden Sie bei Bedarf Ihren Observability-Stack, um die Ursache zu analysieren.
