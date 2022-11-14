FROM maven:3.5.4-jdk-11

COPY . /project

#run the spring boot application
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-jar","/project/target/PubSubExample-0.0.1-SNAPSHOT.jar"]
