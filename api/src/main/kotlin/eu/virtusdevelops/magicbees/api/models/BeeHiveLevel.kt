package eu.virtusdevelops.magicbees.api.models


data class BeeHiveLevel (
    val level : Int,
    val harvestRequirements : List<String>,
    val upgradeRequirements : List<String>,
    val harvestRewards : List<String>
)