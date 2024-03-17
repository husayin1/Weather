package com.example.weather.ui.alerts



import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.weather.MyApplication
import com.example.weather.R
import com.example.weather.databinding.FragmentAlertsBinding
import com.example.weather.databinding.SetAlarmDialogBinding
import com.example.weather.model.response.AlertKind
import com.example.weather.model.response.AlertPojo
import com.example.weather.model.response.LocationLatLngPojo
import com.example.weather.ui.alerts.videwmodel.AlertsViewModel
import com.example.weather.ui.alerts.videwmodel.AlertsViewModelFactory
import com.example.weather.utilites.NetworkManager
import com.example.weather.utilites.setDate
import com.example.weather.utilites.setTime
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit


private const val CHANNEL_ID = "my_channel_id"
private const val NOTIFICATION_ID = 1

class AlertFragment : Fragment() {

    private lateinit var _binding: FragmentAlertsBinding
    private lateinit var notificationManager:NotificationManager
    private lateinit var alertViewModel: AlertsViewModel
    private lateinit var alertViewModelFactory: AlertsViewModelFactory
    private lateinit var customAlertDialogBinding: SetAlarmDialogBinding
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        notificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val isNotificationPermissionGranted = notificationManager.areNotificationsEnabled()
        if (!isNotificationPermissionGranted) {
            showNotificationPermissionDialog()
        }


        alertViewModelFactory = AlertsViewModelFactory(MyApplication.getRepository())
        alertViewModel = ViewModelProvider(this, alertViewModelFactory)[AlertsViewModel::class.java]
        customAlertDialogBinding = SetAlarmDialogBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireActivity())

        _binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener{

            if(NetworkManager.isInternetConnected()){

                val action = AlertFragmentDirections.actionAlertsFragmentToMapFragment(isFromAlerts = true)
                Navigation.findNavController(requireView()).navigate(action)
            }else{
                Snackbar.make(requireView(), getString(R.string.internet_connection), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.Dismiss)) {
                    }.show()
            }
        }

        val adapter = AlertsAdapter(AlertsAdapter.RemoveClickListener {
            delAlertSnack(it)
        })
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = layoutManager
        lifecycleScope.launch(Dispatchers.Main) {
            alertViewModel.getAllAlerts().observe(requireActivity()){
                adapter.submitList(it)

                if (it.isNotEmpty()) {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.groupNoAlarms.visibility = View.GONE
                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                    binding.groupNoAlarms.visibility = View.VISIBLE
                }
            }
            /*alertViewModel.alerts.collectLatest {
                when (it) {
                    is AlertStatus.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is AlertStatus.Success -> {
                        adapter.submitList(it.alertList)
                        if (it.alertList.isNotEmpty()) {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.groupNoAlarms.visibility = View.GONE
                        } else {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.visibility = View.GONE
                            binding.groupNoAlarms.visibility = View.VISIBLE
                        }
                    }

                    else -> {
                        binding.progressBar.visibility = View.GONE
                        binding.recyclerView.visibility = View.GONE
                        binding.groupNoAlarms.visibility = View.VISIBLE
                    }
                }
            }*/
        }
    }

    override fun onResume() {
        super.onResume()
        val args = AlertFragmentArgs.fromBundle(requireArguments())
        val latLng = args.latLng
        if (latLng != null)
        {
            launchCustomAlertDialog(latLng)
        }
    }

    private fun delAlertSnack(alertPojo: AlertPojo) {
        Snackbar.make(requireView(), getString(R.string.ask_del_fav), Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.del_item)) {
                lifecycleScope.launch(Dispatchers.IO) {
                    alertViewModel.deleteAlert(alertPojo)
                }
            }.show()
    }
    private fun launchCustomAlertDialog(locationLatLngPojo: LocationLatLngPojo) {

        val alertDialog = materialAlertDialogBuilder.setView(customAlertDialogBinding.root)
            .setBackground(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.white_hourly_background, requireActivity().theme
                )
            ).setCancelable(false).show()
        setTimeAndDateInDialog()


        var startTime = Calendar.getInstance().timeInMillis
        val endCal = Calendar.getInstance()
        endCal.add(Calendar.DAY_OF_MONTH, 0)
        endCal.add(Calendar.HOUR,1)
        var endTime = endCal.timeInMillis

        customAlertDialogBinding.buttonSave.setOnClickListener {
            val id = if (customAlertDialogBinding.radioAlarm.isChecked) {
                saveToDatabase(startTime, endTime, AlertKind.ALARM,locationLatLngPojo)
            } else {
                saveToDatabase(startTime, endTime, AlertKind.NOTIFICATION,locationLatLngPojo)
            }

            scheduleWork(startTime, endTime, id)
            checkDisplayOverOtherAppPerm()

            alertDialog.dismiss()
        }
        customAlertDialogBinding.cardViewChooseEnd.setOnClickListener {
            setAlarm(endTime) { currentTime ->
                endTime = currentTime
                customAlertDialogBinding.textViewEndDate.setDate(currentTime)
                customAlertDialogBinding.textViewEndTime.setTime(currentTime)
            }
        }
        customAlertDialogBinding.buttonCancel.setOnClickListener {
            alertDialog.dismiss()
        }
    }
    private fun saveToDatabase(startTime: Long, endTime: Long, alarmKind: String,locationLatLngPojo: LocationLatLngPojo): String {
        val alertPojo =
            AlertPojo(start = startTime, end = endTime, kind = alarmKind, lat = locationLatLngPojo.lat, lon = locationLatLngPojo.lng)
        alertViewModel.insertAlert(alertPojo)
        return alertPojo.id
    }

    private fun checkDisplayOverOtherAppPerm() {
        if (!Settings.canDrawOverlays(requireActivity())) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + requireActivity().packageName)
            )
            someActivityResultLauncher.launch(intent)
        }
    }
    private val someActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (!Settings.canDrawOverlays(requireContext())) { }
        }


    private fun scheduleWork(startTime: Long, endTime: Long, tag: String) {

        val _Day_TIME_IN_MILLISECOND = 24 * 60 * 60 * 1000L
        val timeNow = Calendar.getInstance().timeInMillis

        val inputData = Data.Builder()
        inputData.putString(ID, tag)


        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val myWorkRequest: WorkRequest = if ((endTime - startTime) < _Day_TIME_IN_MILLISECOND) {
            Log.d("TAG", "scheduleWork: one")
            OneTimeWorkRequestBuilder<Worker>().addTag(tag).setInitialDelay(
                startTime - timeNow, TimeUnit.MILLISECONDS
            ).setInputData(inputData.build()
            ).setConstraints(constraints).build()

        } else {
            Log.i("TAG", "scheduleWork:  else ")
            Log.i("TAG", "scheduleWork: ${endTime - timeNow}")
            Log.i("TAG", "scheduleWork: ${startTime - timeNow}")
            WorkManager.getInstance(requireContext()).enqueue(
                OneTimeWorkRequestBuilder<Worker>().addTag(tag).setInitialDelay(
                    endTime - timeNow, TimeUnit.MILLISECONDS
                ).setInputData(
                     inputData.build()
                ).setConstraints(constraints).build()
            )
            PeriodicWorkRequest.Builder(
                Worker::class.java, 24L, TimeUnit.HOURS, 1L, TimeUnit.HOURS
            ).addTag(tag).setInputData(
                 inputData.build()
            ).setConstraints(constraints).build()
        }
        WorkManager.getInstance(requireContext()).enqueue(myWorkRequest)
    }
    private fun setTimeAndDateInDialog() {
        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis
        Log.i("TAG", "setTimeAndDateInDialog: ${currentTime}")
        val timeAfterOneHour = calendar.get(Calendar.HOUR_OF_DAY)
        calendar.set(Calendar.HOUR_OF_DAY, timeAfterOneHour + 1)//+2
        customAlertDialogBinding.textViewEndDate.setDate(calendar.timeInMillis)
        customAlertDialogBinding.textViewEndTime.setTime(calendar.timeInMillis)
    }

    private fun setAlarm(minTime: Long, callback: (Long) -> Unit) {
        Calendar.getInstance().apply {
            this.set(Calendar.SECOND, 0)
            this.set(Calendar.MILLISECOND, 0)
            val datePickerDialog = DatePickerDialog(
                requireContext(), R.style.DialogTheme, { _, year, month, day ->
                    this.set(Calendar.YEAR, year)
                    this.set(Calendar.MONTH, month)
                    this.set(Calendar.DAY_OF_MONTH, day)
                    val timePickerDialog = TimePickerDialog(
                        requireContext(), R.style.DialogTheme, { _, hour, minute ->
                            this.set(Calendar.HOUR_OF_DAY, hour)
                            this.set(Calendar.MINUTE, minute)
                            callback(this.timeInMillis)
                        }, this.get(Calendar.HOUR_OF_DAY), this.get(Calendar.MINUTE), false
                    )
                    timePickerDialog.show()
                    timePickerDialog.setCancelable(false)
                    timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.LTGRAY)
                    timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN)
                },

                this.get(Calendar.YEAR), this.get(Calendar.MONTH), this.get(Calendar.DAY_OF_MONTH)

            )
            datePickerDialog.datePicker.minDate = minTime
            datePickerDialog.show()
            datePickerDialog.setCancelable(false)
            datePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.LTGRAY)
            datePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
    private fun sendNotification()
    {
        val imageBitmap = BitmapFactory.decodeResource(resources, R.drawable.baseline_add_alert_24)

        val builder = NotificationCompat.Builder(requireActivity(), CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("El-Kaff Hena")
            .setContentText("Wal3 Wal3")
            .setLargeIcon(imageBitmap)
            .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
        with(NotificationManagerCompat.from(requireActivity()))
        {
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(NOTIFICATION_ID,builder.build())
        }

    }
    private fun showNotificationPermissionDialog()
    {
        val alertDialogBuilder = AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.notification_permission_title))
            .setMessage(getString(R.string.notification_permmission_desc))
            .setPositiveButton(getString(R.string.permission_request)) { dialog: DialogInterface, _: Int ->
                // Open app settings to allow notification permission
                openAppSettings()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.Dismiss)) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .setCancelable(false)

        val dialog = alertDialogBuilder.create()
        dialog.show()
    }
    private fun openAppSettings()
    {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}
