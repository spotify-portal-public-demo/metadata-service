FROM amazoncorretto:21-alpine
ENTRYPOINT ["/usr/bin/metadata-service.sh"]

COPY metadata-service.sh /usr/bin/metadata-service.sh
COPY target/*.jar /usr/share/metadata-service/metadata-service.jar
