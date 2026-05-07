# Bonus Übung 5. Gateway API mit Envoy Gateway

Die Gateway-API ist der moderne nachfolger der Kubernetes Ingress-API
und wird genutzt, um den Zugriff von außerhalb des Clusters zu definieren.

Infos:

- [Cheat-Sheet](cheat-sheet.md)
- [Kubernetes Gateway API Docs](https://gateway-api.sigs.k8s.io/)
- [Envoy Gateway Docs](https://gateway.envoyproxy.io/)

## Vorbereitung

### Helm installieren

<details>
<summary>Windows (winget)</summary>

```shell
winget install Helm.Helm
```

Alternativ mit Chocolatey:

```shell
choco install kubernetes-helm
```

</details>

<details>
<summary>macOS (Homebrew)</summary>

```shell
brew install helm
```

</details>

<details>
<summary>Linux (apt / snap)</summary>

```shell
# Via Snap
sudo snap install helm --classic

# Oder via apt (Debian/Ubuntu)
curl https://baltocdn.com/helm/signing.asc | gpg --dearmor | sudo tee /usr/share/keyrings/helm.gpg > /dev/null
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/helm.gpg] https://baltocdn.com/helm/stable/debian/ all main" | sudo tee /etc/apt/sources.list.d/helm-stable-debian.list
sudo apt-get update
sudo apt-get install helm
```

</details>

Prüfen, ob Helm installiert ist:

```shell
helm version
```

### Envoy Gateway installieren

1. Installieren Sie die Gateway API CRDs und Envoy Gateway in Ihrem Cluster:

```shell
helm install eg oci://docker.io/envoyproxy/gateway-helm \
  -n envoy-gateway-system \
  --create-namespace
```

2. Warten Sie, bis das Envoy Gateway Pod bereit ist:

```shell
kubectl wait --timeout=5m -n envoy-gateway-system deployment/envoy-gateway --for=condition=Available
```

## Aufgaben

1. Erstellen Sie eine `GatewayClass` und ein `Gateway`, das auf Port 80 lauscht.
2. Erstellen Sie eine
   [HTTPRoute](https://gateway-api.sigs.k8s.io/api-types/httproute/),
   die den Traffic an den `Hello-Service` weiterleitet.
3. Prüfen Sie, ob der Service von außerhalb des Clusters erreichbar ist.