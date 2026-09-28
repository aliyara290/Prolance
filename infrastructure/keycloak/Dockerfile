FROM quay.io/keycloak/keycloak:24.0.1
COPY themes /opt/keycloak/themes/
ENV KC_DB=postgres
RUN /opt/keycloak/bin/kc.sh build
