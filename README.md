# Microservices with Red Hat Runtimes demo

> **WARNING**: This is an unstable branch. Work in progress.

This repository was originally intended to contain the source code used as demo for the talk **Openshift Reloaded: Microservices 2.0 with RHOAR"** held at the Openshift Madrid Meetup in February 22nd, 2018 (slides [here](https://www.slideshare.net/rromannissen/openshift-reloaded-microservices-20-with-rhoar)). This demo has now been updated to adapt to [the latest GA available of Red Hat Runtimes](https://www.redhat.com/en/blog/latest-updates-red-hat-runtimes) and replace all services built using Wildfly Swarm with the Red Hat build of Quarkus.

The aim of this demo is to showcase the features included in Red Hat Runtimes, focusing on Spring Boot and Quarkus. Instead of presenting a complex use case, the demo focuses on all the wiring and configuration required to enable all Red Hat Runtimes' answers to several of Microservices' challenges (distributed tracing, externalized configuration, circuit breaker...) using the latest GA available.

Source code for the original demo can still be found in the [tag 1.0-meetup in this repository](https://github.com/rromannissen/rhoar-microservices-demo/tree/1.0-meetup).

## What's new

- **Spring**:
  - Updated to Spring Boot 2.2.6 using the Snowdrop supported BOM.
  - Replaced JAX-RS with Spring MVC in Spring Boot services.
  - Removed all boilerplate code to setup and configure Opentracing. Used the opentracing-spring-jaeger-web-starter starter instead.
  - Replaced spring-cloud-starter-hystrix with spring-cloud-starter-netflix-hystrix.

- **Quarkus**:
  - Migrated all Wildfly Swarm services to the Red Hat build of Quarkus 1.3.
  - Adapted persistence code to Panache and implemented the Repository pattern.
  - Removed all boilerplate code to setup and configure Opentracing. Used the quarkus-smallrye-opentracing extension instead.
  - Removed the custom health endpoint and replaced it with the quarkus-smallrye-health extension instead.
  - Used package-private instead of private members in beans [following Quarkus recommendations](https://quarkus.io/guides/cdi-reference#native-executables-and-private-members).
  - Removed Arquillian for testing and used quarkus-junit5 instead. Once the Red Had build of Quarkus 1.4 is released, tests could be further simplified by [using the quarkus-junit5-mockito extension and the @InjectMock annotation](https://quarkus.io/guides/getting-started-testing#further-simplification-with-injectmock).

## Architecture

The demo includes 4 microservices:

- **Customers**: Stores all customer related data. Developed using Quarkus and PostgreSQL as data store.
- **Inventory**: Stores detailed information about products. It uses the Quarkus/PostgreSQL stack as well.
- **Orders**: Manages all order related entities. It stores only UIDs to refer to Products and Customers. Implemented with Spring Boot and using a PostgreSQL database.
- **Gateway**: Access and aggregation layer for the whole application. It gets orders data and aggregates Products and Customers detailed information. Also implemented with the Spring Boot/PostgreSQL stack.

![Architecture Screenshot](docs/images/basic_architecture.png?raw=true "Architecture Diagram")

It can be argued that the domain is too fine grained for the modeled business, or that the approach is not optimal for data aggregation. While these statements might be true, the focus on the demo was to present a simple case with microservices interacting with each other, and shouldn't be considered a design aimed for a production solution.

## Setup

This demo has been developed using the following setup:

- Openshift Container Platform 4.4
- OpenShift Pipelines Operator 1.0.1 (Tekton 0.11.3)
- ArgoCD Operator 0.0.12

Other setups may work as well, but Tekton 0.11.x or later is required for the pipeline and tasks to work.

## TODO

All following sections are to be remade for Helm templates and ArgoCD based deployment in OCP. Instructions are not up to date and may fail.

## Deployment Pipeline in OCP

![Pipeline Screenshot](docs/images/pipeline.png?raw=true "Pipeline Diagram")


### The build-bot service account

The pipeline available in this repository accesses several external systems such as Git repositories and image repositories. In order to enable authentication in those resources, the definition of a Service Account named build-bot has been included as a YAML file in the tekton directory of this repository. This service account will have to be bound to several secrets as explained in the following points for the pipeline to work properly.

#### Pushing to a Git repository with credentials

As the pipeline requires pushing to a Git repository to update manifests and trigger Argo CD synchronizations, it is necessary to provide credentials for the git related tasks (in this case the update-manifest task). For this, a secret has to be created with valid credentials to push to the application repository. This secret [requires a concrete format for Tekton to create the git credentials in the containers properly](https://github.com/tektoncd/pipeline/blob/v0.11.3/docs/auth.md#basic-authentication-git), and a YAML file with its definition has been included as well in the tekton directory of this repository, looking as follows:

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: git-user-pass
  annotations:
    tekton.dev/git-0: <REPOSITORY DOMAIN, FOR EXAMPLE https://github.com>
type: kubernetes.io/basic-auth
stringData:
  username: <USERNAME WITH PERMISSION TO PUSH TO THE TARGET REPOSITORY>
  password: <PASSWORD>
```

Edit the file and enter valid credentials for your repository and its domain (for example https://github.com if it is hosted in GitHub).

#### Accessing registry.redhat.io

All components use the [OpenJDK 11 Image for Java Applications on RHEL8](https://catalog.redhat.com/software/containers/openjdk/openjdk-11-rhel8/5cd2aedebed8bd5717d3c46a) image available in registry.redhat.io. As [this registry requires authentication](https://access.redhat.com/RegistryAuthentication), a service account linked to a pull secret for the registry is required to pull images from it in the buildah task [as stated in the Tekton documentation](https://github.com/tektoncd/pipeline/blob/v0.11.3/docs/auth.md#kubernetess-docker-registrys-secret). In order for it to work, first create a [registry service account for registry.redhat.io using a Red Hat account](https://access.redhat.com/terms-based-registry/) and download the associated pull secret, which should look similar to this:

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: my-red-hat-account-pull-secret
data:
  .dockerconfigjson:
  <contents of the generated config.json file in base64>
type: kubernetes.io/dockerconfigjson
```

Once the file is downloaded, create the secret in OCP with the following command:

```
oc create -f my-red-hat-account-pull-secret.yaml
```

#### Setting up the build-bot service account


With the secret created, edit the build-bot-sa.yaml file to include it in the secrets list for the service account:


```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: build-bot
secrets:
  - name: my-red-hat-account-pull-secret
  - name: git-user-pass
```

Take into account that the git-user-pass secret has already been included in the build-bot-sa.yaml file available in this repository.

### Creating all resources

Once the build-bot and its associated secrets have been edited following the instructions of the previous points, all pipeline resources can be created easily with a single command. From the root directory of this repository, execute the following command:

```
oc create -f ./tekton
```

Take into account that the build-bot service account will be bound to the namespace in which it was created

### Pushing to the OCP internal registry

Given the pipeline will be executed using the build-bot service account, [granting permission for that account to push to the OCP internal registry is required](https://docs.openshift.com/container-platform/4.4/registry/accessing-the-registry.html). Once the service account has been created as specified in the previous section, simply assign the registry-editor role by executing the following command:

```
oc policy add-role-to-user registry-editor system:serviceaccount:<NAMESPACE IN WHICH THE SA WAS CREATED>:build-bot
```

### Running the pipeline

tkn pipeline start deploy-pipeline --serviceaccount build-bot


## Setting up Argo CD

### Installing the Argo CD Operator

First of all, install [the Argo CD Operator for Kubernetes](https://github.com/argoproj-labs/argocd-operator). This can be easily done in OpenShift 4 using the OperatorHub by installing the "Argo CD" operator from the catalog, not to be confused with "Argo CD Operator (Helm)". This operator will be bound to the namespace in which it was installed, which will usually be "argocd".

### Installing an Argo CD instance

To install a simple Argo CD instance with routes enabled, simply use the descriptor available in the argocd directory of this repository:

```
oc create -f argocd-route.yaml
```

After installation is finished, the password for the "admin" account can be found in the admin.password key from the argocd-cluster secret from the target namespace in which the Argo CD instance was created.

Argo CD must be able to create and modify Kubernetes object in the target deployment namespace for the application, so the argocd service account must be granted the "edit" role on that project:

```
oc policy add-role-to-user edit system:serviceaccount:argocd:argocd-application-controller -n <TARGET DEPLOYMENT NAMESPACE>
```

Finally, in order to simplify interaction with the Argo CD instance, install the ArgoCD CLI following [the instructions available in the official documentation.](https://argoproj.github.io/argo-cd/cli_installation/#linux)


### Adding the simple-java-service repository to Argo CD

All application charts have a dependency on the simple-java-service chart, which is published in a Helm Repository hosted in GitHub pages. Argo CD will need to obtain this chart in order to render configuration from the Helm templates, so the repository has to be added to its configuration. This can be easily done by running this command after logging in to you Argo CD server instance:

```
argocd repo add https://rromannissen.github.io/simple-java-service/docs --type helm --name simple-java-service
```


### Creating an application in Argo CD




## Configuration

All microservices have been designed to make use of externalized configuration, even though all follow a config-file approach. The ones implemented with Spring Boot use [Spring Cloud Kubernetes to get configuration from a ConfigMap](https://github.com/spring-cloud-incubator/spring-cloud-kubernetes#configmap-propertysource) once it has been deployed in Openshift. This ConfigMap can be created using the following command (from the src/main/java/resources directory):

```
oc create configmap orders-config --from-file=application.properties=application-openshift.properties
```

Note that the application-openshift.properties file is renamed to application.properties. This is done because Spring Cloud Kubernetes looks for that exact file name inside the ConfigMap to which it points, included in the file bootstrap.properties.

Finally, the Spring applications require access to the Kubernetes API in order to access the config maps. To do this, simply add the view role to the default service account:

```
oc policy add-role-to-user view -z default
```

As no OCP/Kubernetes integration plugin exists for the moment for Wildfly Swarm, the approach is slightly different in that case. For both services, a [Fabric8 resource fragment](https://maven.fabric8.io/#resource-fragments) is included in the src/main/fabric8 directory of each project, configuring a DeploymentConfig object pointing to a ConfigMap volume with the project-defaults.yml (renamed from bundled the project-openshift.yml file) configuration file. ConfigMap creation command would be as follows in this case:

```
oc create configmap inventory-config --from-file=project-openshift.yml
```

> **Note**: Skipping this step before deploying the applications will result on startup errors and a failed deployment.

## Deployment

### Deployment in Openshift

All projects have been configured to be easily deployed in OCP using the Fabric8 Maven Plugin. In order to perform a deployment, first login to the cluster using the oc CLI and change to the target project:

```
oc login <target_cluster>
oc project <target_project>
```

Running the following command after that will result on the application being deployed in the current OCP project:

```
mvn clean fabric8:deploy -P openshift
```

As stated before, Orders, Inventory and Customers services require a PostgreSQL database instance, so the PosgreSQL template can be used. Data initialization is performed at application startup from import.sql and load.sql files.

### Running Locally

Applications can also run locally for testing purposes. In this case, the command to be used varies between Spring Boot and Wildfly Swarm. In the first case, the command is as follows:

```
mvn clean spring-boot:run -P local
```

For Quarkus, the command is the following:

```
mvn clean wildfly-swarm:run -P local
```

## Distributed Tracing

All microservices are based on the Opentracing API for distributed tracing, using the Jaeger implementation for that.

In order to have a Jaeger instance running in OCP, [the development template available in the official Jaeger Github site](https://github.com/jaegertracing/jaeger-openshift#development-setup) was used.
