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
    val Year = integer("year")
    val Category = varchar("category", 50)
    val Seats = integer("seats")
    val EngineTypeId = integer("engine_type_id").references(EngineTypeTable.Id)
    override val primaryKey = PrimaryKey(Id)
}

object VehicleTable : Table("dbo.vehicle") {
    val Id = integer("id").autoIncrement()
    val LicensePlate = varchar("license_plate", 20).uniqueIndex()
    val Status = varchar("status", 50)
    val Vin = varchar("vin", 20).uniqueIndex()
    val VehicleModelId = reference("vehicle_model_id", VehicleModelTable.Id)
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

object OwnershipTable : Table("dbo.ownership") {
    val Id = integer("id").autoIncrement()
    val UserId = reference("user_id", UsersTable.Id)
    val VehicleId = reference("vehicle_id", VehicleTable.Id)
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

object PaymentTable : Table("dbo.payment") {
    val Id = integer("id").autoIncrement()
    val Amount = decimal("amount", 10, 2)
    val Currency = varchar("currency", 10)
    val PaymentMethod = varchar("payment_method", 50)
    val Status = varchar("status", 50)
    val PaymentDate = datetime("payment_date").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(Id)
}

object ReservationTable : Table("dbo.reservation") {
    val Id = integer("id").autoIncrement()
    val UserId = reference("user_id", UsersTable.Id)
    val VehicleId = reference("vehicle_id", VehicleTable.Id)
    val StartDate = datetime("start_date")
    val EndDate = datetime("end_date")
    val Status = varchar("status", 50)
    val PaymentId = reference("payment_id", PaymentTable.Id)
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

