package com.fsa_profgroep_4.vehicles

import com.fsa_profgroep_4.repository.VehicleRepository
import com.fsa_profgroep_4.vehicles.types.EngineType
import com.fsa_profgroep_4.vehicles.types.TCO
import com.fsa_profgroep_4.vehicles.types.Vehicle
import com.fsa_profgroep_4.vehicles.types.VehicleCategory
import com.fsa_profgroep_4.vehicles.types.VehicleFilter
import com.fsa_profgroep_4.vehicles.types.VehicleImage
import com.fsa_profgroep_4.vehicles.types.VehicleLocation
import com.fsa_profgroep_4.vehicles.types.VehicleStatus
import com.fsa_profgroep_4.vehicles.types.VehicleTcoData
import com.fsa_profgroep_4.vehicles.types.VehicleTcoDataInput
import com.fsa_profgroep_4.vehicles.types.VehicleUpdate
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class VehicleResolverTests {
    private lateinit var sharedRepo: InMemoryVehicleRepository
    private lateinit var mutation: VehiclesMutation
    private lateinit var query: VehiclesQuery

    @BeforeEach
    fun setUp() = runTest {
        sharedRepo = InMemoryVehicleRepository()
        mutation = VehiclesMutation(sharedRepo)
        query = VehiclesQuery(sharedRepo)

        query.createVehicle(
            Vehicle(
                id = 1,
                ownerId = 1,
                brand = "BMW",
                model = "M4",
                year = 2022,
                licensePlate = "ABC-123",
                vin = "1234",
                motValidTill = "1",
                odometerKm = 1000.0,
                seats = 4,
                color = "Blue",
                status = VehicleStatus.ACTIVE,
                category = VehicleCategory.SEDAN,
                images = emptyList(),
                costPerDay = 10.0,
                engineType = EngineType.DIESEL,
                zeroToHundred = 10.0,
                vehicleModelId = null,
                reviewStars = 4.0,
                location = VehicleLocation(0.0, 0.0, "test straat 10")
            )
        )

        mutation.saveVehicleTcoData(
            VehicleTcoDataInput(
                vehicleId = 1,
                acquisitionCost = 20_000.0,
                currentMarketValue = 15_000.0,
                maintenanceCosts = 1_000.0,
                fuelConsumptionPer100Km = 5.0,
                fuelPricePerLiter = 2.5,
                insuranceCostsPerYear = 100.0,
                taxAndRegistrationPerYear = 100.0,
                yearsOwned = 5
            )
        )
    }

    @Test
    @DisplayName("createVehicle should add a vehicle to the repository and return it")
    fun createVehicleAddsVehicle() = runTest {
        val created = query.createVehicle(
            Vehicle(
                id = 2,
                ownerId = 2,
                brand = "Audi",
                model = "A4",
                year = 2021,
                licensePlate = "DEF-456",
                vin = "5678",
                motValidTill = "1",
                odometerKm = 500.0,
                seats = 5,
                color = "Black",
                status = VehicleStatus.ACTIVE,
                category = VehicleCategory.SEDAN,
                images = emptyList(),
                costPerDay = 12.5,
                engineType = EngineType.PETROL,
                zeroToHundred = 9.0,
                vehicleModelId = null,
                reviewStars = 4.5,
                location = VehicleLocation(0.0, 0.0, "test straat 10")
            )
        )
        assertEquals(2, created.id)
        val all = query.getAllVehicles()
        assertTrue(all.any { it.id == 2 })
    }

    @Test
    @DisplayName("addImageToVehicle should append an image and keep numbering consistent")
    fun addImageToVehicleAppendsImage() = runTest {
        query.addImageToVehicle(1, "https://example.com/img.jpg", null)
        val reloaded = query.getVehicleById(1)!!
        assertEquals(1, reloaded.images.size)
        assertEquals("https://example.com/img.jpg", reloaded.images[0].url)
    }

    @Test
    @DisplayName("getAllVehicles should return the full list of vehicles")
    fun getAllVehiclesReturnsList() = runTest {
        val list = query.getAllVehicles()
        assertFalse(list.isEmpty())
        assertTrue(list.any { it.id == 1 })
    }

    @Test
    @DisplayName("getVehiclesByOwnerId should filter vehicles by owner")
    fun getVehiclesByOwnerIdFilters() = runTest {
        query.createVehicle(
            Vehicle(
                id = 3,
                ownerId = 99,
                brand = "Tesla",
                model = "Model 3",
                year = 2020,
                licensePlate = "TES-333",
                vin = "9999",
                motValidTill = "1",
                odometerKm = 200.0,
                seats = 5,
                color = "White",
                status = VehicleStatus.ACTIVE,
                category = VehicleCategory.SEDAN,
                images = emptyList(),
                costPerDay = 20.0,
                engineType = EngineType.ELECTRIC,
                zeroToHundred = 5.5,
                vehicleModelId = null,
                reviewStars = 5.0,
                location = VehicleLocation(0.0, 0.0, "test straat 10")
            )
        )
        val mine = query.getVehiclesByOwnerId(1)
        val others = query.getVehiclesByOwnerId(99)
        assertTrue(mine.all { it.ownerId == 1 })
        assertTrue(others.all { it.ownerId == 99 })
    }

    @Test
    @DisplayName("getVehicleById should return the vehicle when it exists and null otherwise")
    fun getVehicleByIdReturnsOrNull() = runTest {
        val one = query.getVehicleById(1)
        val missing = query.getVehicleById(12345)
        assertNotNull(one)
        assertNull(missing)
    }

    @Test
    @DisplayName("getPaginatedVehicles should return the requested page size")
    fun getPaginatedVehiclesPaginates() = runTest {
        (2..6).forEach { id ->
            query.createVehicle(
                Vehicle(
                    id = id,
                    ownerId = id,
                    brand = "Brand$id",
                    model = "Model$id",
                    year = 2020,
                    licensePlate = "LIC-$id",
                    vin = "$id",
                    motValidTill = "1",
                    odometerKm = id * 10.0,
                    seats = 4,
                    color = "Color$id",
                    status = VehicleStatus.ACTIVE,
                    category = VehicleCategory.SEDAN,
                    images = emptyList(),
                    costPerDay = 15.0,
                    engineType = EngineType.PETROL,
                    zeroToHundred = 8.0,
                    vehicleModelId = null,
                    reviewStars = 4.0,
                    location = VehicleLocation(0.0, 0.0, "test straat 10")
                )
            )
        }
        val page = query.getPaginatedVehicles(2, 2)
        assertEquals(2, page.size)
    }

    @Test
    @DisplayName("searchVehicles should apply provided filters and paginate")
    fun searchVehiclesFiltersAndPaginates() = runTest {
        query.createVehicle(
            Vehicle(
                id = 100,
                ownerId = 1,
                brand = "BMW",
                model = "M3",
                year = 2021,
                licensePlate = "BMW-100",
                vin = "VIN100",
                motValidTill = "1",
                odometerKm = 100.0,
                seats = 4,
                color = "Blue",
                status = VehicleStatus.ACTIVE,
                category = VehicleCategory.SEDAN,
                images = emptyList(),
                costPerDay = 30.0,
                engineType = EngineType.PETROL,
                zeroToHundred = 6.0,
                vehicleModelId = null,
                reviewStars = 4.0,
                location = VehicleLocation(0.0, 0.0, "test straat 10")
            )
        )
        query.createVehicle(
            Vehicle(
                id = 100,
                ownerId = 1,
                brand = "Audi",
                model = "M3",
                year = 2021,
                licensePlate = "BMW-100",
                vin = "VIN100",
                motValidTill = "1",
                odometerKm = 100.0,
                seats = 4,
                color = "Blue",
                status = VehicleStatus.ACTIVE,
                category = VehicleCategory.SEDAN,
                images = emptyList(),
                costPerDay = 30.0,
                engineType = EngineType.PETROL,
                zeroToHundred = 6.0,
                vehicleModelId = null,
                reviewStars = 4.0,
                location = VehicleLocation(0.0, 0.0, "test straat 10")
            )
        )
        val filters = VehicleFilter(brand = "BMW")
        val page = query.searchVehicles(filters, paginationAmount = 5, paginationPage = 1)
        assertTrue(page.isNotEmpty())
        assertTrue(page.all { it.brand == "BMW" })
    }

    @Test
    @DisplayName("vehicleTcoById should compute TCO from stored data")
    fun vehicleTcoByIdComputes() = runTest {
        val tco: TCO = query.vehicleTcoById(1)
        assertTrue(tco.tcoValue >= 0)
    }

    @Test
    @DisplayName("getVehicleTcoData should return persisted TCO data for a vehicle")
    fun getVehicleTcoDataReturnsData() = runTest {
        val data = query.getVehicleTcoData(1)
        assertNotNull(data)
        assertEquals(1, data!!.vehicleId)
    }

    @Test
    @DisplayName("vehicleConsumptionById should compute per-km consumption when data exists")
    fun vehicleConsumptionByIdComputes() = runTest {
        val result = query.vehicleConsumptionById(1)
        assertNotNull(result)
        assertTrue(result!!.litersPerKm >= 0)
        assertTrue(result.costPerKm >= 0)
    }

    @Test
    @DisplayName("seedVehicleData should create the requested number of vehicles")
    fun seedVehicleDataCreates() = runTest {
        val seeded = query.seedVehicleData(3, 1)
        assertEquals(3, seeded.size)
        val all = query.getAllVehicles()
        assertTrue(all.size >= 4)
    }

    @Test
    @DisplayName("saveVehicleTcoData should persist TCO data for a vehicle")
    fun saveVehicleTcoDataPersists() = runTest {
        query.createVehicle(
            Vehicle(
                id = 10,
                ownerId = 2,
                brand = "VW",
                model = "Golf",
                year = 2019,
                licensePlate = "VW-10",
                vin = "VIN10",
                motValidTill = "1",
                odometerKm = 1000.0,
                seats = 5,
                color = "Grey",
                status = VehicleStatus.ACTIVE,
                category = VehicleCategory.HATCHBACK,
                images = emptyList(),
                costPerDay = 25.0,
                engineType = EngineType.DIESEL,
                zeroToHundred = 9.5,
                vehicleModelId = null,
                reviewStars = 4.0,
                location = VehicleLocation(0.0, 0.0, "test straat 10")
            )
        )
        val input = VehicleTcoDataInput(
            vehicleId = 10,
            acquisitionCost = 18_000.0,
            currentMarketValue = 12_000.0,
            maintenanceCosts = 800.0,
            fuelConsumptionPer100Km = 5.5,
            fuelPricePerLiter = 2.0,
            insuranceCostsPerYear = 90.0,
            taxAndRegistrationPerYear = 110.0,
            yearsOwned = 4
        )
        val saved = mutation.saveVehicleTcoData(input)
        assertNotNull(saved)
        val stored = query.getVehicleTcoData(10)
        assertNotNull(stored)
    }

    @Test
    @DisplayName("updateVehicleTcoData should return updated TCO data for the vehicle")
    fun updateVehicleTcoDataUpdates() = runTest {
        val updated = mutation.updateVehicleTcoData(
            VehicleTcoDataInput(
                vehicleId = 1,
                acquisitionCost = 21_000.0,
                currentMarketValue = 14_000.0,
                maintenanceCosts = 1_100.0,
                fuelConsumptionPer100Km = 5.1,
                fuelPricePerLiter = 2.6,
                insuranceCostsPerYear = 120.0,
                taxAndRegistrationPerYear = 120.0,
                yearsOwned = 6
            )
        )
        assertNotNull(updated)
        assertEquals(1, updated!!.vehicleId)
    }

    @Test
    @DisplayName("updateVehicle should return an updated vehicle object")
    fun updateVehicleUpdates() = runTest {
        val data = VehicleUpdate(
            id = 1,
            brand = "BMW",
            model = "M4 Updated",
            color = "Red",
            licensePlate = "ABC-123",
            vin = "1234",
            motValidTill = "1",
            year = 2023,
            odometerKm = 1000.0,
            ownerId = 1,
            seats = 4,
            status = VehicleStatus.ACTIVE,
            category = VehicleCategory.SEDAN,
            images = listOf(),
            costPerDay = 10.0,
            engineType = EngineType.ELECTRIC,
            zeroToHundred = 8.0,
            reviewStars = 4.0,
            vehicleModelId = 1,
            location = VehicleLocation(0.0, 0.0, "test straat 10")
        )
        val updated = mutation.updateVehicle(
            data
        )
        assertEquals(data.id, updated.id)
        assertEquals(data.model, updated.model)
    }

    @Test
    @DisplayName("deleteVehicle should remove the vehicle and return it")
    fun deleteVehicleRemoves() = runTest {
        val deleted = mutation.deleteVehicle(1)
        assertEquals(1, deleted.id)
        val after = query.getVehicleById(1)
        assertNull(after)
    }

    @Test
    @DisplayName("removeImageFromVehicle should delete the image from the vehicle")
    fun removeImageFromVehicleDeletesImage() = runTest {
        query.addImageToVehicle(1, "https://example.com/pic1.jpg", null)
        val before = query.getVehicleById(1)!!
        assertEquals(1, before.images.size)

        mutation.removeImageFromVehicle(1, 1)
        val after = query.getVehicleById(1)!!
        assertEquals(0, after.images.size)
    }

    class InMemoryVehicleRepository : VehicleRepository {
        private val vehicles = mutableListOf<Vehicle>()
        private val tcoData = mutableListOf<VehicleTcoData>()

        override fun findById(id: Int): Vehicle? {
            return vehicles.find { it.id == id }
        }

        override fun findByOwnerId(ownerId: Int): List<Vehicle> {
            return vehicles.filter { it.ownerId == ownerId }
        }

        override fun getAllVehicles(): List<Vehicle> {
            return vehicles.toList()
        }

        override fun saveVehicle(vehicle: Vehicle): Vehicle? {
            if (vehicles.add(vehicle)) return vehicle
            return null
        }

        override fun deleteVehicleById(vehicleId: Int): Vehicle? {
            val vehicle = vehicles.find { it.id == vehicleId } ?: return null
            vehicles.removeIf { it.id == vehicleId }
            return vehicle
        }

        override fun updateVehicle(vehicle: VehicleUpdate): Vehicle? {
            val existing = vehicles.find { it.id == vehicle.id } ?: return null
            val updated = existing.copy(
                brand = vehicle.brand ?: existing.brand,
                model = vehicle.model ?: existing.model,
                year = vehicle.year ?: existing.year,
                licensePlate = vehicle.licensePlate ?: existing.licensePlate,
                vin = vehicle.vin ?: existing.vin,
                motValidTill = vehicle.motValidTill ?: existing.motValidTill,
                odometerKm = vehicle.odometerKm ?: existing.odometerKm,
                seats = vehicle.seats ?: existing.seats,
                color = vehicle.color ?: existing.color,
                status = vehicle.status ?: existing.status,
                category = vehicle.category ?: existing.category,
                images = existing.images,
                costPerDay = vehicle.costPerDay ?: existing.costPerDay,
                engineType = vehicle.engineType ?: existing.engineType,
                zeroToHundred = vehicle.zeroToHundred ?: existing.zeroToHundred,
                vehicleModelId = vehicle.vehicleModelId ?: existing.vehicleModelId,
                reviewStars = vehicle.reviewStars ?: existing.reviewStars
            )
            vehicles.removeIf { it.id == existing.id }
            vehicles.add(updated)
            return updated
        }

        override fun addImageToVehicle(
            vehicleId: Int,
            imageUrl: String,
            number: Int?
        ): Vehicle? {
            val vehicle = vehicles.find { it.id == vehicleId } ?: return null
            val images = vehicle.images.toMutableList()
            images.add(
                VehicleImage(
                    id = vehicle.images.size + 1,
                    vehicleId = vehicleId,
                    url = imageUrl,
                    number = number ?: (vehicle.images.size + 1)
                )
            )
            val updated = vehicle.copy(images = images)
            vehicles.removeIf { it.id == vehicleId }
            vehicles.add(updated)
            return updated
        }

        override fun removeImageFromVehicle(
            vehicleId: Int,
            imageId: Int
        ): Vehicle? {
            val vehicle = vehicles.find { it.id == vehicleId } ?: return null
            val images = vehicle.images.filter { it.id != imageId }
            val updated = vehicle.copy(images = images)
            vehicles.removeIf { it.id == vehicleId }
            vehicles.add(updated)
            return updated
        }

        override fun getVehicleTcoData(vehicleId: Int): VehicleTcoData? {
            vehicles.find { it.id == vehicleId } ?: return null
            return tcoData.find { it.vehicleId == vehicleId }
        }

        override fun saveVehicleTcoData(data: VehicleTcoData): VehicleTcoData? {
            vehicles.find { it.id == data.vehicleId } ?: return null
            val newData = data.copy()
            tcoData.removeIf { it.vehicleId == data.vehicleId }
            tcoData.add(newData)
            return newData
        }

        override fun updateVehicleTcoData(data: VehicleTcoDataInput): VehicleTcoData? {
            val existing = tcoData.find { it.vehicleId == data.vehicleId } ?: return null
            val updated = existing.copy(
                acquisitionCost = data.acquisitionCost ?: existing.acquisitionCost,
                currentMarketValue = data.currentMarketValue ?: existing.currentMarketValue,
                maintenanceCosts = data.maintenanceCosts ?: existing.maintenanceCosts,
                fuelConsumptionPer100Km = data.fuelConsumptionPer100Km ?: existing.fuelConsumptionPer100Km,
                fuelPricePerLiter = data.fuelPricePerLiter ?: existing.fuelPricePerLiter,
                insuranceCostsPerYear = data.insuranceCostsPerYear ?: existing.insuranceCostsPerYear,
                taxAndRegistrationPerYear = data.taxAndRegistrationPerYear ?: existing.taxAndRegistrationPerYear,
                yearsOwned = data.yearsOwned ?: existing.yearsOwned
            )
            tcoData.removeIf { it.vehicleId == data.vehicleId }
            tcoData.add(updated)
            return updated
        }
    }
}