package eu.virtusdevelops.magicbees.core.storage

import eu.virtusdevelops.magicbees.api.models.ChunkLocation
import eu.virtusdevelops.magicbees.api.models.Position
import eu.virtusdevelops.magicbees.api.models.StoredItem

class ChunkData<T : StoredItem>(
    val chunkLocation: ChunkLocation,
    val items: HashMap<Position, T> = HashMap()
) {

    val removedItems : HashMap<Position, T> = HashMap()
    val addedItems : HashMap<Position, T> = HashMap()


    fun addItem(item: T){
        if(removedItems[item.getPosition()] != null){
            removedItems.remove(item.getPosition())
        }
        addedItems[item.getPosition()] = item
        items[item.getPosition()] = item
    }

    fun removeItem(item: T): Boolean{
        var removed = false
        if(items[item.getPosition()] != null){
            if(items[item.getPosition()] == item){
                // remove
                removedItems[item.getPosition()] = item
                items.remove(item.getPosition())
                removed = true
            }
        }
        // check added items
        if(addedItems[item.getPosition()] != null){
            if(addedItems[item.getPosition()] == item){
                addedItems.remove(item.getPosition())
                removed = true
            }
        }


        return removed
    }

    fun getItem(chunkPosition: Position): T?{
        return items[chunkPosition]
    }
}