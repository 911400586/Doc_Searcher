package com.example.doc_searcher;

import com.example.doc_searcher.search.Parse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DocSearcherApplication {
    public static void main(String[] args) {
        SpringApplication.run(DocSearcherApplication.class, args);
    }

}
