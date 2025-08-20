package org.an.springai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
public class SpringAiApplication {

    public static void main(String[] args) {

        System.out.println("HTTP Proxy Host: " + System.getProperty("http.proxyHost"));
        System.out.println("HTTP Proxy Port: " + System.getProperty("http.proxyPort"));
        SpringApplication.run(SpringAiApplication.class, args);
    }

}
