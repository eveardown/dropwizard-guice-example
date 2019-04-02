dropwizard-guice-example
========================

A complete dropwizard example for using Guice.

```
mvn clean package
java -jar target/hello-guice-*-SNAPSHOT.jar server hello-world.yml

To test:

$ curl http://localhost:8080/v1/hello-world?name=Steve
```
