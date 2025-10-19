package com.fsa_profgroep_4.repository

import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.datetime.*

object EngineTypeTable : Table("dbo.engine_type") {
    val id = integer("id").autoIncrement()
    val code = varchar("code", 50)
    val description = varchar("description", 250).nullable()
    override val primaryKey = PrimaryKey(id)
}

object VehicleModelTable : Table("dbo.vehicle_model") {
    val id = integer("id").autoIncrement()
    val brand = varchar("brand", 100)
    val model = varchar("model", 100)
    val year = integer("year")
    val category = varchar("category", 50).nullable()
    val seats = integer("seats").nullable()
    val engineTypeId = reference("engine_type_id", EngineTypeTable.id).nullable()
    override val primaryKey = PrimaryKey(id)
}

object VehicleTable : Table("dbo.vehicle") {
    val id = integer("id").autoIncrement()
    val licensePlate = varchar("license_plate", 20).uniqueIndex()
    val status = varchar("status", 50)
    val vin = varchar("vin", 20).uniqueIndex()
    val vehicleModelId = reference("vehicle_model_id", VehicleModelTable.id).nullable()
    override val primaryKey = PrimaryKey(id)
}

object UsersTable : Table("dbo.users") {
    val id = integer("id").autoIncrement()
    val firstName = varchar("first_name", 100)
    val middleName = varchar("middle_name", 100).nullable()
    val lastName = varchar("last_name", 100)
    val dateOfBirth = date("date_of_birth")
    val email = varchar("email", 255)
    val username = varchar("username", 100)
    val password = varchar("password", 255)
    val creationDate = datetime("creation_date")
    override val primaryKey = PrimaryKey(id)
}

object OwnershipTable : Table("dbo.ownership") {
    val id = integer("id").autoIncrement()
    val userId = reference("user_id", UsersTable.id).nullable()
    val vehicleId = reference("vehicle_id", VehicleTable.id).nullable()
    override val primaryKey = PrimaryKey(id)
}

object OdometerTable : Table("dbo.odometer") {
    val id = integer("id").autoIncrement()
    val vehicleId = reference("vehicle_id", VehicleTable.id).nullable()
    val date = datetime("date")
    val mileage = decimal("mileage", 10, 2).nullable()
    override val primaryKey = PrimaryKey(id)
}

object LocationTable : Table("dbo.location") {
    val id = integer("id").autoIncrement()
    val vehicleId = reference("vehicle_id", VehicleTable.id).nullable()
    val latitude = decimal("latitude", 10, 6).nullable()
    val longitude = decimal("longitude", 10, 6).nullable()
    val date = datetime("date").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}

object PaymentTable : Table("dbo.payment") {
    val id = integer("id").autoIncrement()
    val amount = decimal("amount", 10, 2)
    val currency = varchar("currency", 10)
    val paymentMethod = varchar("payment_method", 50).nullable()
    val status = varchar("status", 50).nullable()
    val paymentDate = datetime("payment_date").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}

object ReservationTable : Table("dbo.reservation") {
    val id = integer("id").autoIncrement()
    val userId = reference("user_id", UsersTable.id).nullable()
    val vehicleId = reference("vehicle_id", VehicleTable.id).nullable()
    val startDate = datetime("start_date")
    val endDate = datetime("end_date")
    val status = varchar("status", 50).nullable()
    val paymentId = reference("payment_id", PaymentTable.id).nullable()
    override val primaryKey = PrimaryKey(id)
}

object MaintenanceTable : Table("dbo.maintenance") {
    val id = integer("id").autoIncrement()
    val vehicleId = reference("vehicle_id", VehicleTable.id).nullable()
    val startDate = date("start_date")
    val endDate = date("end_date").nullable()
    val cost = decimal("cost", 10, 2).nullable()
    val description = text("description").nullable()
    override val primaryKey = PrimaryKey(id)
}
