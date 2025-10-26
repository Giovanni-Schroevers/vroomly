package com.fsa_profgroep_4.repository

import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.datetime.*

object EngineTypeTable : Table("dbo.engine_type") {
    val Id = integer("id").autoIncrement()
    val Code = varchar("code", 50)
    val Description = varchar("description", 250).nullable()
    override val primaryKey = PrimaryKey(Id)
}

object VehicleModelTable : Table("dbo.vehicle_model") {
    val Id = integer("id").autoIncrement()
    val Brand = varchar("brand", 100)
    val Model = varchar("model", 100)
    val Color = varchar("color", 30)
    val Year = integer("year")
    val Category = varchar("category", 50)
    val Seats = integer("seats")
    val EngineTypeId = integer("engine_type_id").references(EngineTypeTable.Id)
    val ZeroToHundred = double("zero_to_hundred")
    override val primaryKey = PrimaryKey(Id)
}

object VehicleTable : Table("dbo.vehicle") {
    val Id = integer("id").autoIncrement()
    val LicensePlate = varchar("license_plate", 20).uniqueIndex()
    val Status = varchar("status", 50)
    val Vin = varchar("vin", 20).uniqueIndex()
    val ReviewStars = double("review_stars")
    val VehicleModelId = reference("vehicle_model_id", VehicleModelTable.Id)

    val OwnerId = reference(
        "owner_id",
        UsersTable.Id,
        onDelete = ReferenceOption.CASCADE,
    )

    override val primaryKey = PrimaryKey(Id)
}

object UsersTable : Table("dbo.users") {
    val Id = integer("id").autoIncrement()
    val FirstName = varchar("first_name", 100)
    val MiddleName = varchar("middle_name", 100).nullable()
    val LastName = varchar("last_name", 100)
    val DateOfBirth = date("date_of_birth")
    val Email = varchar("email", 255)
    val Username = varchar("username", 100)
    val Password = varchar("password", 255)
    val CreationDate = datetime("creation_date")
    override val primaryKey = PrimaryKey(Id)
}

object VehicleTcoDataTable : Table("dbo.vehicle_tco_data") {
    val Id = integer("id").autoIncrement()
    val VehicleId = reference("vehicle_id", VehicleTable.Id, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val AcquisitionCost = decimal("acquisition_cost", 12, 2)
    val CurrentMarketValue = decimal("current_market_value", 12, 2)
    val MaintenanceCosts = decimal("maintenance_costs", 12, 2)
    val FuelConsumptionPer100Km = decimal("fuel_consumption_per_100km", 5, 2)
    val FuelPricePerLiter = decimal("fuel_price_per_liter", 5, 2)
    val InsuranceCostsPerYear = decimal("insurance_costs_per_year", 12, 2)
    val TaxAndRegistrationPerYear = decimal("tax_and_registration_per_year", 12, 2)
    val YearsOwned = integer("years_owned")
    override val primaryKey = PrimaryKey(Id)
}


object VehicleImageTable : Table("dbo.vehicle_images") {
    val Id = integer("id").autoIncrement()
    val VehicleId = reference("vehicle_id", VehicleTable.Id)
    val Number = integer("number")
    val Url = varchar("url", 512)

    init {
        uniqueIndex(VehicleId, Number)
    }

    override val primaryKey = PrimaryKey(Id)
}

object OdometerTable : Table("dbo.odometer") {
    val Id = integer("id").autoIncrement()
    val VehicleId = reference("vehicle_id", VehicleTable.Id)
    val Date = datetime("date")
    val Mileage = decimal("mileage", 10, 2)
    override val primaryKey = PrimaryKey(Id)
}

object LocationTable : Table("dbo.location") {
    val Id = integer("id").autoIncrement()
    val VehicleId = reference("vehicle_id", VehicleTable.Id)
    val Latitude = decimal("latitude", 10, 6)
    val Longitude = decimal("longitude", 10, 6)
    val Date = datetime("date").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(Id)
}

object ReservationTable : Table("dbo.reservation") {
    val Id = integer("id").autoIncrement()
    val UserId = reference("user_id", UsersTable.Id)
    val VehicleId = reference("vehicle_id", VehicleTable.Id)
    val StartDate = date("start_date")
    val EndDate = date("end_date")
    val Status = varchar("status", 50)
    val TotalCost = double("total_cost")
    val Paid = bool("paid")
    val CreationDate = date("creation_date")
    override val primaryKey = PrimaryKey(Id)
}

object MaintenanceTable : Table("dbo.maintenance") {
    val Id = integer("id").autoIncrement()
    val VehicleId = reference("vehicle_id", VehicleTable.Id)
    val StartDate = date("start_date")
    val EndDate = date("end_date").nullable()
    val Cost = decimal("cost", 10, 2)
    val Description = text("description")
    override val primaryKey = PrimaryKey(Id)
}

object DrivingReportTable : Table("dbo.driving_report") {
    val Id = integer("id").autoIncrement()
    val SafetyScore = double("safety_score")
    val ReservationId = reference("reservation_id", ReservationTable.Id)
    val Date = date("date")
}

object ViolationTable : Table("dbo.violation") {
    val Id = integer("id").autoIncrement()
    val Description = varchar("description", 255)
    val Score = double("score")
    val DrivingReportId = reference("driving_report_id", DrivingReportTable.Id)
}

