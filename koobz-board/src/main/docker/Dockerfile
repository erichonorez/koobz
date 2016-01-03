FROM java:8
MAINTAINER Eric Honorez
EXPOSE 8080
ADD koobz-board.jar koobz-board.jar
ADD bin/koobz-board-start.sh koobz-board-start.sh
RUN chmod +x /koobz-board-start.sh
ENTRYPOINT ["/koobz-board-start.sh"]
