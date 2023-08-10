package com.meerkat.punchtest.analytics

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.meerkat.punchtest.AppContainer.Companion.firebaseAnalytics

class AnalyticsEngine {
    companion object{
        const val TAG = "AnalyticsEngine"
        const val EVT_TYP = "EVENT_TYPE"
        const val EVT_ID = "EVENT_ID"
        const val EVT_DTE = "EVENT_DATE"
        const val EVT_TME = "EVENT_TIME"
        //event types.
        const val ADD_EVT = "ADD_EVENT"
        const val EDIT_EVT = "EDIT_EVENT"
        //manual delete via dialog.
        const val DEL_EVT = "DEL_EVENT"
        //system delete via alarm.
        const val ALT_SHW_EVT = "ALERT_SHOW_EVENT"
        //
        fun logEvent(eventType: String, eventID : String, evtDate: String, evtTime:String){
            val bundle = Bundle()
            bundle.putString(EVT_TYP, eventType)
            bundle.putString(EVT_ID, eventID)
            bundle.putString(EVT_DTE, evtDate)
            bundle.putString(EVT_TME, evtTime)
            //log event.x
            Log.d(TAG, "Logging Analytics eventType : $eventType $eventID $evtDate $evtTime")
            firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
        }
    }
}