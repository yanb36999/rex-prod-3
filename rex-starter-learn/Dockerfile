FROM java:8

COPY target/rex-starter-learn.jar /app.jar
COPY rex-ui/ /rex-ui/
EXPOSE 8080
ENTRYPOINT ["java","-jar","-server","-Xms2g","-Xmx2g","-Xmn1g","-XX:+UseG1GC","-XX:SurvivorRatio=6","/app.jar"]
