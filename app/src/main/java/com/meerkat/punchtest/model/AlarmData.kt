package com.meerkat.punchtest.model

import java.util.Calendar

data class AlarmData(val calender: Calendar,
                     val i: Int,
                     val id: Long,
                     val title: String,
                     val hour:Int,
                     val minute:Int)
