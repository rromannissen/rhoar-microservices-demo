# Develop in the same environment we will deploy with
FROM registry.access.redhat.com/ubi8/openjdk-11:1.3-16

# install packages needed for development
USER root
RUN microdnf update -y && rm -rf /var/cache/yum
RUN microdnf install -y sudo wget vim git && microdnf clean all

# Allow sudo access to development environment
RUN echo "ALL            ALL = (ALL) NOPASSWD: ALL" >> /etc/sudoers

# Don't run as root, not even in development
USER jboss

# Make the prompt look nice
RUN echo 'export PS1="[\[\033[01;32m\]\u@\h\[\033[00m\]: \[\033[01;34m\]\W\[\033[00m\]]\$ "' >> ~/.bashrc
