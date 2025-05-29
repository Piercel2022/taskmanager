# Multi-stage build for JavaFX application
FROM openjdk:17-jdk-slim as builder

# Install Maven
RUN apt-get update && apt-get install -y maven

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean compile javafx:jlink

# Runtime stage
FROM openjdk:17-jre-slim

# Install necessary packages for JavaFX
RUN apt-get update && apt-get install -y \
    libgtk-3-0 \
    libglib2.0-0 \
    libfontconfig1 \
    libfreetype6 \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libgl1-mesa-glx \
    && rm -rf /var/lib/apt/lists/*

# Create non-root user
RUN useradd -m -s /bin/bash taskmanager

# Copy built application
COPY --from=builder /app/target/image /opt/taskmanager
RUN chown -R taskmanager:taskmanager /opt/taskmanager

# Switch to non-root user
USER taskmanager

# Set environment variables
ENV DISPLAY=:0
ENV PATH="/opt/taskmanager/bin:${PATH}"

# Expose port (if you add web features later)
EXPOSE 8080

# Set working directory
WORKDIR /home/taskmanager

# Command to run the application
CMD ["taskmanager"]