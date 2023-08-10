package com.meerkat.punchtest.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todolistinkotlin.database.ToDoListDataEntity
import com.meerkat.punchtest.analytics.AnalyticsEngine
import com.meerkat.punchtest.model.AlarmData
import com.meerkat.punchtest.model.ToDoListData
import com.meerkat.punchtest.room_db.ToDoRepository
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class ToDoListViewModel() : ViewModel() {

    var toDoListData : MutableLiveData<ToDoListData> = MutableLiveData<ToDoListData>()
    var getAllData = mutableListOf(ToDoListDataEntity())
    val toDoList = MutableLiveData<List<ToDoListDataEntity>>()

    private var toDoRepository : ToDoRepository ?= null

    companion object {
        const val TAG = "ToDoListViewModel"
        val viewModelFactory : ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ToDoListViewModel() as T
            }
        }
    }

    init {
        //initialize the repository for use.
        toDoRepository = ToDoRepository()
        getAllData = toDoRepository?.mAllToDos as MutableList<ToDoListDataEntity>
        Log.d(TAG, "getAllData: $getAllData")
    }

    var title = ObservableField<String>("")
    var date = ObservableField<String>("")
    var time = ObservableField<String>("")

    var month = 0
    var day = 0
    var year = 0

    var hour = 0
    var minute = 0

    var position: Int = -1
    var index: Long = -1

    fun validateData(title: String, date : String, time : String):Boolean{
        if (title.isNotBlank() && date.isNotBlank() && time.isNotBlank())return true
        return false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @WorkerThread
    fun addData(title: String, date : String, time : String){
        if(position != -1){
            toDoRepository?.update(title = title, date = date, time = time, id = index)
            //Logging analytics event - update event
            AnalyticsEngine.logEvent(AnalyticsEngine.EDIT_EVT,
                index.toString(),
                evtDate = date,
                evtTime = "$date $time")
        }else{
            val newId = toDoRepository?.insert(title = title, date = date, time = time)
            //calendar operation to set alarm.
            val cal = calendarOperation()
            Log.d("Alarm Title","$month , $date : ${cal.time}")
            newId?.let {
                //set an alarm.
                setAlarm(cal, 0, it, title,hour,minute)
            }
            //Logging analytics event - add event
            AnalyticsEngine.logEvent(AnalyticsEngine.ADD_EVT,
                newId.toString(),
                evtDate = date,
                evtTime = cal.time.toString())
        }

        //
        toDoRepository?.mAllToDos.let {
            getAllData = it as MutableList<ToDoListDataEntity>
            getPreviousList()
        }
    }

    fun getPreviousList() {
        toDoList.value = getAllData
    }

    fun delete(id: Long) {
        //delete from repository.
        toDoRepository?.delete(id)
        //
        toDoRepository?.mAllToDos.let {
            getAllData = it as MutableList<ToDoListDataEntity>
            getPreviousList()
        }
        //
        val cal = calendarOperation()
        //Logging analytics event - add event
        AnalyticsEngine.logEvent(AnalyticsEngine.DEL_EVT,
            id.toString(),
            evtDate = "",
            evtTime = cal.time.toString())
    }

    private fun calendarOperation(): Calendar {

        val cal  = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault())

        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.DAY_OF_MONTH, day)

        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        return cal
    }

    var alarmListener : MutableLiveData<AlarmData> = MutableLiveData()
    private fun setAlarm(calender: Calendar, i: Int, id: Long, title: String, hour:Int,minute:Int) {
        //delegate this work to fragment since use of context in a view model is not recommended practice
        alarmListener.postValue(AlarmData(calender, i, id, title, hour, minute))
    }
}