# superapps
super performing nonblocking apps

We have two apps, user service and account service.

The idea is both should be regisrted to a service registry and it must be discoverable to each other.


Run the apps using following commands

```java -jar target/user-service-1.0-SNAPSHOT-fat.jar -conf src/main/resources/application-conf.json```

```java -jar target/account-service-1.0-SNAPSHOT-fat.jar -conf src/main/resources/application-conf.json```


Have installed mongodb on your system so that both the services can communicate with each other.
