package com.jetbrains.handson.chat.server

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import java.util.*
import kotlin.collections.LinkedHashSet


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(WebSockets)
    routing {
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        webSocket("/chat") {
            send("Adding the user!")
            val currentConnection = Connection(this)
            connections += currentConnection
            try{
                send("You are connected. There are ${connections.count()} users here.")
                for(frame in incoming){
                    frame as? Frame.Text ?:continue
                    val receivedText = frame.readText()
                    val textUsername = "[${currentConnection.name}] : $receivedText"
                    connections.forEach{
                        it.session.send(textUsername)
                    }

                }
            } catch (e: Exception){
                println(e.localizedMessage)
            } finally {
                println("Removing ${currentConnection.name}")
                connections -= currentConnection
            }
        }
    }
}


