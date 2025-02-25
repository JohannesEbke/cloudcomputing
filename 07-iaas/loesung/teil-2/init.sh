#!/bin/bash
set -euxo pipefail

dnf update -y

dnf install -y cowsay
dnf install -y nginx
dnf clean all

cat > /usr/share/nginx/html/index.html <<EOF
<pre>
$(/usr/bin/cowsay -f dragon "${message}")
</pre>
EOF

# Modify nginx configuration to listen on port 8080 instead of 80.
sed -i 's/listen\s\+80;/listen 8080;/' /etc/nginx/nginx.conf

systemctl enable nginx
systemctl restart nginx