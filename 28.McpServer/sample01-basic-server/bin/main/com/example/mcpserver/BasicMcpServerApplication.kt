package com.example.mcpserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class BasicMcpServerApplication

fun main(args: Array<String>) {
    runApplication<BasicMcpServerApplication>(*args)
}
