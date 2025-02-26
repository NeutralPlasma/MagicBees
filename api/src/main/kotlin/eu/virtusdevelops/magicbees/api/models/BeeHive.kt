package eu.virtusdevelops.magicbees.api.models

import kotlin.properties.Delegates
import java.util.UUID

/**
 * Represents a beehive with various properties and upgrade levels, capable of tracking its state and position in the world.
 *
 * @property id Unique identifier for the beehive.
 * @property location Physical location of the beehive, represented by a HiveLocation.
 * @property owner Unique identifier of the owner of the beehive.
 * @property createdTime The timestamp when the beehive was created.
 * @property updated Indicates whether the beehive has been updated. Defaults to false but is set to true when observable properties are modified.
 * @property fullnessStatus Current fullness level of the beehive, reflecting honey processing progress.
 * @property bees Current number of bees in the beehive.
 * @property honeyUpgradeLevel Current upgrade level of honey production for the beehive.
 * @property honeyCombUpgradeLevel Current upgrade level of honeycomb production for the beehive.
 * @property combCollectedTimes Total number of times honeycombs have been collected from the beehive.
 * @property honeyCollectedTimes Total number of times honey has been collected from the beehive.
 * @property lastCollectionTime Timestamp of the most recent collection event from the beehive.
 * @property modifiedTime The timestamp indicating the last modification to the beehive's state.
 */
data class BeeHive(
    val id: UUID,
    val location: HiveLocation,
    val owner: UUID,
    val createdTime: Long,
    var updated: Boolean = false
) : StoredItem {

    constructor(
        id: UUID,
        location: HiveLocation,
        owner: UUID,
        modifiedTime: Long = 0,
        fullnessStatus: Int = 0,
        bees: Int = 0,
        honeyUpgradeLevel: Int = 0,
        honeyCombUpgradeLevel: Int = 0,
        combCollectedTimes: Int = 0,
        honeyCollectedTimes: Int = 0,
        lastCollectionTime: Long = 0,
        createdTime: Long = 0
    ) : this(id, location, owner, createdTime) {
        this.modifiedTime = if (modifiedTime == 0L) createdTime else modifiedTime
        this.fullnessStatus = fullnessStatus
        this.bees = bees
        this.honeyUpgradeLevel = honeyUpgradeLevel
        this.honeyCombUpgradeLevel = honeyCombUpgradeLevel
        this.combCollectedTimes = combCollectedTimes
        this.honeyCollectedTimes = honeyCollectedTimes
        this.lastCollectionTime = lastCollectionTime
    }

    var fullnessStatus: Int by Delegates.observable(0) { _, _, _ -> updated = true }
    var bees: Int by Delegates.observable(0) { _, _, _ -> updated = true }
    var honeyUpgradeLevel: Int by Delegates.observable(0) { _, _, _ -> updated = true }
    var honeyCombUpgradeLevel: Int by Delegates.observable(0) { _, _, _ -> updated = true }
    var combCollectedTimes: Int by Delegates.observable(0) { _, _, _ -> updated = true }
    var honeyCollectedTimes: Int by Delegates.observable(0) { _, _, _ -> updated = true }
    var lastCollectionTime: Long by Delegates.observable(0L) { _, _, _ -> updated = true }
    var modifiedTime: Long by Delegates.observable(createdTime) { _, _, _ -> updated = true }


    override fun getPosition(): Position {
        return location.position
    }



}