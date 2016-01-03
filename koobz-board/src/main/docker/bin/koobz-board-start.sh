#!/bin/sh
java -jar koobz-board.jar \
    --spring.datasource.url=$KOOBZ_DB_URL \
    --spring.datasource.username=$KOOBZ_DB_USER \
    --spring.datasource.password=$KOOBZ_DB_PASSWORD
