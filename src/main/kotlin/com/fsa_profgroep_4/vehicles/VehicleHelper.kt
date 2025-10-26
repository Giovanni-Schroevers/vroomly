package com.fsa_profgroep_4.vehicles

import com.fsa_profgroep_4.vehicles.types.EngineType
import com.fsa_profgroep_4.vehicles.types.Vehicle
import com.fsa_profgroep_4.vehicles.types.VehicleCategory
import com.fsa_profgroep_4.vehicles.types.VehicleStatus
import java.time.LocalDate

object VehicleHelper {
    fun generateVehicles(count: Int = 10): List<Vehicle> {
        val brands = listOf("Toyota", "Volkswagen", "BMW", "Audi", "Mercedes", "Ford", "Honda", "Nissan", "Kia", "Hyundai")
        val models = listOf("Corolla", "Golf", "3 Series", "A4", "C-Class", "Focus", "Civic", "Altima", "Sportage", "i30")
        val colors = listOf("Silver", "White", "Black", "Blue", "Red", "Grey", "Green")
        val statuses = VehicleStatus.entries

        return (1..count).map { index ->
            val status = statuses.random()
            val ownerId = 3 // For simplicity, all vehicles belong to owner with ID 3

            Vehicle(
                ownerId = ownerId,
                brand = brands.random(),
                model = models.random(),
                licensePlate = "%03d-%s-%02d".format(index, ('A'..'Z').random().toString(), (10..99).random()),
                vin = (1..17).joinToString("") { ('A'..'Z').random().toString() + (0..9).random() }.take(20),
                motValidTill = LocalDate.of(
                    2025 + (index % 3),
                    ((index % 12) + 1),
                    ((index % 28) + 1)
                ).toString(),
                odometerKm = (10_000..120_000).random().toDouble(),
                seats = listOf(2, 4, 5, 7).random(),
                color = colors.random(),
                status = status,
                costPerDay = (30..120).random().toDouble(),
                engineType = EngineType.entries.random(),
                images = emptyList(),
                category = VehicleCategory.entries.toTypedArray().random(),
                reviewStars = listOf(3.5, 4.0, 4.5, 5.0).random(),
                year = (2000..2025).random(),
                zeroToHundred = (6..12).random().toDouble(),
                vehicleModelId = null // This doesn't need to be set for generating vehicles
            )
        }
    }
}
