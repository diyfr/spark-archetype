#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
FROM openjdk:8-alpine
ENV LANG fr_FR.UTF-8
RUN apk add --update ttf-dejavu ttf-ubuntu-font-family && rm -rf /var/cache/apk/*
EXPOSE 8081
RUN mkdir /out
RUN mkdir /logs
RUN mkdir /config
VOLUME /out
VOLUME /logs
VOLUME /config
COPY target/${groupId}.${artifactId}*.jar ${groupId}.${artifactId}.jar
ENTRYPOINT ["java","-jar","/${groupId}.${artifactId}.jar"]
