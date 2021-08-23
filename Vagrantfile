# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|
  # Every Vagrant development environment requires a box. You can search for
  # boxes at https://vagrantcloud.com/search.

  #config.vm.box = "bento/ubuntu-20.04"
  config.vm.box = "geerlingguy/centos8"
  config.vm.hostname = "centos8"

  # config.vm.network "forwarded_port", guest: 80, host: 8080, host_ip: "127.0.0.1"
  config.vm.network "private_network", ip: "192.168.33.10"

  config.vm.provider "virtualbox" do |vb|
    # Customize the amount of memory on the VM:
    vb.memory = "4096"
    vb.cpus = 4
  end

  ############################################################
  # Install Java 11 and Maven with development tools
  ############################################################
  config.vm.provision "shell", inline: <<-SHELL
    echo "*** Updating OS..."
    yum update
    echo "*** Installing Java 11 Development Environment..."
    yum install -y git make tree jq java-11-openjdk-devel maven

    # Make the shell look nice
    echo 'export PS1="[\\[\\033[01;32m\\]\\u@\\h\\[\\033[00m\\]: \\[\\033[01;34m\\]\\W\\[\\033[00m\\]]\\$ "' >> /home/vagrant/.bashrc
    chown vagrant:vagrant /home/vagrant/.bashrc

  SHELL

  ############################################################
  # Install Docker with PostgreSQL container
  ############################################################
  config.vm.provision :docker do |d|
    d.pull_images "postgres:alpine"
    d.run "postgres:alpine",
       args: "-d --name postgres -p 5432:5432 -v postgres:/var/lib/postgresql/data -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=postgres"
  end  

  ############################################################
  # Install Minikube abd Kubernetes tools
  ############################################################
  config.vm.provision "shell", inline: <<-SHELL
    # Install prerequisite packages for minikube
    # apt-get install -y conntrack

    # Install minikube
    echo "*** Installing: minikube..."
    curl -Lo minikube https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
    chmod +x minikube
    sudo install minikube /usr/local/bin/
    rm minikube

    # Install kubectl
    echo "*** Installing: kubectl..."
    curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
    chmod +x kubectl
    sudo install kubectl /usr/local/bin/
    rm kubectl
    echo "alias kc='/usr/local/bin/kubectl'" >> /home/vagrant/.bashrc
    chown vagrant:vagrant /home/vagrant/.bashrc
    
    # Install helm
    echo "*** Installing: helm..."
    curl https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3 | bash

    # Install Stern to monitor k8s logs
    echo "*** Installing: stern..."
    wget -O stern https://github.com/wercker/stern/releases/download/1.11.0/stern_linux_amd64
    install stern /usr/local/bin
    rm stern

    # # Install DevSace
    # curl -s -L "https://github.com/devspace-cloud/devspace/releases/latest" | sed -nE 's!.*"([^"]*devspace-linux-amd64)".*!https://github.com\1!p' | xargs -n 1 curl -L -o devspace
    # install devspace /usr/local/bin
    # rm devspace

    # Install Skaffold
    curl -Lo skaffold https://storage.googleapis.com/skaffold/releases/latest/skaffold-linux-amd64
    install skaffold /usr/local/bin/
    rm skaffold
    
    echo "****************************************"
    echo " Start minikube with:"
    echo "    minikube start --driver=docker"
    echo "----------------------------------------"
    echo " Switch to java 11 with:"
    echo "    sudo alternatives --config java"
    echo "****************************************"
  SHELL

end
