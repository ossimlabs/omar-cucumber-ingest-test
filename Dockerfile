FROM centos:7
ENV HOME "/home/omar"
RUN adduser -D -h $HOME -s /sbin/nologin -u 1001 omar
RUN yum -y update 
RUN yum -y install java-1.8.0-openjdk

RUN mkdir -p /home/omar/build 
RUN mkdir /home/omar/build/classes 
RUN mkdir /home/omar/src 
RUN mkdir /home/omar/src/main

COPY /src/main/ /home/omar/src/main  
COPY /build/classes/groovy /home/omar/build/classes
COPY /build/libs/omar-cucumber-ingest-test-latest.jar home/omar/ 
RUN chown 1001:0 -R /home/omar/
RUN chmod -R 777 /home/omar/
EXPOSE 8080
USER 1001
VOLUME /conf
WORKDIR /home/omar
CMD ['java', '-server', '-Xms256m', '-Xmx1024m', '-Djava.awt.headless=true', '-XX:+CMSClassUnloadingEnabled', '-XX:+UseGCOverheadLimit', '-Djava.security.egd=file:/dev/./urandom', '-jar', "omar-cucumber-ingest-test-${version}.jar"]