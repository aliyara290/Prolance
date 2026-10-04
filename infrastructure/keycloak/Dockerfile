FROM quay.io/keycloak/keycloak:latest
COPY themes /opt/keycloak/themes/
ENV KC_DB=postgres
RUN /opt/keycloak/bin/kc.sh build

