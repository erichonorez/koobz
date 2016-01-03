FROM java:8
MAINTAINER Eric Honorez
EXPOSE 8080
ADD koobz-board.jar koobz-board.jar
ADD bin/koobz.sh koobz.sh
RUN chmod +x /koobz.sh
ENTRYPOINT ["/koobz.sh"]
