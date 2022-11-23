package onlab.mlkit.tiktok.ui

import android.os.CountDownTimer
import onlab.mlkit.tiktok.CameraActivity

abstract class CountUpTimer(
    private val secondsInFuture: Int,
    countUpIntervalSeconds: Int,
) : CountDownTimer(secondsInFuture.toLong() * 1000, countUpIntervalSeconds.toLong() * 1000) {
    abstract fun onCount(count: Int)

    override fun onTick(msUntilFinished: Long) {
        onCount(((secondsInFuture.toLong() * 1000 - msUntilFinished) / 1000).toInt())
    }
}