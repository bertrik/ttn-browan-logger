FROM eclipse-temurin:17.0.11_9-jre-alpine

LABEL maintainer="Bertrik Sikken bertrik@gmail.com"

ADD ttn-browan-logger/build/distributions/ttn-browan-logger.tar /opt/

WORKDIR /opt/ttn-browan-logger
ENTRYPOINT ["/opt/ttn-browan-logger/bin/ttn-browan-logger"]

