packer {
  required_plugins {
    docker = {
      source  = "github.com/hashicorp/docker"
      version = "1.0.9"
    }
  }
}

source "docker" "nginx" {
  changes     = ["EXPOSE 80", "CMD [\"nginx\", \"-g\", \"daemon off;\"]"]
  commit      = true
  image       = "nginx:1.25.4-alpine3.18"
  run_command = ["-d", "-t", "-i", "{{ .Image }}", "/bin/sh"]
}

build {
  sources = ["source.docker.nginx"]

  provisioner "file" {
    destination = "/usr/share/nginx/html/"
    source      = "./index.html"
  }

  post-processor "docker-tag" {
    repository = "packer-nginx"
    tag        = "1.0"
  }
}
