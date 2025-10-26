# vroomly

## Installation
1. Use git clone, or install the zip to get the application on your device.
2. Copy the `.env.example` and name it `.env`. Make sure to fill in the environment variables.
3. Run `./gradlew` build to make sure everything builds properly.
4. Run `./gradlew` run to start running the application (refer to [Building & Running](https://github.com/Giovanni-Schroevers/vroomly?tab=readme-ov-file#building--running) for other operations.)
5. When running locally, open the [GraphiQL playground](http://127.0.0.1:5000/playground).
6. You may refer to the [GraphQL queries](http://127.0.0.1:5000/playground) for all possible queries.


## Building & Running

To build or run the project, use one of the following tasks:

| Task                                    | Description                                                          |
|-----------------------------------------|----------------------------------------------------------------------|
| `./gradlew test`                        | Run the tests                                                        |
| `./gradlew build`                       | Build everything                                                     |
| `./gradlew buildFatJar`                 | Build an executable JAR of the server with all dependencies included |
| `./gradlew buildImage`                  | Build the docker image to use with the fat JAR                       |
| `./gradlew publishImageToLocalRegistry` | Publish the docker image locally                                     |
| `./gradlew run`                         | Run the server                                                       |
| `./gradlew runDocker`                   | Run using the local docker image                                     |
| `./gradlew :buildFatJar`                | Builds a .jar file for aws deployments                               |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```

## GraphQL queries

### Vehicle queries
| Operation              | Query/Mutation | Description                                                          | Parameters                                                                                 |
|------------------------|----------------|----------------------------------------------------------------------|--------------------------------------------------------------------------------------------|
| getAllVehicles         | Query          | Get all registered vehicles                                          | `n/a`                                                                                      |
| getVehiclesByOwnerId   | Query          | Get all registered vehicles filtered by owner id                     | `ownerId: Int`                                                                             |
| getVehicleById         | Query          | Get specific vehicle filtered by id                                  | `vehicleId: int`                                                                           |
| getPaginatedVehicles   | Query          | Get a paginated list of all vehicles                                 | `paginationAmount: Int`, `paginationPage: Int`                                             |
| searchVehicles         | Query          | Get all vehicles paginated with optional filter parameters           | `filters: VehicleFilter? = null`, `paginationAmount: Int`, `paginationPage: Int`           |
| createVehicle          | Query          | Create a vehicle                                                     | `vehicle: Vehicle`                                                                         |
| addImageToVehicle      | Query          | Add an Image to a vehicle                                            | `vehicleId: Int`, `imageUrl: String`, `number: Int?`(positions image, 0 is the main image) |
| updateVehicle          | Mutation       | Update vehicle's data                                                | `vehicle: VehicleUpdate`                                                                   |
| deleteVehicle          | Mutation       | Delete vehicle from the database                                     | `vehicleId: Int`                                                                           |
| removeImageFromVehicle | Mutation       | Removes an image from a vehicle                                      | `vehicleId: Int`                                                                           |
| vehicleTcoById         | Query          | Get calculated vehicle's TCO (Total Cost of Ownership) by vehicle id | `vehicleId: Int`                                                                           |
| vehicleConsumptionById | Query          | Get vehicle's fuel consumption and cost per kilometer                | `vehicleId: Int`                                                                           |
| saveVehicleTcoData     | Mutation       | Save TCO data for a vehicle                                          | `input: VehicleTcoDataInput`                                                               |
| updateVehicleTcoData   | Mutation       | Update vehicle's TCO data                                            | `input: VehicleTcoDataInput`                                                               |


### User queries

| Operation    | Query/Mutation | Description                                      | Parameters                                         |
|--------------|----------------|--------------------------------------------------|----------------------------------------------------|
| login        | Query          | Login using existing user details                | `input: LoginInput`                                |
| registerUser | Mutation       | Register as a user with new unique login details | `input: RegisterInput`                             |
| editUser     | Mutation       | Edit user details                                | `input: EditInput`, `env: DataFetchingEnvironment` |
| deleteUser   | mutation       | Deletes a user                                   | `env: DataFetchingEnvironment`                     |


### Reservation queries

| Operation                  | Query/Mutation | Description                                 | Parameters                                                                      |
|----------------------------|----------------|---------------------------------------------|---------------------------------------------------------------------------------|
| createReservation          | Query          | Creates a reservation for a vehicle         | `vehicleId: Int`, `renterId: Int`, `startDate: LocalDate`, `endDate: LocalDate` |
| getReservation             | Query          | Get the requested reservation               | `reservationId: Int`                                                            |
| getReservationsByVehicleId | Query          | Get all reservations for a specific vehicle | `vehicleId: Int`                                                                |
| getReservationByRenterId   | Query          | Get all reservations from a renter          | `renterId: Int`                                                                 |
| updateReservation          | Query          | Update a specific reservation               | `input: ReservationUpdate`                                                      |
| deleteReservation          | Query          | Delete a specific reservation               | `reservationId: Int`                                                            |
