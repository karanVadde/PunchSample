package com.meerkat.punchtest

import android.app.Application
import com.example.todolistinkotlin.database.ToDoListDatabase
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class PunchApp : Application() {
    override fun onCreate() {
        super.onCreate()
        //create our db.
        AppContainer.toDoListDatabase = ToDoListDatabase.getInstance(applicationContext)
        AppContainer.firebaseAnalytics = Firebase.analytics
    }
}