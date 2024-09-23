package com.thaianramalho.cadastroferiados

import androidx.room.*

@Dao
interface FeriadoDao {
    @Insert
    suspend fun insert(feriado: Feriado)

    @Update
    suspend fun update(feriado: Feriado)

    @Delete
    suspend fun delete(feriado: Feriado)

    @Query("SELECT * FROM feriado ORDER BY data")
    suspend fun getAll(): List<Feriado>
}

@Database(entities = [Feriado::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun feriadoDao(): FeriadoDao
}
