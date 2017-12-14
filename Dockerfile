FROM java:8
LABEL description="Enthaelt den SyncServer fuer das Avare-Projekt."
LABEL maintainer="Lukas Struppek <lukas.struppek@student.kit.com>"
EXPOSE 8443
ADD /target/AvareSyncServer-1.0.jar /AvareSyncServer-1.0.jar
ENTRYPOINT ["java","-jar","AvareSyncServer-1.0.jar"]