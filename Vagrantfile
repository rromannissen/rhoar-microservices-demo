# -*- mode: ruby -*-
# vi: set ft=ruby :

# All Vagrant configuration is done below. The "2" in Vagrant.configure
# configures the configuration version (we support older styles for
# backwards compatibility). Please don't change it unless you know what
# you're doing.
Vagrant.configure("2") do |config|
  # Every Vagrant development environment requires a box. You can search for
  # boxes at https://vagrantcloud.com/search.
  config.vm.box = "bento/ubuntu-20.04"
  config.vm.hostname = "ubuntu"

  # config.vm.network "forwarded_port", guest: 80, host: 8080, host_ip: "127.0.0.1"
  config.vm.network "private_network", ip: "192.168.33.10"

  config.vm.provider "virtualbox" do |vb|
    # Customize the amount of memory on the VM:
    vb.memory = "4096"
    vb.cpus = 4
  end

  ############################################################
  # Provider for Docker
  ############################################################
  config.vm.provider :docker do |docker, override|
    override.vm.box = nil
    docker.image = "rofrano/vagrant-provider:ubuntu"
    docker.remains_running = true
    docker.has_ssh = true
    docker.privileged = true
    docker.volumes = ["/sys/fs/cgroup:/sys/fs/cgroup:ro"]
    # Uncomment to force arm64 for testing images on Intel
    # docker.create_args = ["--platform=linux/arm64"] 
  end

  # Set up development environment
  # Needed: Java, maven, minikube, kubectl, helm
  config.vm.provision "shell", inline: <<-SHELL
    # Update OS
    apt-get update && apt-get upgrade -y

    # Install extra packages for development
    apt-get install git make tree jq
    
    # Install OpenJDK 11 and Maven
    apt-get install -y openjdk-11-jdk maven
    echo "JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64/" >> /etc/environment

    # Install prerequisite packages for minikube
    apt-get install -y conntrack

    # Install minikube
    curl -Lo minikube https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
    chmod +x minikube
    sudo install minikube /usr/local/bin/
    rm minikube

    # Install kubectl
    snap install kubectl --classic
    echo "alias kc='/snap/bin/kubectl'" >> /home/vagrant/.bash_aliases
    chown vagrant:vagrant /home/vagrant/.bash_aliases
    
    # Install helm
    curl https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3 | bash

    # Install Stern to monitor k8s logs
    wget -O stern https://github.com/wercker/stern/releases/download/1.11.0/stern_linux_amd64
    install stern /usr/local/bin
    rm stern
    
  SHELL

  # Install Docker
  config.vm.provision :docker

  # minikube start --driver=none

  ######################################################################
  # Add PostgreSQL docker container for database
  ######################################################################
  # docker run -d --name postgres -p 5432:5432 -v psqldata:/var/lib/postgresql/data postgres
  config.vm.provision :docker do |d|
    d.pull_images "postgres:alpine"
    d.run "postgres:alpine",
       args: "-d --name customers-postgresql -p 5432:5432 -v psqldata:/var/lib/postgresql/data -e POSTGRES_PASSWORD=customers -e POSTGRES_USER=customers -e POSTGRES_DB=customers"
  end  
end
