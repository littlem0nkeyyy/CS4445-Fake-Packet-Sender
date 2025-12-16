# 1. GIAI ĐOẠN BUILD (Sử dụng Maven để đóng gói ứng dụng)
# Sử dụng một image Java Development Kit (JDK) phù hợp với Java 17
FROM maven:3.9.5-eclipse-temurin-17 AS builder

# Thiết lập thư mục làm việc bên trong container
WORKDIR /app


# Sao chép các file cấu hình Maven (pom.xml) trước để tận dụng Docker cache
COPY pom.xml .

# Tải các dependency trước (chỉ chạy khi pom.xml thay đổi)
RUN mvn dependency:go-offline

# Sao chép mã nguồn của bạn
COPY src ./src

# Đóng gói ứng dụng thành file JAR có thể thực thi được
RUN mvn package -DskipTests

# 2. GIAI ĐOẠN CHẠY (Sử dụng image JRE tối thiểu để giảm kích thước)
# Sử dụng một image Java Runtime Environment (JRE) nhẹ tương ứng Java 17
FROM eclipse-temurin:17-jre-alpine

# Thiết lập đối số (argument) để lấy tên file JAR từ giai đoạn BUILD
ARG JAR_FILE=target/*.jar

# Sao chép file JAR đã build từ giai đoạn 'builder'
# File này sẽ có dạng: traffic-simulator-0.0.1-SNAPSHOT.jar (tên tùy thuộc vào artifactId)
COPY --from=builder /app/${JAR_FILE} app.jar

# ENTRYPOINT: Lệnh chạy ứng dụng khi container khởi động
# -XX:+HeapDumpOnOutOfMemoryError: Rất quan trọng cho việc debug nghẽn RAM!
# -Xmx: Bạn có thể điều chỉnh RAM tối đa mà container sử dụng
ENTRYPOINT ["java", "-XX:+HeapDumpOnOutOfMemoryError", "-jar", "/app.jar"]