package eu.virtusdevelops.magicbees.core.storage

interface CrudDao<T, ID> {
    fun init()

    fun getById(id: ID): T?

    fun getAll(): List<T>

    fun save(t: T): Boolean

    fun update(t: T): Boolean

    fun delete(t: T): Boolean
}