package com.meerkat.punchtest.sqdb

import android.content.Context
import android.util.Log

class DbInstanceHolder {

    companion object{
        private const val TAG = "DatabaseInstanceHolder"
        var DBHelper: DBHandler? = null
        fun createDBInstance(pContext: Context?) {
            if (DBHelper == null) {
                DBHelper = DBHandler(pContext!!) // This will be your DB Handler Class
            }
            Log.d(TAG, "DBHelper: \$DBHelper")
        }
    }
}