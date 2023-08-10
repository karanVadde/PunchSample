package com.meerkat.punchtest.sqdb

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBHandler(context : Context) : SQLiteOpenHelper(
    context,
    DbConstants.DATABASE_NAME,
    null,
    DbConstants.DATABASE_VERSION) {

    companion object{
        const val TAG = "DBHandler"
    }

    val tableName = "todolist"

    override fun onCreate(sqLiteDatabase: SQLiteDatabase?) {
        //create the
        val CREATE_TO_DO_TABLE = ("CREATE TABLE IF NOT EXISTS todolist(id INTEGER PRIMARY KEY," +
                "title TEXT," +
                "date TEXT," +
                "time TEXT,"
                + "isShow INTEGER)")

        sqLiteDatabase?.execSQL(CREATE_TO_DO_TABLE)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    fun isDBSetup(): Boolean {
        var isSetup = false
        val db = DbInstanceHolder.DBHelper?.writableDatabase
        val sql = "SELECT name FROM sqlite_master WHERE type='table' AND name=$tableName"
        try {
            val cursor = db?.rawQuery(sql, null)
            // if table exists
            if (cursor != null && cursor.moveToFirst()) {
                isSetup = true
            }
        } catch (e: Exception) {
            Log.d(TAG, e.message!!)
        }
        return isSetup
    }

    fun addItem(){
        val db: SQLiteDatabase? = DbInstanceHolder.DBHelper?.writableDatabase
        val values = ContentValues()
        /*values.put(com.chariotsolutions.nfc.plugin.DatabaseHandler.EVENT_TIMESTAMP, event.getTimestamp())
        values.put(com.chariotsolutions.nfc.plugin.DatabaseHandler.EVENT_SYNC_STATUS, event.getSyncStatus())
        values.put(com.chariotsolutions.nfc.plugin.DatabaseHandler.EVENT_CODE, event.getEventCode())
        values.put(com.chariotsolutions.nfc.plugin.DatabaseHandler.EVENT_DATA, event.getEventData())
        values.put(com.chariotsolutions.nfc.plugin.DatabaseHandler.EVENT_SEQUENCE_NO, event.getSequenceNumber())*/
        try {
            // Inserting Row
            db?.insert(tableName, null, values)
        } catch (e: java.lang.Exception) {
            Log.d(TAG, e.message!!)
        }
    }
}