# Shell

``` shell
nerdctl build -t cc-managed . -f Dockerfile_Managed_Node
nerdctl run --rm -it -p 3000:22 cc-managed
ssh -p 3000 root@localhost
```
