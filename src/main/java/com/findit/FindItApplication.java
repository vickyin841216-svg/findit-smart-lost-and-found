package com.findit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FindItApplication {

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("   🔍 FindIt – Smart Lost & Found Management System       ");
        System.out.println("   Powered by Java 17 & Spring Boot 3                    ");
        System.out.println("==========================================================");
        SpringApplication.run(FindItApplication.class, args);
        System.out.println("\n🚀 FindIt is LIVE and running!");
        System.out.println("👉 Web Portal URL:   http://localhost:8080");
        System.out.println("👉 H2 Database URL:  http://localhost:8080/h2-console");
        System.out.println("👉 REST API Root:    http://localhost:8080/api/items\n");
    }
}
