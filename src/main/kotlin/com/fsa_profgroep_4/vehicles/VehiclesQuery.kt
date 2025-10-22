package com.fsa_profgroep_4.vehicles

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import com.fsa_profgroep_4.repository.RepositoryFactory
import com.fsa_profgroep_4.repository.VehicleRepository
import com.fsa_profgroep_4.vehicles.types.*
import io.ktor.server.application.ApplicationEnvironment

class VehiclesQuery(
    private val vehicleRepository: VehicleRepository
): Query {
    constructor(environment: ApplicationEnvironment): this(
    RepositoryFactory(environment).createVehicleRepository()
    )
    private val vehicleService: VehicleService = VehicleService()

    /** ========================================================
     *                     CREATE FUNCTIONS
     *  ======================================================== */
    /**
     * Creates an entry of a singular Vehicle in the database.
     *
     * @return a [Vehicle] object.
     */
    suspend fun createVehicle(vehicle: Vehicle): Vehicle{
        val repository = vehicleRepository
        return vehicleService.createVehicle(repository, vehicle)
    }

    /**
     * Adds an image to a vehicle in the database.
     *
     * @return the [Vehicle] object with the added image.
     */
    suspend fun addImageToVehicle(
        @GraphQLDescription("Vehicle Id to add image to")
        vehicleId: Int,

        @GraphQLDescription("Image URL to add to vehicle")
        imageUrl: String
    ): Vehicle {
        val repository = vehicleRepository
        return vehicleService.addImageToVehicle(repository, vehicleId, imageUrl)
    }
    /** ========================================================
     *                      READ FUNCTIONS
     *  ======================================================== */

    /**
     * Retrieves all vehicles from the database via the provided repository.
     *
     * @return a list of all [Vehicle] objects.
     */
    @GraphQLDescription("Get all vehicles")
    suspend fun getAllVehicles(): List<Vehicle> {
        val repository = vehicleRepository
        return vehicleService.getAllVehicles(repository)
    }

    /**
     * Retrieves all vehicles belonging to a specific owner.
     *
     * @param ownerId the ID of the vehicle owner.
     * @return a list of [Vehicle] objects owned by the specified user.
     */
    @GraphQLDescription("Get all vehicles from a specific vehicle owner")
    suspend fun getVehiclesByOwnerId(
        @GraphQLDescription("Vehicle owner (verhuurder) id")
        ownerId: Int,
    ): List<Vehicle> {
        val repository = vehicleRepository
        return vehicleService.getVehiclesByOwnerId(repository, ownerId)
    }

    /**
     * Retrieves a single vehicle.
     *
     * @param vehicleId the ID of the vehicle.
     * @return a [Vehicle] object.
     */
    @GraphQLDescription("Get detailed info about a specific vehicle")
    suspend fun getVehicleById(
        @GraphQLDescription("Vehicle Id")
        vehicleId: Int
    ): Vehicle? {
        val repository = vehicleRepository
        return vehicleService.getVehicleById(repository,vehicleId)
    }

    /**
     * Retrieves a paginated list of vehicles.
     *
     * @param paginationAmount the number of vehicles to return per page.
     * @param paginationPage the page number to retrieve.
     * @return a paginated list of [VehicleBasic] objects.
     */
    @GraphQLDescription("Get a paginated list of vehicles")
    suspend fun getPaginatedVehicles(
        @GraphQLDescription("The amount of vehicles to return per page")
        paginationAmount: Int,

        @GraphQLDescription("The page of vehicles to return")
        paginationPage: Int
    ): List<VehicleBasic> {
        val repository = vehicleRepository
        val vehicles = vehicleService.getAllVehicles(repository)

        val startIndex = (paginationPage - 1) * paginationAmount
        val pagedVehicles = vehicles.drop(startIndex).take(paginationAmount)

        return vehicleService.getBasicVehicleInfo(pagedVehicles)
    }

    /**
     * Retrieves vehicles matching optional filters, with pagination support.
     *
     * @param filters optional filters to apply (e.g. brand, engine type, cost range).
     * @param paginationAmount the number of vehicles to return per page.
     * @param paginationPage the page number to retrieve.
     * @return a paginated and filtered list of [VehicleBasic] objects.
     */
    @GraphQLDescription("Search vehicles with optional filters and pagination")
    suspend fun searchVehicles(
        @GraphQLDescription("Filters to apply to the vehicles")
        filters: VehicleFilter? = null,

        @GraphQLDescription("The amount of vehicles to return per page")
        paginationAmount: Int,

        @GraphQLDescription("The page of vehicles to return")
        paginationPage: Int
    ): List<VehicleBasic> {
        val repository = vehicleRepository
        var vehicles = vehicleService.getAllVehicles(repository)

        // Apply filters if provided
        filters?.let {
            vehicles = vehicles.filter { vehicle ->
                (it.brand == null || vehicle.brand.equals(it.brand, ignoreCase = true)) &&
                        (it.engineType == null || vehicle.engineType == it.engineType) &&
                        (it.minCostPerDay == null || vehicle.costPerDay >= it.minCostPerDay) &&
                        (it.maxCostPerDay == null || vehicle.costPerDay <= it.maxCostPerDay)
            }
        }

        val startIndex = (paginationPage - 1) * paginationAmount
        val pagedVehicles = vehicles.drop(startIndex).take(paginationAmount)

        return vehicleService.getBasicVehicleInfo(pagedVehicles)
    }
    /** ========================================================
     *                      UPDATE FUNCTIONS
     *  ======================================================== */

    /**
     * Updates a vehicle from the database.
     *
     * @return the deleted [Vehicle] object.
     */
    suspend fun updateVehicle(
        @GraphQLDescription("VehicleUpdate to update")
        vehicle: VehicleUpdate
    ): Vehicle {
        val repository = vehicleRepository
        return vehicleService.updateVehicle(repository, vehicle)
    }

    /** ========================================================
     *                      DELETE FUNCTIONS
     *  ======================================================== */

    /**
     * Deletes a vehicle from the database.
     *
     * @return the deleted [Vehicle] object.
     */
    suspend fun deleteVehicle(
        @GraphQLDescription("Vehicle Id to delete")
        vehicleId: Int
    ): Vehicle {
        val repository = vehicleRepository
        return vehicleService.deleteVehicle(repository, vehicleId)
    }

    /**
     * Deletes an image from a vehicle in the database.
     *
     * @return the [Vehicle] object the image got deleted from.
     */
    suspend fun removeImageFromVehicle(
        @GraphQLDescription("Vehicle Id to remove image from")
        vehicleId: Int,

        @GraphQLDescription("Image Id to remove from vehicle")
        imageId: Int
    ): Vehicle {
        val repository = vehicleRepository
        return vehicleService.removeImageFromVehicle(repository, vehicleId, imageId)
    }
    /** ========================================================
     *                      OTHER FUNCTIONS
     *  ======================================================== */
    @GraphQLDescription("Calculate Total Cost of Ownership for a vehicle")
    suspend fun vehicleTco(
        @GraphQLDescription("TCO calculation input")
        input: TcoInput
    ): VehicleTco {
        val repository = vehicleRepository

        val vehicle = vehicleService.getVehicleById(repository, input.id)
        return vehicleService.calculateTco(input, vehicle)
    }

    /**
     * Seeds the vehicle data in the database.
     *
     * @return a list of [Vehicle] objects.
     */
    suspend fun seedVehicleData(amountToSeed: Int): List<Vehicle>{
        val repository = vehicleRepository
        return vehicleService.vehicleDataSeeder(repository, amountToSeed)
    }
}