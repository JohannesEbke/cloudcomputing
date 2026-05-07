# Lösung Bonus Übung 5. Gateway API mit Envoy Gateway

## Vorbereitung

Envoy Gateway installieren:

```shell
helm install eg oci://docker.io/envoyproxy/gateway-helm \
  -n envoy-gateway-system \
  --create-namespace
```

Warten, bis es bereit ist:

```shell
kubectl wait --timeout=5m -n envoy-gateway-system deployment/envoy-gateway --for=condition=Available
```

## Aufgaben

### 1. GatewayClass, Gateway und HTTPRoute anlegen

siehe `05_gateway.yaml`

```shell
kubectl apply -f 05_gateway.yaml
```

### 2. Prüfen, ob die Ressourcen erstellt wurden

```shell
kubectl get gatewayclass
kubectl get gateway
kubectl get httproute
```

### 3. Service von außerhalb des Clusters erreichen

#### Debugging: Gateway-Status und Service finden

Prüfen, ob das Gateway eine Adresse zugewiesen bekommen hat:

```shell
kubectl get gateway eg-gateway -o yaml
```

Den vom Envoy Gateway erstellten Proxy-Service finden (wird im **gleichen Namespace
wie das Gateway** erstellt, nicht in `envoy-gateway-system`):

```shell
kubectl get svc -l gateway.envoyproxy.io/owning-gateway-name=eg-gateway
```

#### Docker Desktop

Docker Desktop weist `LoadBalancer`-Services automatisch `localhost` zu.

```shell
kubectl get svc -l gateway.envoyproxy.io/owning-gateway-name=eg-gateway
```

Erwartete Ausgabe:

```
NAME              TYPE           CLUSTER-IP     EXTERNAL-IP   PORT(S)        AGE
envoy-default-eg-gateway-<id>   LoadBalancer   10.x.x.x      localhost      80:3xxxx/TCP   ...
```

Sobald `EXTERNAL-IP` = `localhost`:

```shell
curl http://localhost/hello
```

> **Hinweis:** Falls Port 80 bereits belegt ist (z.B. durch IIS oder einen anderen Dienst),
> bleibt der Service im Status `Pending`. In dem Fall Port 80 freigeben oder Port-Forward nutzen.

#### Fallback: Port-Forward

Den tatsächlichen Service-Namen ermitteln und Port-Forward starten:

```shell
# Service-Name herausfinden
kubectl get svc -l gateway.envoyproxy.io/owning-gateway-name=eg-gateway -o name

# Port-Forward (Service-Name aus vorherigem Befehl einsetzen)
kubectl port-forward svc/<service-name> 8080:80
```

Dann:

```shell
curl http://localhost:8080/hello
```


## Hinweise

- Die Gateway API ist der offizielle Nachfolger der Ingress API in Kubernetes.
- Eine `GatewayClass` definiert den Controller (ähnlich wie eine `IngressClass`).
- Ein `Gateway` beschreibt den Listener (Port, Protokoll).
- Eine `HTTPRoute` definiert die Routing-Regeln (ersetzt die Ingress-Regeln).
- Weitere Infos: https://gateway-api.sigs.k8s.io/
