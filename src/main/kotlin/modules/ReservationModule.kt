package com.fsa_profgroep_4.modules

import com.fsa_profgroep_4.routes.authRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.reservationModule(){
    routing {
        route("/api/reservation") {
            reservationModule()
        }
    }
}
