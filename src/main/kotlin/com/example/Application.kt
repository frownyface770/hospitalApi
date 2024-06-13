package com.example

import com.example.models.DatabseFactory
import com.example.plugins.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabseFactory.init()
    configureSerialization()
    //configureDatabases()
    configureSecurity()
    configureRouting()
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.ContentType)
        //We have to register any custom header here for it to allow cors
        //header("X-My-Header")
        allowCredentials = true
        anyHost() // You can restrict this in production
         allowHost("localhost:63342") // Specify your client domain and port if needed
    }
}
