package com.rkbapps.canvas.db.migrator

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

/**
 * DB version 2 → 3
 *
 * Adds a `pageCount` column to the `designs` table.
 *
 * This allows blank trailing pages to survive save/reload cycles.
 * Previously, the number of pages was inferred from the maximum `pageIndex`
 * in the `paths` table — meaning a page with no strokes was invisible to
 * the mapper and silently dropped when reopening a drawing.
 *
 * All existing rows default to 1 (single-page drawings), which is correct
 * because multi-page support was introduced at the same time as this migration.
 */
object Migration2To3 : Migration(2, 3) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "ALTER TABLE designs ADD COLUMN pageCount INTEGER NOT NULL DEFAULT 1"
        )
    }
}
