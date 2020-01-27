package com.ab.servitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables

class BarcodeReceiver {

    fun from(action: String, context: Context): Observable<Intent> {
        val observable = Observable.create<Intent> {observer ->
            val receiver = object:BroadcastReceiver(){
                override fun onReceive(context: Context?, intent: Intent?) {
                    observer.onNext(intent)
                }
            }
            observer.setDisposable(Disposables.fromRunnable {
                context.unregisterReceiver(receiver)
            })
            context.registerReceiver(receiver, IntentFilter(action))
        }
        return observable.subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
