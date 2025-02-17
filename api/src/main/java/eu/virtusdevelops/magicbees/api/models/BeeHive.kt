package eu.virtusdevelops.magicbees.api.models

import java.util.UUID

/**
 * Represents a beehive containing information about its location, ownership, and activity status.
 * This data class is used to manage and persist details of a beehive, including tracking collection
 * statistics and timestamps.
 *
 * @property id Unique identifier for the beehive.
 * @property location The geographical location of the beehive.
 * @property owner UUID of the player or entity that owns the beehive.
 * @property level Represents the level or tier of the beehive.
 * @property combCollectedTimes Tracks how many times combs have been collected from this beehive.
 * @property lastCollectionTime The timestamp of the last collection activity.
 * @property createdTime The timestamp of when the beehive was created.
 * @property modifiedTime The last modified timestamp related to any changes made to the beehive.
 */
data class BeeHive(
    val id: UUID,
    val location: Location,
    val owner: UUID,
    val honeyLevel: Int,
    val honeyCombLevel: Int,

    val combCollectedTimes: Int,
    val lastCollectionTime: Long,

    val createdTime: Long,

    val modifiedTime: Long
) {
}