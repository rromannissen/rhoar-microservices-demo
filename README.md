# Microservices with RHOAR demo

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


## TODO

All following sections are to be remade for Helm templates and ArgoCD based deployment in OCP. Instructions are not up to date and may fail.

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

For Wildfly Swarm, the command is the following:

```
mvn clean wildfly-swarm:run -P local
```

## Distributed Tracing

All microservices are based on the Opentracing API for distributed tracing, using the Jaeger implementation for that.

In order to have a Jaeger instance running in OCP, [the development template available in the official Jaeger Github site](https://github.com/jaegertracing/jaeger-openshift#development-setup) was used.
