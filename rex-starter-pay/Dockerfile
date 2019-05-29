FROM java:8

COPY target/rex-starter-pay.jar /app.jar
EXPOSE 8011
ENTRYPOINT ["java","-jar","-server","-Xms2G","-Xmx2G","-Xmn128M","-XX:+UseG1GC","-XX:SurvivorRatio=6","/app.jar"]
