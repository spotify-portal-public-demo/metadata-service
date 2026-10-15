# metadata-service

gRPC service for entity metadata

Builds and tests target Java 26. If your default JDK is older, Maven bootstraps a local Corretto 26 toolchain during the build.

Running the service locally still requires a Java 26 runtime or the Java 26 container image.

## Run Locally

```shell
./mvnw spring-boot:run
```
