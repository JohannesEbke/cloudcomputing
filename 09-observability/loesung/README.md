# Observability (Cloud Computing @ TH Rosenheim)

This project is a showcase of the LGTM stack to demonstrate the topic Observability.

The tle-fetcher retrieves TLE (two-line element set) data for calculating satellites trajectories from a NASA related API, 
see https://tle.ivanstanojevic.me/#/tle/25544.
You can learn more information on the TLE format [here](https://en.wikipedia.org/wiki/Two-line_element_set), on orbital
mechanics [here](https://en.wikipedia.org/wiki/Orbital_mechanics) and while playing some rounds of Kerbal Space Program.

The sky-map service retrieves the data from tle-fetcher and outputs it as JSON.

This project uses Quarkus. If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Prerequisites

To build and run this application, you will need the following dependencies on your system:

| Name           | Version |
|----------------|---------|
| Docker         | *       |
| Docker-Compose | 1.13.0+ |
| Java           | 17      |


## Java services

### Building the application

You can build the application using Gradle:

```shell
$ ./gradlew build
```

This will build the Java applications and two docker images. The docker image will be registered as `qaware/tle-fetcher:1.0.0` and `qaware/sky-map:1.0.0` within your Docker daemon:

```shell
$ docker images

REPOSITORY           TAG     IMAGE ID       CREATED         SIZE
qaware/sky-map       1.0.0   5f4a5695cc8f   7 seconds ago   438MB
qaware/tle-fetcher   1.0.0   55bd6d637c77   7 seconds ago   438MB
```

### Configuration

All relevant configuration can be found in each service `src/main/resources/application.properties`.

## The Grafana stack

The Grafana stack is configured in the docker-compose.yaml and its configuration files `loki-config.yaml`, `otel-collector-config.yaml`, 
`prometheus-config.yaml`, `tempo-config.yaml`.

### Grafana

Grafana is the visualization engine of the Grafana stack.

You can provision several other things automatically, like dashboards and datasources. All of this is stored in `grafana/` and deployed automatically on startup.

### Loki

Loki is the log storage engine of the Grafana stack. In this repository, there is only the `loki-config.yaml` with basic storage configuration.

### OTEL-Collector

The OTEL-Collector is a vendor independent observability agent, which can scrape or receive different types of telemetry data.

The configuration `otel-collector-config.yaml` includes a setup for receiving logs, traces and metrics via the open telemetry protocol and forwarding
them to Loki, Prometheus and Tempo

### Prometheus

### Tempo

Tempo stores APM and tracing data from services. It can also be connected to Grafana.

## Running the services

Run the applications with `docker compose`:

```shell
$ docker compose up
```

## Related Guides

- OpenTelemetry ([guide](https://quarkus.io/guides/opentelemetry#introduction)): Implements OTLP for Tracing, Metrics and Traces
- Micrometer Registry Prometheus ([guide](https://quarkus.io/guides/micrometer)): Enable Prometheus support for Micrometer
- Logging JSON ([guide](https://quarkus.io/guides/logging#json-logging)): Add JSON formatter for console logging
- SmallRye Health ([guide](https://quarkus.io/guides/microprofile-health)): Monitor service health
- Micrometer and OpenTelemetry metrics ([guide](https://quarkus.io/guides/telemetry-micrometer-to-opentelemetry)): Instrument the runtime and your application with dimensional metrics using Micrometer.
