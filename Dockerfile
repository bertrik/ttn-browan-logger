FROM adoptopenjdk/openjdk14:jre-14.0.2_12-alpine
LABEL maintainer="Bertrik Sikken bertrik@gmail.com"

ADD ttn-browan-logger/build/distributions/ttn-browan-logger.tar /opt/

WORKDIR /opt/ttn-browan-logger
ENTRYPOINT ["/opt/ttn-browan-logger/bin/ttn-browan-logger"]

