FROM java:8
EXPOSE 8443
ADD /target/AvareSyncServer-1.0.jar /AvareSyncServer-1.0.jar
ENTRYPOINT ["java","-jar","AvareSyncServer-1.0.jar"]