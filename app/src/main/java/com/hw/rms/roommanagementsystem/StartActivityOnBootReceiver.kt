package com.hw.rms.roommanagementsystem

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StartActivityOnBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(Intent.ACTION_BOOT_COMPLETED == intent?.action){
            context?.startActivity(Intent(context,RootActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
}