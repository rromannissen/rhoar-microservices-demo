# GitOps for Microservices with Red Hat Runtimes demo

This repository was originally intended to contain the source code used as demo for the talk **Openshift Reloaded: Microservices 2.0 with RHOAR"** held at the Openshift Madrid Meetup in February 22nd, 2018 (slides [here](https://www.slideshare.net/rromannissen/openshift-reloaded-microservices-20-with-rhoar)). This demo has now been updated to adapt to [the latest GA available of Red Hat Runtimes](https://www.redhat.com/en/blog/red-hat-runtimes-update-delivers-new-features-open-hybrid-cloud) and replace all services built using Wildfly Swarm with the Red Hat build of Quarkus.

The aim of this demo is to showcase the features included in Red Hat Runtimes, focusing on Spring Boot and Quarkus. Instead of presenting a complex use case, the demo focuses on all the wiring and configuration required to enable all Red Hat Runtimes' answers to several of Microservices' challenges (distributed tracing, externalized configuration, circuit breaker...) using the latest GA available.

Another focus area is to provide an example of a modern approach to CI/CD using a GitOps paradigm. For that, a deployment pipeline based in new technologies such as [Tekton](https://github.com/tektoncd/pipeline) and [ArgoCD](https://argoproj.github.io/argo-cd/) has been included as well, adding a configuration model based on [Helm Charts](https://helm.sh/).

Source code for the original demo can still be found in the [tag 1.0-meetup in this repository](https://github.com/rromannissen/rhoar-microservices-demo/tree/1.0-meetup).

> **Note**: The best approach to work with this demo is forking this repository, since the pipeline requires write permissions in the Git repository where the application configuration is stored.

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

- Openshift Container Platform 4.5
- OpenShift Pipelines Operator 1.0.1 (Tekton 0.11.3)
- ArgoCD Operator 0.0.14

Other setups may work as well, but Tekton 0.11.x or later is required for the pipeline and tasks to work.

## Deployment Pipeline in OCP

As stated before, one of the main focus areas of this demo is to showcase a modern approach for CI/CD using a set of tools and practices around the GitOps paradigm. For that, a Deployment Pipeline has been developed using Tekton. The following diagram depicts all tasks to be executed by the pipeline and its interaction with external systems and tools:


![Pipeline Screenshot](docs/images/pipeline.png?raw=true "Pipeline Diagram")

Each of these tasks can be described as follows:

- **Clone Repository** downloads the source code from the target Git repository.

- **Build from Source** builds the application artifact from source code. This task has been tweaked to allow selecting the target subdirectory from the repository in which the target application source is available, allowing to have several application/components available in a single repository. **This way of versioning different services/components is highly discouraged**, as the optimal approach would be to have a dedicated repository for each component, since their lifecycle should be independent. Nevertheless, this choice was made to gather all demo materials on a single repository for simplicity purposes.

- **Build image** uses the Dockerfile packaged on the root directory of an application to build and image and push it to the target registry. The image will be tagged with the short commit hash of the source it contains.

- **Update Manifest** uses the short commit hash tag to update the application manifests in Git and point to the newly built image. Application deployment is then delegated to ArgoCD, which is continuously polling the configuration repository for changes and creates/updates all OpenShift objects accordingly. Since the DeploymentConfig configured for each component has a ConfigChange trigger, these changes trigger a new deployment on each update.

The pipeline accepts the following parameters:

- **git-url**: URL of the target Git repository.
- **git-branch**: target branch to work with. Defaults to master.
- **app-subdir**: Subdirectory from the repository in which the application source code is stored.
- **target-namespace**: Namespace/project in which to deploy the application.
- **target-registry**: Registry to push the built image to. Defaults to the internal OCP registry (image-registry.openshift-image-registry.svc:5000).


## Application Configuration Model

Both application and deployment configuration for each component have been modeled using Helm Charts. Each component directory contains a **config** subdirectory in which the chart is available. All deployment configuration is included in the **values.yaml** available in the root of the **config** directory. Application configuration is available as application.yaml files available in both the **config** and **secret** subdirectories. Non sensitive parameters should be included in the file inside the **config** subdirectory. Sensitive data such as passwords should be included in the file available in the **secret** subdirectory.

This chart will create a ConfigMap containing the application.yaml file inside the **config** subdirectory to be consumed by the applications via the OCP API. Along with that, a Secret containing the application.yaml file inside the **secret** subdirectory will be created as well, and mounted in the pod as a volume for the component to access the configuration file directly. The creation of other OpenShift objects such as DeploymentConfig, Service and Route will be delegated to [the simple-java-service chart developed for this demos as well](https://github.com/rromannissen/simple-java-service) and that the configuration chart uses as a dependency.    

## Setting up Argo CD

### Installing the Argo CD Operator

First of all, install [the Argo CD Operator for Kubernetes](https://github.com/argoproj-labs/argocd-operator). This can be easily done in OpenShift 4 using the OperatorHub by installing the "Argo CD" operator from the catalog, not to be confused with "Argo CD Operator (Helm)". This operator will be bound to the namespace in which it was installed, which will usually be "argocd".

### Installing an Argo CD instance

To install a simple Argo CD instance with routes enabled, simply use the descriptor available in the argocd directory of this repository:

```
oc create -f argocd-route.yaml
```

After installation is finished, the password for the "admin" account can be found in the admin.password key from the argocd-cluster secret from the target namespace in which the Argo CD instance was created.


Finally, in order to simplify interaction with the Argo CD instance, install the ArgoCD CLI following [the instructions available in the official documentation.](https://argoproj.github.io/argo-cd/cli_installation/#linux)


### Adding the simple-java-service repository to Argo CD

All application charts have a dependency on the simple-java-service chart, which is published in a Helm Repository hosted in GitHub pages. Argo CD will need to obtain this chart in order to render configuration from the Helm templates, so the repository has to be added to its configuration. This can be easily done by running this command after logging in to you Argo CD server instance:

```
argocd repo add https://rromannissen.github.io/simple-java-service/docs --type helm --name simple-java-service
```

### Creating the namespace

All demo materials have been configured to use a namespace or project named **order-management**. While it is possible to use any other name for the target project, it would require modifying the deployment configuration for all components, along with the ArgoCD application definitions. This task could be error prone, so do it at your own risk. To create the **order-management** namespace simply run:

```
oc new-project order-management
```

### Granting permission for ArgoCD to deploy in the project

Argo CD must be able to create and modify Kubernetes object in the target deployment namespace for the application, so the argocd service account must be granted the "edit" role on that project:

```
oc policy add-role-to-user edit system:serviceaccount:argocd:argocd-application-controller -n order-management
```

### Creating an application in Argo CD

Once the operator is installed and an Argo CD instance managed by it is running, it is very easy to create Argo applications using the available CRDs. For example, for the orders service we would have:

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: orders
  namespace: argocd
spec:
  project: default
  source:
    repoURL: https://github.com/rromannissen/rhoar-microservices-demo.git
    targetRevision: master
    path: orders/config
  destination:
    server: https://kubernetes.default.svc
    namespace: order-management
  syncPolicy:
    automated:
      prune: true
      selfHeal: true
```

> **Note**: Please take into account that the repoURL parameter must not target this repository but a fork of it since write permissions are required by the Tekton pipeline. Once this repository has been forked, this parameter must be changed to point to the new target repository.

ArgoCD application definitions for all services for this demo have been included in the argocd directory. In order to create them, change to the **argocd** project and simply create the resource from each file:

```
oc project argocd
oc create -f customers-application.yaml
oc create -f inventory-application.yaml
oc create -f orders-application.yaml
oc create -f gateway-application.yaml
```

## Deploying all services in OCP

The following points will provide a detailed explanation on how to deploy both the CI/CD pipeline and all application services in an OCP 4 cluster.

### Installing the OpenShift Pipelines Operator

The procedure to install the OpenShift Pipelines Operator [is explained in detail in the OCP official documentation](https://docs.openshift.com/container-platform/4.4/pipelines/installing-pipelines.html). This operator doesn't come out of the box in vanilla OCP 4 and should be installed to run this demo.  

### Using the right namespace

The target namespace or project named **order-management** was already created while configuring ArgoCD. In order to make sure that all the resources to be created in the following steps are instantiated in the right place, change to the target project by running:

```
oc project order-management
```

In case you decided to change the name of the target namespace, change into that project using the same command.

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

### Creating pipeline resources

Once the build-bot and its associated secrets have been edited following the instructions of the previous points, all pipeline resources can be created easily with a single command. From the root directory of this repository, execute the following command:

```
oc create -f ./tekton
```

Take into account that the build-bot service account will be bound to the namespace in which it was created.

### Pushing to the OCP internal registry

Given the pipeline will be executed using the build-bot service account, [granting permission for that account to push to the OCP internal registry is required](https://docs.openshift.com/container-platform/4.4/registry/accessing-the-registry.html). Once the service account has been created as specified in the previous section, simply assign the registry-editor role by executing the following command:

```
oc policy add-role-to-user registry-editor system:serviceaccount:order-management:build-bot
```

### Granting permission for applications to access the OCP API

All application components require access to the OCP API in order to access the ConfigMap objects to obtain their configuration at startup. To do this, simply add the view role to the default service account:

```
oc policy add-role-to-user view -z default
```

### Application databases

As stated before, Orders, Inventory and Customers services require a PostgreSQL database instance, so the PosgreSQL Ephemeral template can be used. Data initialization is performed at application startup from import.sql and load.sql files.

> **Note**: Object management for the required databases has been kept outside the application Helm charts for simplicity purposes. They will have to be created manually using the available templates in OCP prior to the application deployment or some components will fail to start properly or won't start at all.

### Distributed Tracing

All microservices are based on the Opentracing API for distributed tracing, using the Jaeger implementation for that.

In order to have a Jaeger instance running in OCP, [the Red Hat OpenShift Jaeger operator available in the Operator Hub](https://access.redhat.com/documentation/en-us/openshift_container_platform/4.4/html-single/jaeger/index#jaeger-operator-install_jaeger-install) has been used for this demo.

Once the operator has been installed, creating a simple Jaeger instance intended for demo purposes is as easy as instantiating the following object:

```yaml
apiVersion: jaegertracing.io/v1
kind: Jaeger
metadata:
  name: jaeger-all-in-one-inmemory
```


### Running the Pipeline

Once all setup is done, the pipeline will have to be run once per component, indicating the different component directory on each run. In order to run the pipeline, [the tkn CLI will have to be installed](https://docs.openshift.com/container-platform/4.4/cli_reference/tkn_cli/installing-tkn.html). For example, for the inventory service, the command and interaction to provide the parameters would be the following:

```
$ tkn pipeline start deploy-pipeline --serviceaccount build-bot
? Value for param `git-url` of type `string`? https://github.com/rromannissen/rhoar-microservices-demo.git
? Value for param `git-branch` of type `string`? (Default is `master`) master
? Value for param `app-subdir` of type `string`? inventory
? Value for param `target-namespace` of type `string`? (Default is `order-management`) order-management
? Value for param `target-registry` of type `string`? (Default is `image-registry.openshift-image-registry.svc:5000`) image-registry.openshift-image-registry.svc:5000
Please give specifications for the workspace: ws
? Name for the workspace : ws
? Value of the Sub Path :  
?  Type of the Workspace : pvc
? Value of Claim Name : inventory-pvc
```

It is important to highlight the usage of the --serviceaccount flag to make sure the build-bot service account is used in order to access all external resources. Also, a pvc for each service has been provided in the Tekton configuration to avoid having to download dependencies on each run.

## Running Locally

Applications can also run locally for testing purposes. In this case, the command to be used varies between Spring Boot and Quarkus. In the first case, the command is as follows:

```
mvn clean spring-boot:run -P local
```

For Quarkus services, the command is the following:

```
./mvnw compile quarkus:dev -P local
```

## Known issues

### SQLFeatureNotSupportedException exception at startup

The following exception is displayed at startup for the Orders service:

```
java.sql.SQLFeatureNotSupportedException: Method org.postgresql.jdbc.PgConnection.createClob() is not yet implemented.
```

This is caused by [an issue in Hibernate that has been fixed in version 5.4.x](https://hibernate.atlassian.net/browse/HHH-12368). Since the Hibernate version used for the Orders service is 5.3.14, a warning is displayed including the full stack trace for this exception. Although annoying, this warning is harmless for this example and can be ignored.

### Quarkus Kubernetes Config extension not available in 1.3.x

By the time of developing this demo, the Red Hat Build of Quarkus is based on the release 1.3.x of the Quarkus community project. This means that some later features are not included in the productized version supported by Red Hat. [The Quarkus kubernetes-config extension which allows developers to use Kubernetes ConfigMaps and Secrets as a configuration source](https://quarkus.io/guides/kubernetes-config) is among these non available features. The proposed configuration approach relied on non sensitive configuration being available as ConfigMaps to be directly accessed by applications through the OCP API, and sensitive data encrypted in Secrets and mounted as a volume in the application pod. This was meant to be applicable for both Quarkus and Spring Boot, but since the Quarkus version used for this demo doesn't allow this yet, all configuration has been moved to a Secret as a workaround to still enable the externalized configuration mechanism.

This issue should be solved as soon as the next release of the Red Hat Build of Quarkus is available, as the kubernetes-config extension was made available in Quarkus community 1.4.
