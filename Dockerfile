FROM java:8
LABEL description="Enthaelt den SyncServer für das Avare-Projekt."
LABEL maintainer="Lukas Struppek <lukas.struppek@gmail.com>"
EXPOSE 8443
ADD /target/AvareSyncServer-1.0.jar /AvareSyncServer-1.0.jar
ENTRYPOINT ["java","-jar","AvareSyncServer-1.0.jar"]