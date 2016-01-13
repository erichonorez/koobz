FROM java:8
MAINTAINER Eric Honorez
EXPOSE 9000
ADD koobz-web.jar koobz-web.jar
RUN bash -c 'touch /koobz-web.jar'
ENTRYPOINT ["java", "-jar", "koobz-web.jar"]
