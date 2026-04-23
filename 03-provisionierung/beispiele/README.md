# Beispiel: Provisionierung mit Packer

## Abspielen (fake)

Dieses Beispiel demonstriert die Erzeugung eines Amazon Images mit Packer zur Erzeugung von standadisierten [Dokku](https://dokku.com/) Instanzen.

[asciinema](https://asciinema.org) demonstration ausführbar mit:

```bash
asciinema play asciinema.cast
```

## Live Demo

# Check that you are logged: 
aws sts get-caller-identity

# if not: 
aws configure # for login with api key
aws login 

# run packer
packer build packer.json

# You might want to look/show at the aws web console for ec2
