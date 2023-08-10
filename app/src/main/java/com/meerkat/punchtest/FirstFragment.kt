package com.meerkat.punchtest

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.meerkat.punchtest.databinding.FragmentFirstBinding
import com.meerkat.punchtest.model.AlarmData
import com.meerkat.punchtest.model.ToDoListData
import com.meerkat.punchtest.notification.AlarmReceiver
import com.meerkat.punchtest.sqdb.DbInstanceHolder
import com.meerkat.punchtest.viewmodel.ToDoListViewModel
import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), OnItemClick {

    companion object{
        const val TAG = "FirstFragment"
    }

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    //view model for use with our fragment.
    private lateinit var  viewModel: ToDoListViewModel

    val list = mutableListOf<ToDoListData>()
    //
    val c = Calendar.getInstance()
    val month: Int = c.get(Calendar.MONTH)
    val year: Int = c.get(Calendar.YEAR)
    val day: Int = c.get(Calendar.DAY_OF_MONTH)
    //use this field to calculate time field.
    private var cal = Calendar.getInstance()
    private val listAdapter = ListAdapter(list, this)

    //
    var date : String = ""
    var time : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val clickListener = View.OnClickListener {
        when(it.id){
            R.id.etdate -> showDatePicker()
            R.id.etTime -> showTimePicker()
            R.id.bAddList -> addList()
        }
    }

    private fun addList(){
        //validate data.
        if(viewModel.validateData(viewModel.title.get().toString(), date, time)){
            //proceed to add data to the table.
            viewModel.addData(viewModel.title.get().toString(), date, time)
        }else{
            Toast.makeText(requireContext(), "Enter all fields!!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpUI(){
        binding.etdate.setOnClickListener(clickListener)
        binding.etTime.setOnClickListener(clickListener)
        binding.bAddList.setOnClickListener(clickListener)
        //
        binding.rvTodoList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTodoList.adapter = listAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initialize view model.
        viewModel = ViewModelProvider(this, ToDoListViewModel.viewModelFactory)[ToDoListViewModel::class.java]
        viewModel.alarmListener.observe(viewLifecycleOwner) {
            it?.let { setAlarm(it) }
        }
        //set view model.
        binding.viewModel = viewModel
        //
        setUpUI()
        //
        viewModel.getPreviousList()
        //
        viewModel.toDoList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            //list.addAll(it)
            if (it == null) return@Observer
            //
            list.clear()
            val tempList = mutableListOf<ToDoListData>()
            it.forEach {
                tempList.add(
                    ToDoListData(
                        title = it.title,
                        date = it.date,
                        time = it.time,
                        indexDb = it.id,
                        isShow = it.isShow
                    )
                )
            }
            //
            list.addAll(tempList)
            listAdapter.notifyDataSetChanged()
            viewModel.position = -1
            viewModel.toDoList.value = null
        })
        //
        viewModel.toDoListData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (viewModel.position != -1) {
                list.set(viewModel.position, it)
                listAdapter.notifyItemChanged(viewModel.position)
            } else {
                list.add(it)
                listAdapter.notifyDataSetChanged()
            }
            viewModel.position = -1;
        })
        //
        Log.d(TAG, DbInstanceHolder.DBHelper?.isDBSetup().toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setAlarm(alarmData: AlarmData){
        val alarmManager: AlarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("INTENT_NOTIFY", true)
        intent.putExtra("isShow", alarmData.i)
        intent.putExtra("id", alarmData.id)
        intent.putExtra("title", alarmData.title)
        intent.putExtra("date","Time-> ${alarmData.hour}:${alarmData.minute}")
        //
        var pendingIntent : PendingIntent ?= null
        pendingIntent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_MUTABLE);
        } else {
            PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        //
        if (alarmData.i == 0) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                alarmData.calender.timeInMillis , pendingIntent)
        } else {
            alarmManager.cancel(pendingIntent)
        }
    }

    /**
     * use this function to show a date picker dialog and write it into specific et field.
     */
    @SuppressLint("SuspiciousIndentation")
    private fun showDatePicker(){
        val dpd = DatePickerDialog(requireContext(),
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                date = "" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year
                // Display Selected date in text-box
                binding.etdate.setText(date)
                //view model data to set alarm.
                viewModel.month = monthOfYear
                viewModel.year = year
                viewModel.day = dayOfMonth
        }, year, month, day)
        dpd.datePicker.minDate = System.currentTimeMillis() - 1000
        dpd.show()
    }
    /**
     * use this function to show a time picker dialog and write it into specific et field.
     */
    private fun showTimePicker(){
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            this.cal.set(Calendar.HOUR_OF_DAY, hour)
            this.cal.set(Calendar.MINUTE, minute)
            //view model data to set alarm.
            viewModel.hour = hour
            viewModel.minute = minute
            time = SimpleDateFormat("HH:mm").format(cal.time)
            binding.etTime.setText(time)
        }

        this.cal = cal
        TimePickerDialog(
            requireContext(),
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    override fun onItemClick(v: View, position: Int) {
        Toast.makeText(requireContext(), "position: $position", Toast.LENGTH_SHORT).show()

        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.apply {
            setMessage(list[position].title)
            setPositiveButton("Edit", DialogInterface.OnClickListener { dialogInterface, i ->
                viewModel.title.set(list[position].title)
                viewModel.date.set(list[position].date)
                viewModel.time.set(list[position].time)
                viewModel.position = position
                viewModel.index = list[position].indexDb
                binding.editText.isFocusable = true
            })
            setNegativeButton("Delete", DialogInterface.OnClickListener { dialogInterface, i ->
                viewModel.delete(list[position].indexDb)
            })
        }.create().show()
    }
}