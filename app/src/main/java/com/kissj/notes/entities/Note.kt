package com.kissj.notes.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "notes")
data class Note(

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "date_time")
    var dateTime: String = "",

    @ColumnInfo(name = "subtitle")
    var subTitle: String = "",

    @ColumnInfo(name = "note_text")
    var noteText: String = "",

    @ColumnInfo(name = "image_Path")
    var imagePath: String = "",

    @ColumnInfo(name = "color")
    var color: String = "",

    @ColumnInfo(name = "web_link")
    var webLink: String = ""
) : Serializable