# PostgreSQL Deploy

These instructions are how to access the PostgreSQL that is deployed via `helm` as a dependency of the inventory microservice using
bitnami helm charts.

## Set your namespace context

Unlike OpenShift, you must set the namespace context manually with Kubernetes

```bash
kubectl config set-context kubernetes-admin@kubernetes
kubectl config set-context --current --namespace=inventory-src
```

## Accessing PostgreSQL

PostgreSQL can be accessed via port 5432 on the following DNS names from within your cluster:
```
	inventory-postgresql.inventory-src.svc.cluster.local - Read/Write connection
```

To get the password for "postgres" run:
```bash
    export POSTGRES_PASSWORD=$(kubectl get secret --namespace inventory-src inventory-postgresql -o jsonpath="{.data.postgresql-password}" | base64 --decode)
```

To connect to your database run the following command:
```bash
    kubectl run inventory-postgresql-client --rm --tty -i --restart='Never' --namespace inventory-src --image docker.io/bitnami/postgresql:11.11.0-debian-10-r16 --env="PGPASSWORD=$POSTGRES_PASSWORD" --command -- psql --host inventory-postgresql -U postgres -d postgres -p 5432
```

To connect to your database from outside the cluster execute the following commands:
```bash
    kubectl port-forward --namespace inventory-src svc/inventory-db-postgresql 5432:5432 &
    PGPASSWORD="$POSTGRES_PASSWORD" psql --host 127.0.0.1 -U postgres -d postgres -p 5432
```

