package com.fsa_profgroep_4.vehicles

import com.fsa_profgroep_4.vehicles.types.Vehicle
import com.fsa_profgroep_4.vehicles.types.VehicleCategory
import com.fsa_profgroep_4.vehicles.types.VehicleImageType
import com.fsa_profgroep_4.vehicles.types.VehicleStatus
import java.time.LocalDate

object VehicleHelper {
    fun generateMockVehicles(count: Int = 10): List<Vehicle> {
        val brands = listOf("Toyota", "Volkswagen", "BMW", "Audi", "Mercedes", "Ford", "Honda", "Nissan", "Kia", "Hyundai")
        val models = listOf("Corolla", "Golf", "3 Series", "A4", "C-Class", "Focus", "Civic", "Altima", "Sportage", "i30")
        val colors = listOf("Silver", "White", "Black", "Blue", "Red", "Grey", "Green")
        val statuses = VehicleStatus.entries

        return (1..count).map { index ->
            val brandIndex = (index - 1) % brands.size
            val modelIndex = (index - 1) % models.size
            val colorIndex = (index - 1) % colors.size
            val status = statuses.random()
            val ownerId = (index % 3) + 1

            Vehicle(
                id = index,
                ownerId = ownerId,
                brand = brands[brandIndex],
                model = models[modelIndex],
                licensePlate = "%03d-%s-%02d".format(index, ('A'..'Z').random().toString(), (10..99).random()),
                vin = (1..17).joinToString("") { ('A'..'Z').random().toString() + (0..9).random() },
                motValidTill = LocalDate.of(
                    2025 + (index % 3),
                    ((index % 12) + 1),
                    ((index % 28) + 1)
                ).toString(),
                odometerKm = (10_000..120_000).random().toDouble(),
                seats = listOf(2, 4, 5, 7).random(),
                color = colors[colorIndex],
                status = status,
                costPerDay = (30..120).random().toDouble(),
                engineType = if (index % 2 == 0) com.fsa_profgroep_4.vehicles.types.EngineType.PETROL else com.fsa_profgroep_4.vehicles.types.EngineType.DIESEL,
                category = VehicleCategory.entries.toTypedArray().random(),
                reviewStars = listOf(3.5, 4.0, 4.5, 5.0).random(),
                year = (2020..2025).random()
            )
        }
    }
    fun generateMockVehicleImages(vehicles: List<Vehicle>): List<com.fsa_profgroep_4.vehicles.types.VehicleImage> {
        val images = mutableListOf<com.fsa_profgroep_4.vehicles.types.VehicleImage>()
        vehicles.forEach { vehicle ->
            val imageCount = (1..5).random()
            for (i in 1..imageCount) {
                images.add(
                    com.fsa_profgroep_4.vehicles.types.VehicleImage(
                        id = "${vehicle.id}-$i",
                        vehicleId = vehicle.id,
                        path = "https://vroomly.com/images/${vehicle.id}-$i.jpg",
                        type = VehicleImageType.entries.toTypedArray().random()
                    )
                )
            }
        }
        return images
    }
}
