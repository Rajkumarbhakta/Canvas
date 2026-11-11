package com.rkbapps.canvas.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.rkbapps.canvas.db.dao.DrawingDao
import com.rkbapps.canvas.db.entities.DesignEntity
import com.rkbapps.canvas.db.entities.PathEntity
import com.rkbapps.canvas.db.migrator.Migration1To2
import com.rkbapps.canvas.db.migrator.Migration2To3
import com.rkbapps.canvas.db.utils.RoomConverters

@Database(
    entities = [DesignEntity::class, PathEntity::class],
    version = 3,        // bumped 2 → 3 (added pageCount column)
    exportSchema = true
)
@TypeConverters(RoomConverters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun drawingDao(): DrawingDao
}

// The compiler generates the implementation of this class
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
