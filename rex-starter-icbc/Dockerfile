FROM java:8

COPY target/rex-starter-icbc.jar /app.jar
EXPOSE 8011
ENTRYPOINT ["java","-jar","-server","-Xms512M","-Xmx512M","-Xmn128M","-XX:+UseG1GC","-XX:SurvivorRatio=6","/app.jar"]
