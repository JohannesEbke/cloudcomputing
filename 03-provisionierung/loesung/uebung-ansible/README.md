# Shell

``` shell
nerdctl build -t cc-managed . -f Dockerfile_Managed_Node
nerdctl run --rm -it -p 3000:22 cc-managed
# other terminal: 
ssh -p 3000 root@localhost
```

``` shell
docker build -t cc-managed . -f Dockerfile_Managed_Node
docker run --rm -it -p 3000:22 cc-managed
# other terminal: 
ssh -p 3000 root@localhost

docker compose up

docker ps # finden sie die Ports, auf dem die Container laufen
curl localhost:<port>

docker compose down
```
