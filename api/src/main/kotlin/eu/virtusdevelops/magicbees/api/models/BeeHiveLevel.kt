package eu.virtusdevelops.magicbees.api.models


// contains harvest requirements
// contains comb requirements

// contains upgrade requirements


// harvest rewards
// comb rewards

// requirements should be constructed as they're called probably
// (cause other plugins can add their own custom providers later on thru API)
// or maybe make requirement be able to call API to get provider?
data class BeeHiveLevel (
    val level : Int,

    val harvestRequirements : List<String>,
    val combRequirements : List<String>,
    val honeyUpgradeRequirements : List<String>,
    val combUpgradeRequirements : List<String>,

    val harvestRewards : List<String>,
    val combRewards : List<String>
)