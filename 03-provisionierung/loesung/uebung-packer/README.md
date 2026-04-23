

```shell
packer build nginx.json

packer init nginx.pkr.hcl 
packer build nginx.pkr.hcl

docker image ls | grep packer
```