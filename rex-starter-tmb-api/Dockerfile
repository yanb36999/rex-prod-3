FROM java:8

COPY target/rex-starter-tmb-api.jar /app.jar
EXPOSE 8089
ENTRYPOINT ["java","-jar","-server","-Xms1g","-Xmx1g","-Xmn512M","-XX:+UseG1GC","-XX:SurvivorRatio=6","/app.jar"]
