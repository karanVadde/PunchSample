package com.meerkat.punchtest

import com.example.todolistinkotlin.database.ToDoListDatabase
import com.google.firebase.analytics.FirebaseAnalytics


class AppContainer {
    companion object{
        var toDoListDatabase : ToDoListDatabase? = null
        var firebaseAnalytics: FirebaseAnalytics?= null
    }
}