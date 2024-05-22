FROM ubuntu:24.04@sha256:3f85b7caad41a95462cf5b787d8a04604c8262cdcdf9a472b8c52ef83375fe15

WORKDIR /root

COPY setup.sh .
RUN ./setup.sh \
 && rm ./setup.sh
