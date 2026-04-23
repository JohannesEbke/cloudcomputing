#!/bin/bash
set -euo pipefail

echo "This is provision.sh 👋"
sudo apt update && sudo apt install -y awscli

