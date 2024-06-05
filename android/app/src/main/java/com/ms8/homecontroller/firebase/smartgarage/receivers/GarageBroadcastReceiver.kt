package com.ms8.homecontroller.firebase.smartgarage.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.os.Process
import android.util.Log
import java.lang.ref.WeakReference


class GarageBroadcastReceiver : BroadcastReceiver(), Handler.Callback {
    override fun onReceive(context: Context?, intent: Intent) {
        Log.d(TAG, "onReceive")
        val pendingResult = goAsync()
        val handlerThread = HandlerThread("GarageThread", Process.THREAD_PRIORITY_BACKGROUND)
        handlerThread.start()
        val looper = handlerThread.getLooper()
        val handler = Handler(looper, this)
        // Register the broadcast receiver to run on the separate Thread
    }

    companion object {
        private const val path = "com.ms8.homecontroller.firebase.smartgarage.functions"
        const val ACTION_CANCEL_AUTO_CLOSE = "$path.cancel_auto_close"
        const val ACTION_CLOSE_DOOR = "$path.actions.close_garage"
        const val ACTION_OPEN_DOOR = "$path.actions.open_garage"

        const val TAG = "GarageBroadcastReceiver"
    }

    override fun handleMessage(msg: Message): Boolean {
        TODO("Not yet implemented")
    }



//    private class Task (private val pendingResult: PendingResult, private val intent: Intent, private val contextRef: WeakReference<Context>)
//        : AsyncTask<String, Int, String>() {
//        override fun doInBackground(vararg p0: String?): String {
//            Log.d(TAG, "Handling Intent with action: ${intent.action}")
//            when (intent.action) {
//                ACTION_CANCEL_AUTO_CLOSE -> FirebaseDatabaseFunctions
//                    .sendGarageAction(FirebaseDatabaseFunctions.ActionType.STOP_AUTO_CLOSE)
//                    .also { (contextRef.get()?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
//                        .cancel(notificationId) }
//                ACTION_CLOSE_DOOR -> FirebaseDatabaseFunctions
//                    .sendGarageAction(FirebaseDatabaseFunctions.ActionType.CLOSE)
//                ACTION_OPEN_DOOR -> FirebaseDatabaseFunctions
//                    .sendGarageAction(FirebaseDatabaseFunctions.ActionType.OPEN)
//            }
//
//            return toString()
//        }
//
//        override fun onPostExecute(result: String?) {
//            super.onPostExecute(result)
//            pendingResult.finish()
//        }
//    }
}