FROM adoptopenjdk/openjdk13-openj9:x86_64-alpine-jre-13.0.1_9_openj9-0.17.0

LABEL mantainer="Eduardo Betanzos <ebetanzos@hotmail.es>"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/cinema-family.jar", "--root.dir=/mnt/videos"]

ARG JAR_FILE
ADD $JAR_FILE /cinema-family.jar