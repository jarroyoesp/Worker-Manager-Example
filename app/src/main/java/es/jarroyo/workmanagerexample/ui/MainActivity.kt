package es.jarroyo.workmanagerexample.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import androidx.work.*
import es.jarroyo.workmanagerexample.R
import es.jarroyo.workmanagerexample.app.workers.test.WorkerExample
import es.jarroyo.workmanagerexample.ui.viewModel.WorkerViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private var mViewModel: WorkerViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configView()
    }

    /**
     * CONFIG VIEW
     */
    private fun configView() {
        // Worker Unique Power&Internet
        activity_main_button_worker_unique.setOnClickListener {
            initWorkerUniqueWithPowerAndConnectivity()
        }

        // Periodic worker
        activity_main_button_worker_periodic.setOnClickListener {
            initPeriodicWorker()
        }

        // Periodic worker
        activity_main_button_worker_with_param.setOnClickListener {
            initWorkerUniqueWithParamenters()
        }

        // Periodic worker
        activity_main_button_worker_view_model.setOnClickListener {
            configViewModel()
            initWorkerViewModel()
        }
    }

    /**
     * In this example this work was called when the device is connected to Internet and
     * is charging. If you click the button and this constraints are not satisfied then the works will not be
     * launched, but when connectivity and charging will be available, the workmanager will be in charge to
     * launch it. This work will not be lost.
     */
    private fun initWorkerUniqueWithPowerAndConnectivity() {
        // optionally, add constraints like power, network availability
        val constraints: Constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workerTest = OneTimeWorkRequestBuilder<WorkerExample>()
            .setConstraints(constraints).build()

        // Now, enqueue your work
        WorkManager.getInstance().beginUniqueWork(WorkerExample.TAG, ExistingWorkPolicy.REPLACE, workerTest)
    }

    /**
     * PERIODIC TASK
     * W/WM-WorkSpec: Interval duration lesser than minimum allowed value; Changed to 900000
     * If we want to cancel a worker we can add a Tag and cancel it by its Tag
     */
    private fun initPeriodicWorker() {
        val mWorkManager = WorkManager.getInstance()
        mWorkManager?.cancelAllWorkByTag(WorkerExample.TAG)

        val periodicBuilder = PeriodicWorkRequest.Builder(WorkerExample::class.java, 15, TimeUnit.MINUTES)
        val myWork = periodicBuilder.addTag(WorkerExample.TAG).build()
        mWorkManager?.enqueue(myWork)
    }

    /**
     * To send parameter to a Work:
     *    val data = Data.Builder()     *
     *   //Add parameter in Data class. just like bundle. You can also add Boolean and Number in parameter.
     *   data.putString(WorkerExample.ARG_EXTRA_PARAM, "your string param")
     *
     * And in the worker to get the data:
     *   val param =  inputData.getString(ARG_EXTRA_PARAM)
     */
    private fun initWorkerUniqueWithParamenters() {
        val data = Data.Builder()

        //Add parameter in Data class. just like bundle. You can also add Boolean and Number in parameter.
        data.putString(WorkerExample.ARG_EXTRA_PARAM, "your string param")

        //Set Input Data
        val workerTest = OneTimeWorkRequestBuilder<WorkerExample>()
            .setInputData(data.build())
            .setInitialDelay(10, TimeUnit.SECONDS)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS)
            .build()

        // Now, enqueue your work
        WorkManager.getInstance().enqueue(workerTest)
    }

    private fun configViewModel() {
        // Get the ViewModel
        mViewModel = ViewModelProviders.of(this).get(WorkerViewModel::class.java)

        // Show work status
        mViewModel!!.getOutputWorkInfo().observe(this, observer)

    }

    private fun initWorkerViewModel() {
        showWorkerStatus("Init")
        mViewModel!!.initWorker()
    }

    private val observer = Observer<List<WorkInfo>> { state ->
        state?.let {
            // Note that these next few lines grab a single WorkInfo if it exists
            // This code could be in a Transformation in the ViewModel; they are included here
            // so that the entire process of displaying a WorkInfo is in one location.

            // If there are no matching work info, do nothing
            if (it == null || it.isEmpty()) {
                showWorkerStatus("Empty")
            } else {

                // We only care about the one output status.
                // Every continuation has only one worker tagged TAG_OUTPUT
                val workInfo = it.get(0)

                val finished = workInfo.getState().isFinished()
                if (finished) {
                    showWorkerStatus("Finished")
                } else {
                    showWorkerStatus("Working")
                }
            }
        }
    }

    private fun showWorkerStatus(message: String) {
        activity_main_tv_status.text = message
    }
}
