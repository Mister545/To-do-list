package com.example.todolist.db

import android.provider.BaseColumns

object MyDbName {
    const val TABLE_NAME = "my_table"
    const val COLUMN_NAME_TITLE = "title"
    const val COLUMN_NAME_CONTENT = "content"
    const val COLUMN_NAME_IMAGE_URL= "uri"
    const val COLUMN_NAME_TIME= "time"

    const val DATABASE_VERSION = 1
    const val DATABASE_NAME = "MyToDoDb.db"

    const val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
    "$COLUMN_NAME_TITLE TEXT," +
    "$COLUMN_NAME_CONTENT TEXT," +
    "$COLUMN_NAME_IMAGE_URL TEXT," +
    "$COLUMN_NAME_TIME TEXT)"

    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS ${TABLE_NAME}"

}