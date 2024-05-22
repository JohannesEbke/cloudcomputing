#!/bin/bash
set -euxo pipefail

apt-get update
apt-get install -y -q \
  gnupg \
  curl \
  wget \
  git \
  unzip \
  groff \
  less \
  lsb-release

wget -O- https://apt.releases.hashicorp.com/gpg | gpg --dearmor -o /usr/share/keyrings/hashicorp-archive-keyring.gpg
echo "deb [signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] https://apt.releases.hashicorp.com $(lsb_release -cs) main" > /etc/apt/sources.list.d/hashicorp.list
apt-get update
apt-get install -y -q terraform
rm -rf /var/lib/apt/lists/*

mkdir bin
cd bin

case $(uname -m) in
  aarch64)
    curl "https://awscli.amazonaws.com/awscli-exe-linux-aarch64.zip" -o "awscliv2.zip"
    ;;
  x86_64)
    curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
    ;;
  *)
    echo "Unsupported architecture"
    exit 1
    ;;
esac
unzip awscliv2.zip
rm awscliv2.zip
./aws/install

cat >> /root/.bashrc <<-EOF
  complete -C /usr/local/bin/aws_completer aws
EOF
