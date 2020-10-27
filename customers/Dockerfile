FROM registry.access.redhat.com/ubi8/ubi-minimal:8.1
WORKDIR /deployments/
RUN chown 1001 /deployments \
    && chmod "g+rwX" /deployments \
    && chown 1001:root /deployments
COPY --chown=1001:root target/*-runner /deployments/application

EXPOSE 8080
USER 1001

CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
