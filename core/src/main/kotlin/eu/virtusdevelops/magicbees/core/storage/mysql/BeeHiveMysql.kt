package eu.virtusdevelops.magicbees.core.storage.mysql

import com.zaxxer.hikari.HikariDataSource
import eu.virtusdevelops.magicbees.api.models.BeeHive
import eu.virtusdevelops.magicbees.api.models.ChunkLocation
import eu.virtusdevelops.magicbees.api.models.HiveLocation
import eu.virtusdevelops.magicbees.api.models.Position
import eu.virtusdevelops.magicbees.core.storage.BeeHiveDao
import java.sql.SQLException
import java.util.*
import java.util.logging.Logger

class BeeHiveMysql(
    private val dataSource: HikariDataSource,
    private val logger: Logger,
) : BeeHiveDao {


    override fun init() {
        try{
            dataSource.connection.use {
                it.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS beehives(
                        id UUID PRIMARY KEY,

                        chunk_x INTEGER NOT NULL DEFAULT 0,
                        chunk_z INTEGER NOT NULL DEFAULT 0,
                        world VARCHAR(255) NOT NULL,
                        position_x INTEGER NOT NULL DEFAULT 0,
                        position_y INTEGER NOT NULL DEFAULT 0,
                        position_z INTEGER NOT NULL DEFAULT 0,
                        
                        owner UUID NOT NUll,
                        fullness_status INTEGER NOT NULL DEFAULT 0,
                        bees INTEGER NOT NULL DEFAULT 0,
                        honey_upgrade_level INTEGER NOT NULL DEFAULT 0,
                        honey_comb_upgrade_level INTEGER NOT NULL DEFAULT 0,
                        
                        comb_collected_times INTEGER NOT NULL DEFAULT 0,
                        honey_collected_times INTEGER NOT NULL DEFAULT 0,
                        last_collected_time BIGINT NOT NULL DEFAULT 0,
                        
                        created_at BIGINT NOT NULL DEFAULT 0,
                        updated_at BIGINT NOT NULL DEFAULT 0
                    )
                """.trimIndent()).execute()
            }
        }catch (e: SQLException){
            logger.severe("An error occurred while initializing the BeeHiveMysql")
            e.printStackTrace()
        }
    }

    override fun getById(id: UUID): BeeHive? {
        try{
            dataSource.connection.use {
                val statement = it.prepareStatement("SELECT * FROM beehives WHERE id = ?")
                statement.setObject(1, id)
                val resultSet = statement.executeQuery()
                return if(resultSet.next()){
                    mapToBeeHive(resultSet)
                }else{
                    null
                }
            }
        }catch (e: SQLException){
            logger.severe("An error occurred while getting the BeeHive by id [$id]")
            e.printStackTrace()
            return null
        }
    }

    override fun getAll(): List<BeeHive> {
        TODO("Not yet implemented")
    }

    override fun save(t: BeeHive): Boolean {
        try {
            dataSource.connection.use { connection ->

                // Check if the BeeHive already exists in the database
                val existsStatement = connection.prepareStatement("""
                SELECT COUNT(*) FROM beehives WHERE id = ?
            """.trimIndent())
                existsStatement.setObject(1, t.id)
                val resultSet = existsStatement.executeQuery()

                val exists = resultSet.next() && resultSet.getInt(1) > 0
                existsStatement.close()

                if (exists) {
                    // Update existing BeeHive
                    val updateStatement = connection.prepareStatement("""
                    UPDATE beehives
                    SET 
                        fullness_status = ?, bees = ?, 
                        honey_upgrade_level = ?, honey_comb_upgrade_level = ?, 
                        comb_collected_times = ?, honey_collected_times = ?, 
                        last_collected_time = ?, updated_at = ?
                    WHERE id = ?
                """.trimIndent())
                    updateStatement.setInt(1, t.fullnessStatus)
                    updateStatement.setInt(2, t.bees)
                    updateStatement.setInt(3, t.honeyUpgradeLevel)
                    updateStatement.setInt(4, t.honeyCombUpgradeLevel)
                    updateStatement.setInt(5, t.combCollectedTimes)
                    updateStatement.setInt(6, t.honeyCollectedTimes)
                    updateStatement.setLong(7, t.lastCollectionTime)
                    updateStatement.setLong(8, t.modifiedTime)
                    updateStatement.setObject(9, t.id)

                    val rowsUpdated = updateStatement.executeUpdate()
                    updateStatement.close()
                    return rowsUpdated > 0
                } else {
                    // Insert new BeeHive
                    val insertStatement = connection.prepareStatement("""
                    INSERT INTO beehives (
                        id, chunk_x, chunk_z, world, 
                        position_x, position_y, position_z, 
                        owner, fullness_status, bees, 
                        honey_upgrade_level, honey_comb_upgrade_level, 
                        comb_collected_times, honey_collected_times, 
                        last_collected_time, created_at, updated_at
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent())

                    insertStatement.setObject(1, t.id)
                    insertStatement.setInt(2, t.location.chunkLocation.x)
                    insertStatement.setInt(3, t.location.chunkLocation.z)
                    insertStatement.setString(4, t.location.worldName)
                    insertStatement.setInt(5, t.location.position.x)
                    insertStatement.setInt(6, t.location.position.y)
                    insertStatement.setInt(7, t.location.position.z)
                    insertStatement.setObject(8, t.owner)
                    insertStatement.setInt(9, t.fullnessStatus)
                    insertStatement.setInt(10, t.bees)
                    insertStatement.setInt(11, t.honeyUpgradeLevel)
                    insertStatement.setInt(12, t.honeyCombUpgradeLevel)
                    insertStatement.setInt(13, t.combCollectedTimes)
                    insertStatement.setInt(14, t.honeyCollectedTimes)
                    insertStatement.setLong(15, t.lastCollectionTime)
                    insertStatement.setLong(16, t.createdTime)
                    insertStatement.setLong(17, t.modifiedTime)

                    val rowsInserted = insertStatement.executeUpdate()
                    insertStatement.close()
                    return rowsInserted > 0
                }
            }
        } catch (e: SQLException) {
            logger.severe("An error occurred while saving the BeeHive [${t.id}]")
            e.printStackTrace()
            return false
        }
    }


    override fun delete(t: BeeHive): Boolean {
        try {
            dataSource.connection.use {
                return it.prepareStatement("DELETE FROM beehives WHERE id = ?").apply {
                    setObject(1, t.id)
                }.executeUpdate() != 0
            }
        }catch (e: SQLException){
            logger.severe("An error occurred while deleting the BeeHive")
            e.printStackTrace()
            return false
        }
    }

    override fun getChunkData(chunkX: Int, chunkZ: Int, world: String): List<BeeHive> {
        val beehives = mutableListOf<BeeHive>()
        try {
            dataSource.connection.use {
                val statement = it.prepareStatement("""
                    SELECT *
                    FROM beehives
                    WHERE chunk_x = ?
                    AND chunk_z = ?
                    AND world = ?
                """.trimIndent())

                statement.setInt(1, chunkX)
                statement.setInt(2, chunkZ)
                statement.setString(3, world)

                val resultSet = statement.executeQuery()
                // mapper function call?
                while(resultSet.next()){
                    beehives.add(mapToBeeHive(resultSet))
                }
                statement.close()
            }
        }catch (e: SQLException){
            logger.severe("An error occurred while getting chunk data [$chunkX, $chunkZ, $world]")
            e.printStackTrace()
        }
        return beehives
    }





    // Mapper function for mapping a ResultSet row into a BeeHive object
    private fun mapToBeeHive(resultSet: java.sql.ResultSet): BeeHive {
        return BeeHive(
            id = UUID.fromString(resultSet.getString("id")),
            location = HiveLocation(
                worldName = resultSet.getString("world"),
                chunkLocation = ChunkLocation(
                    x = resultSet.getInt("chunk_x"),
                    z = resultSet.getInt("chunk_z")
                ),
                position = Position(
                    x = resultSet.getInt("position_x"),
                    y = resultSet.getInt("position_y"),
                    z = resultSet.getInt("position_z")
                )
            ),
            owner = UUID.fromString(resultSet.getString("owner")),
            createdTime = resultSet.getLong("created_at"),
            fullnessStatus = resultSet.getInt("fullness_status"),
            bees = resultSet.getInt("bees"),
            honeyUpgradeLevel = resultSet.getInt("honey_upgrade_level"),
            honeyCombUpgradeLevel = resultSet.getInt("honey_comb_upgrade_level"),
            combCollectedTimes = resultSet.getInt("comb_collected_times"),
            honeyCollectedTimes = resultSet.getInt("honey_collected_times"),
            lastCollectionTime = resultSet.getLong("last_collected_time"),
            modifiedTime = resultSet.getLong("updated_at")
        )
    }
}