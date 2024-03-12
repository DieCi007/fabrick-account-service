FROM arm64v8/openjdk:17.0

WORKDIR /home/gradle/app

COPY ./run-in-docker.sh .
COPY ./build/libs/account-0.0.1-SNAPSHOT.jar /home/gradle/app/app.jar
COPY ./application.properties /home/gradle/app/application.properties

EXPOSE 8080
ENTRYPOINT [ "sh", "./run-in-docker.sh" ]
