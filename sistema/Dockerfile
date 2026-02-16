# Tomcat 10
FROM tomcat:10.1.12-jdk17

# Elimina los archivos por defecto
RUN rm -rf /usr/local/tomcat/webapps/*

# Copia tu WAR
COPY target/sistema-0.0.1.war /usr/local/tomcat/webapps/ROOT.war

# Expone el puerto 8080
EXPOSE 8080

CMD ["catalina.sh", "run"]
