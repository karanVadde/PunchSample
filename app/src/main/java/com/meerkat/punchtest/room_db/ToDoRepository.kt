package com.meerkat.punchtest.room_db

import com.example.todolistinkotlin.database.ToDoListDAO
import com.example.todolistinkotlin.database.ToDoListDataEntity
import com.meerkat.punchtest.AppContainer

class ToDoRepository() {

    private var mToDoListDAO: ToDoListDAO? = null
    //get a list of all data.
    var mAllToDos : List<ToDoListDataEntity> ?= null

    init {
        mToDoListDAO = AppContainer.toDoListDatabase?.toDoListDao()
        mAllToDos = mToDoListDAO?.getAll()
    }

    fun update(title: String, date : String, time : String, id: Long){
        mToDoListDAO?.update(title = title, date = date, time = time, id = id)
    }

    fun insert(title: String, date : String, time : String): Long? {
        return mToDoListDAO?.insert(ToDoListDataEntity(title = title, date = date, time = time, isShow = 0))
    }

    fun delete(id:Long){
        mToDoListDAO?.Delete(id)
    }

    fun isShownUpdate(id:Long , isShow : Int){
        mToDoListDAO?.isShownUpdate(id, isShow)
    }
}