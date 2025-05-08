# Übung 1. Pods & Deployments

Infos:

- [Cheat-Sheet](cheat-sheet.md)

Aufgaben:

1. Installieren oder konfigurieren Sie ein Kubernetes-Cluster. Nutzen Sie dazu *eine* der folgenden Varianten:
   * Möglichkeit 1: Installieren Sie [kind](https://kind.sigs.k8s.io/)
   * Möglichkeit 2: Melden Sie sich am [kube07](https://kube.cs.hm.edu/) an, erzeugen Sie einen Cluster und laden Sie die kubeconfig herunter.
   * Möglichkeit 3: Falls sie schon Docker Desktop oder Podman Desktop installiert haben, aktivieren Sie das eingebaute Kubernetes in den Einstellungen.
   * Möglichkeit 4: Nutzen sie ggf. das online Tool [Killercoda](https://killercoda.com/playgrounds).
2. Prüfen Sie, ob der Cluster online ist:
   ```shell
   $ kubectl version
   Client Version: v1.32.2
   Kustomize Version: v5.5.0
   Server Version: v1.32.2
   ```

3. Machen Sie sich mit der App `Hello-Service` vertraut (siehe [code/hello-service](code/hello-service)).
4. Bauen Sie das Container-Image gegen Kubernetes (`build-to-kubernetes.sh`).
5. Schreiben Sie ein
   [Kubernetes Deployment](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/#creating-a-deployment)
   für die App `Hello-Service`.
6. Installieren Sie dieses Deployment in den Kubernetes-Cluster (`kubectl apply -f`).
7. Machen Sie sich mit der Navigation in `k9s` vertraut (siehe [Cheat-Sheet: k9s](cheat-sheet.md#k9s)).
8. Prüfen Sie mittels `k9s`, ob die App korrekt startet (Ready Flag, Container Logs).

Tipps:

Anstatt `k9s` aufzusetzen, können Sie auch `kubectl` benutzen, um den Zustand des Clusters zu sehen (z.B. mit `kubectl get pods`).

Falls Sie unter Windows Probleme mit `build-to-kubernetes.sh` haben:
- Versuchen Sie, das Script in der [Git Bash](https://gitforwindows.org/) oder der WSL
  auszuführen. Starten Sie die Git Bash mit Admin-Rechten.

Bonus:

9. Beenden Sie einen Pod über `k9s` und stellen Sie sicher, dass er neu gestartet wird
10. Ändern Sie die Anzahl der Replikas des Deployments
   1. über k9s > `:deployments` > `<s> Scale`
   2. über `kubectl scale`
   3. In welcher Reihenfolge passiert das Rolling Upgrade? (z.B. Zug-um-Zug oder wird erst komplett die neue Version ausgerollt und dann die alte heruntergefahren?)

