package com.example.todolist.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.example.todolist.ListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.DataTruncation
import kotlin.collections.ArrayList

class MyDbManager(context: Context) {
    val myDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null

    fun openDb(){
        db = myDbHelper.writableDatabase
    }
    suspend fun insertToDb(title: String, content: String, url: String, currentTime: String) = withContext(Dispatchers.IO){
        val values = ContentValues().apply {
            put(MyDbName.COLUMN_NAME_TITLE, title)
            put(MyDbName.COLUMN_NAME_CONTENT, content)
            put(MyDbName.COLUMN_NAME_IMAGE_URL, url)
            put(MyDbName.COLUMN_NAME_TIME, currentTime)
        }
        db?.insert(MyDbName.TABLE_NAME, null, values)
    }

    fun removeItemFromDb( id: String){
        val selection = BaseColumns._ID + "=$id"
        db?.delete(MyDbName.TABLE_NAME, selection, null)
    }

    suspend fun readDbData(searchText: String) : ArrayList<ListItem> = withContext(Dispatchers.IO){
        val dataList = ArrayList<ListItem>()
        val selection = "${MyDbName.COLUMN_NAME_TITLE} like ?"
        val cursor = db?.query(MyDbName.TABLE_NAME, null, selection, arrayOf("%$searchText%"),
            null, null, null)

            while (cursor?.moveToNext()!!){
                val dataText = cursor.getString(cursor.getColumnIndexOrThrow(MyDbName.COLUMN_NAME_TITLE))
                val dataContent = cursor.getString(cursor.getColumnIndexOrThrow(MyDbName.COLUMN_NAME_CONTENT))
                val dataUri = cursor.getString(cursor.getColumnIndexOrThrow(MyDbName.COLUMN_NAME_IMAGE_URL))
                val dataId = cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID))
                val time = cursor.getString(cursor.getColumnIndexOrThrow(MyDbName.COLUMN_NAME_TIME))

                val item = ListItem()
                item.title = dataText
                item.desc = dataContent
                item.uri = dataUri
                item.id = dataId
                item.time = time
                dataList.add(item)
            }
        cursor.close()
        return@withContext dataList
    }

    suspend fun updateItem( title: String, content: String, url: String, id: Int, time: String) = withContext(Dispatchers.IO){
        val values = ContentValues().apply {
            put(MyDbName.COLUMN_NAME_TITLE, title)
            put(MyDbName.COLUMN_NAME_CONTENT, content)
            put(MyDbName.COLUMN_NAME_IMAGE_URL, url)
            put(MyDbName.COLUMN_NAME_TIME, time)
        }
        val selection = BaseColumns._ID + "=$id"

        db?.update(MyDbName.TABLE_NAME, values, selection, null)
    }
    fun closeDb(){
        myDbHelper.close()
    }
}