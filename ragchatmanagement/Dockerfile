FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy JAR from target folder with original name
COPY target/rag-chat-management.jar rag-chat-management.jar

# Expose port
EXPOSE 8080

# Health check
#HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
#  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the app
ENTRYPOINT ["java", "-jar", "rag-chat-management.jar"]
