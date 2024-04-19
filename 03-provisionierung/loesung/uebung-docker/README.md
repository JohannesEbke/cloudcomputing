# Shell

``` shell
nerdctl run --rm -it -p 80:80 nginx

nerdctl build -t cc-nginx:v1 .
nerdctl run --rm -it -p 8080:80 cc-nginx:v1

nerdctl compose build
nerdctl compose up -d
nerdctl compose stop
nerdctl compose rm –s -f
```
