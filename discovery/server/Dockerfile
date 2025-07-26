FROM eclipse-temurin:21-jdk-alpine

# Install curl (Alpine uses apk as the package manager)
RUN apk add --no-cache curl

WORKDIR /app

ENV KUBERNETES_URL="kubernetes.default.svc"

EXPOSE 9393 50052

COPY /build/install/server ./

ENTRYPOINT ["./bin/server"]