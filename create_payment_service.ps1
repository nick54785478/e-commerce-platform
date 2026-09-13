$orderSvc = ".\order-service"
$paymentSvc = ".\payment-service"

# Create directories
New-Item -ItemType Directory -Force -Path "$paymentSvc\src\main\java\com\omni\payment"
New-Item -ItemType Directory -Force -Path "$paymentSvc\src\main\resources"

# 1. Create pom.xml
$pomContent = @"
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.omni.recommender</groupId>
        <artifactId>omni-recommender-platform</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <relativePath>../pom.xml</relativePath>
    </parent>
    <artifactId>payment-service</artifactId>
    <name>payment-service</name>
    <description>Payment Service</description>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.axonframework</groupId>
            <artifactId>axon-spring-boot-starter</artifactId>
            <version>4.10.0</version>
        </dependency>
        <dependency>
            <groupId>com.omni.recommender</groupId>
            <artifactId>payment-api</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
</project>
"@
Set-Content -Path "$paymentSvc\pom.xml" -Value $pomContent

# 2. Create application.yml
$ymlContent = @"
server:
  port: 8083
spring:
  application:
    name: payment-service
  datasource:
    url: jdbc:postgresql://postgres:5432/payment_db
    username: myuser
    password: mypassword
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
axon:
  axonserver:
    servers: axonserver:8124
"@
Set-Content -Path "$paymentSvc\src\main\resources\application.yml" -Value $ymlContent

# 3. Create Main Class
$mainContent = @"
package com.omni.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }
}
"@
Set-Content -Path "$paymentSvc\src\main\java\com\omni\payment\PaymentApplication.java" -Value $mainContent

# 4. Move classes from order-service
$orderApp = "$orderSvc\src\main\java\com\example\demo\application"
$orderIface = "$orderSvc\src\main\java\com\example\demo\iface"
$orderInfra = "$orderSvc\src\main\java\com\example\demo\infra"
$paymentApp = "$paymentSvc\src\main\java\com\omni\payment\application"
$paymentIface = "$paymentSvc\src\main\java\com\omni\payment\iface"
$paymentInfra = "$paymentSvc\src\main\java\com\omni\payment\infra"

New-Item -ItemType Directory -Force -Path "$paymentApp\domain\payment\aggregate"
New-Item -ItemType Directory -Force -Path "$paymentApp\service"
New-Item -ItemType Directory -Force -Path "$paymentIface\dto\req"
New-Item -ItemType Directory -Force -Path "$paymentIface\dto\res"
New-Item -ItemType Directory -Force -Path "$paymentIface\rest"
New-Item -ItemType Directory -Force -Path "$paymentInfra\mapper"
New-Item -ItemType Directory -Force -Path "$paymentInfra\processor"
New-Item -ItemType Directory -Force -Path "$paymentInfra\projection\payment"
New-Item -ItemType Directory -Force -Path "$paymentInfra\persistence"

Move-Item -Path "$orderApp\domain\payment\aggregate\Payment.java" -Destination "$paymentApp\domain\payment\aggregate\"
Move-Item -Path "$orderApp\service\*Payment*.java" -Destination "$paymentApp\service\"
Move-Item -Path "$orderIface\dto\req\*Payment*.java" -Destination "$paymentIface\dto\req\"
Move-Item -Path "$orderIface\dto\res\*Payment*.java" -Destination "$paymentIface\dto\res\"
Move-Item -Path "$orderIface\rest\*Payment*.java" -Destination "$paymentIface\rest\"
Move-Item -Path "$orderInfra\mapper\*Payment*.java" -Destination "$paymentInfra\mapper\"
Move-Item -Path "$orderInfra\processor\*Payment*.java" -Destination "$paymentInfra\processor\"
Move-Item -Path "$orderInfra\projection\payment\*.java" -Destination "$paymentInfra\projection\payment\"
Move-Item -Path "$orderInfra\persistence\*Payment*.java" -Destination "$paymentInfra\persistence\"

# Replace the packages in all moved java files
Get-ChildItem -Path "$paymentSvc\src\main\java\com\omni\payment" -Recurse -Filter *.java | ForEach-Object {
    (Get-Content $_.FullName) -replace 'package com.example.demo', 'package com.omni.payment' | Set-Content $_.FullName
}

# Update the command/event imports to point to payment-api
Get-ChildItem -Path "$paymentSvc\src\main\java\com\omni\payment" -Recurse -Filter *.java | ForEach-Object {
    $content = Get-Content $_.FullName
    $content = $content -replace 'import com.example.demo.application.command', 'import com.omni.payment.api.command'
    $content = $content -replace 'import com.example.demo.application.domain.payment.event', 'import com.omni.payment.api.event'
    $content = $content -replace 'import com.omni.order.api.dto.PaymentQueriedView', 'import com.omni.payment.api.dto.PaymentQueriedView'
    $content = $content -replace 'import com.example.demo.', 'import com.omni.payment.'
    $content | Set-Content $_.FullName
}
