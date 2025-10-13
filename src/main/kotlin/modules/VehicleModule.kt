package com.fsa_profgroep_4.modules

import com.fsa_profgroep_4.routes.vehicleOwnerRoute
import com.fsa_profgroep_4.routes.vehicleRenterRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.vehicleModule(){
    routing {
        route("/graphql/vehicle/owner") {
            vehicleOwnerRoute()
        }
        route("/graphql/vehicle/renter") {
            vehicleRenterRoute()
        }
    }
}
