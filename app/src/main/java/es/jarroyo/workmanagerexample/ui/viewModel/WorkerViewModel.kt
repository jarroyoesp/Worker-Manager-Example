package es.jarroyo.workmanagerexample.ui.viewModel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import es.jarroyo.workmanagerexample.app.workers.test.WorkerExample

class WorkerViewModel(): ViewModel() {

    private var mWorkManager: WorkManager = WorkManager.getInstance()
    private var mSavedWorkInfo: LiveData<List<WorkInfo>>

    init {
        mSavedWorkInfo = mWorkManager.getWorkInfosByTagLiveData(WorkerExample.TAG)
    }

    fun initWorker() {
        val data = Data.Builder()

        //Add parameter in Data class. just like bundle. You can also add Boolean and Number in parameter.
        data.putString(WorkerExample.ARG_EXTRA_PARAM, "PARAM_STRING")

        //Set Input Data
        val workerTest = OneTimeWorkRequestBuilder<WorkerExample>()
            .setInputData(data.build())
            .addTag(WorkerExample.TAG)
            .build()

        // Now, enqueue your work
        mWorkManager.enqueue(workerTest)
    }

    internal fun getOutputWorkInfo(): LiveData<List<WorkInfo>> {
        return mSavedWorkInfo
    }
}