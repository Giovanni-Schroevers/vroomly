package com.fsa_profgroep_4.driving_report

import com.fsa_profgroep_4.driving_report.types.LocationSnapshot
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max

class SafeDrivingService(val locationSnapshots: List<LocationSnapshot>) {
    private fun haversineDistanceMeters(
        startLocation: LocationSnapshot,
        endLocation: LocationSnapshot
    ): Double {
        // Mean Earth radius in meters (authalic average ≈ 6,371,008.8 m)
        val earthRadiusMeters = 6_371_008.8

        val lat1Rad = Math.toRadians(startLocation.latitude)
        val lat2Rad = Math.toRadians(endLocation.latitude)
        val dLatRad = Math.toRadians(endLocation.latitude - startLocation.latitude)
        val dLonRad = Math.toRadians(endLocation.longitude - startLocation.longitude)

        val sinHalfDLat = sin(dLatRad / 2.0)
        val sinHalfDLon = sin(dLonRad / 2.0)

        val a = sinHalfDLat * sinHalfDLat +
                cos(lat1Rad) * cos(lat2Rad) * sinHalfDLon * sinHalfDLon
        val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))

        return earthRadiusMeters * c
    }

    fun checkForUnsafeAcceleration(
        zeroToHundredTime: Double,
        measuredAcceleration: Double,
    ): Pair<Boolean, Double> {
        val kMin = 1.15  // multiplier for very quick cars
        val kMax = 1.80  // multiplier for slow cars
        val tRefSec = 13.0 // reference 0-100 time
        val v100 = 27.78 // 100 km/h in m/s
        val aAvg = v100 / zeroToHundredTime

        // Flattening multiplier k(t01)
        val norm = ln(zeroToHundredTime + 1.0) / ln(tRefSec)
        val k = kMin + (kMax - kMin) * norm.coerceIn(0.0, 1.0)
        val thr = k * aAvg

        val severityPct = max(0.0, (measuredAcceleration - thr) / thr) * 100.0
        return (severityPct > 0.0) to severityPct
    }

    fun checkForMaxSpeedViolations(): Map<String, Double> {
        val incidents = mutableMapOf<String, Double>()

        for ((index, locationSnapshot) in locationSnapshots.withIndex()) {
            if (index == 0) {
                continue
            }

            val prevLocationSnapshot = locationSnapshots[index - 1]
            val distance = haversineDistanceMeters(prevLocationSnapshot, locationSnapshot)
            val speed = distance / (locationSnapshot.timestamp - prevLocationSnapshot.timestamp) * 3.6

            val maxSpeed = 100.0
            val overSpeed = max(0.0, speed - maxSpeed)
            val growthArg = (overSpeed / 10.0).coerceAtMost(709.0)
            val penalty = 1.0 * (exp(growthArg) - 1.0)

            if (overSpeed > 0.0) {
                incidents["speed violation at ${locationSnapshot.timestamp}"] = penalty
            }
        }
        return incidents
    }
}