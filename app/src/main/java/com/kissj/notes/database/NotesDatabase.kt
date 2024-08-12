package com.kissj.notes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kissj.notes.dao.NoteDao
import com.kissj.notes.entities.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        private var instance: NotesDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): NotesDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(
                    ctx.applicationContext, NotesDatabase::class.java,
                    "notes_db"
                )
                    .fallbackToDestructiveMigration()
                    //.addCallback(roomCallback)
                    .build()

            return instance!!

        }

        /*private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                populateDatabase(instance!!)
            }
        } */
    }
}