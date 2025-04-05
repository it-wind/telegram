# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the version argument
ARG VERSION
# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY ./target/telegram-${VERSION}.jar /app/telegram.jar

# Run the application
CMD ["java", "-jar", "telegram.jar"]