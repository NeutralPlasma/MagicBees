package eu.virtusdevelops.magicbees.api.models

import java.util.UUID

/**
 * Represents a beehive in the system.
 *
 * @property id Unique identifier of the beehive.
 * @property location Physical location of the beehive in the world.
 * @property owner UUID of the player who owns the beehive.
 * @property fullnessStatus Indicates the current fullness status of the hive.
 * @property bees Amount of bees in the beehive.
 * @property honeyUpgradeLevel Represents the honey production upgrade level of the hive.
 * @property honeyCombUpgradeLevel Represents the honeycomb production upgrade level of the hive.
 * @property combCollectedTimes Number of times comb has been collected from the hive.
 * @property lastCollectionTime Timestamp of the last comb collection from the hive.
 * @property createdTime Timestamp when the beehive was created.
 * @property modifiedTime Timestamp of the last modification to the beehive data.
 */
data class BeeHive(
    val id: UUID,
    val location: Location,
    val owner: UUID,
    var fullnessStatus: Int,
    var bees: Int,
    var honeyUpgradeLevel: Int,
    var honeyCombUpgradeLevel: Int,

    var combCollectedTimes: Int,
    var honeyCollectedTimes: Int,
    var lastCollectionTime: Long,

    var createdTime: Long,

    var modifiedTime: Long
) {
}