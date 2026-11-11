package com.rkbapps.canvas.db.migrator

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

/**
 * DB version 1 → 2
 *
 * Adds page geometry columns to the `designs` table and a `pageIndex` column
 * to the `paths` table.
 *
 * Room 2.8 / SQLite 2.6 KMP API:
 *  - `migrate(connection: SQLiteConnection)` is a regular (non-suspend) open function.
 *  - `androidx.sqlite.execSQL` is a top-level extension on `SQLiteConnection`
 *    that wraps `prepare(sql).use { step() }` internally.
 *
 * Crucially, `isLegacy` defaults to **1** (true) for every pre-existing row so all drawings
 * created before this migration continue to use the legacy full-screen render path.
 */
object Migration1To2 : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        // ── designs table: page geometry + legacy flag ─────────────────────
        connection.execSQL("ALTER TABLE designs ADD COLUMN pageWidth REAL NOT NULL DEFAULT 794.0")
        connection.execSQL("ALTER TABLE designs ADD COLUMN pageHeight REAL NOT NULL DEFAULT 1123.0")
        connection.execSQL("ALTER TABLE designs ADD COLUMN pageSizeLabel TEXT NOT NULL DEFAULT 'A4 Portrait'")
        // All existing rows get isLegacy = 1 (true) so they render as before migration.
        connection.execSQL("ALTER TABLE designs ADD COLUMN isLegacy INTEGER NOT NULL DEFAULT 1")

        // ── paths table: per-page index ────────────────────────────────────
        // Default 0 so all existing strokes belong to page 0 of their drawing.
        connection.execSQL("ALTER TABLE paths ADD COLUMN pageIndex INTEGER NOT NULL DEFAULT 0")
    }
}
