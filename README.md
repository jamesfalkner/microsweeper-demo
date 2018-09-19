Microsweeper on Azure demo
==========================

To create secrets to connect to CosmosDB:

```sh
oc create secret generic cosmosdb-credentials --from-literal=uri=[URI] --from-literal=key=[KEY]
```

Replace `[URI]` with your CosmosDB connection string.
Replace `[KEY]` with your CosmosDB connection key.

Then run `mvn clean package fabric8:deploy` to deploy.
