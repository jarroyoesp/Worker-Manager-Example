package es.jarroyo.workmanagerexample.app.workers.test

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

/**
 * EXAMPLE WORKER to do a task in background
 */
class WorkerExample(context: Context, params: WorkerParameters)
    : Worker(context, params) {

    companion object {
        val TAG = WorkerExample::class.java.simpleName
        val ARG_EXTRA_PARAM = "ARG_EXTRA_PARAM"

    }

    override fun doWork(): Result {

        // Get data
        val param =  inputData.getString(ARG_EXTRA_PARAM)


        //do the work you want done in the background here
        Log.d(TAG, "Worker Test: doWork() called & param = $param")
        return Result.success()
    }
}