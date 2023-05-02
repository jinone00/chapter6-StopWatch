package com.example.chapter6

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.example.chapter6.databinding.ActivityMainBinding
import com.example.chapter6.databinding.DialogCountdownSettingBinding
import java.util.Timer
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var countdownSecond = 10
    private var currentDeciSecond = 0
    private var timer: Timer? = null
    private var currentCountdownDeciSecond = countdownSecond * 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.countdownTextView.setOnClickListener {
            showCountdownSettingDialog()
        }

        binding.startButton.setOnClickListener {
            start()
            binding.startButton.isVisible = false
            binding.stopButton.isVisible = false
            binding.pauseButton.isVisible = true
            binding.lapButton.isVisible = true
        }

        binding.stopButton.setOnClickListener {
            showAlertdialog()
        }

        binding.pauseButton.setOnClickListener {
            pause()
            binding.startButton.isVisible = true
            binding.stopButton.isVisible = true
            binding.pauseButton.isVisible = false
            binding.lapButton.isVisible = false
        }

        binding.lapButton.setOnClickListener {
            lap()
        }
        initCountdownViews()


    }

    private fun initCountdownViews(){
        val second = currentCountdownDeciSecond/10
        binding.countdownTextView.text = String.format("%02d", countdownSecond)
        binding.countdownProgressBar.progress = 100
    }

    private fun start() {
        timer = timer(initialDelay = 0, period = 100) {
            if(currentCountdownDeciSecond == 0){
                currentDeciSecond += 1
                val minutes = currentDeciSecond.div(10) / 60
                val seconds = currentDeciSecond.div(10) % 60
                val deciSeconds = currentDeciSecond % 10

                runOnUiThread {
                    binding.timeTextView.text =
                        String.format("%02d:%02d", minutes, seconds)
                    binding.tickTextView.text = deciSeconds.toString()
                    binding.countdownGroup.isVisible = false
                }
            } else{
                currentCountdownDeciSecond -= 1
                val second = currentCountdownDeciSecond/10
                val progress = (currentCountdownDeciSecond)

                binding.root.post{
                    binding.countdownTextView.text = String.format("%02d", second)
                    binding.countdownProgressBar.progress = progress.toInt()
                }
            }



        }
    }

    private fun stop() {
        binding.startButton.isVisible = true
        binding.stopButton.isVisible = true
        binding.pauseButton.isVisible = false
        binding.lapButton.isVisible = false

        currentDeciSecond = 0
        binding.timeTextView.text = "00:00"
        binding.tickTextView.text = "0"

        binding.countdownGroup.isVisible = true
        binding.lapContainerLineaLayout.removeAllViews()
        initCountdownViews()
    }

    private fun pause() {
        timer?.cancel()
        timer = null
    }

    @SuppressLint("SetTextI18n")
    private fun lap() {
        val container = binding.lapContainerLineaLayout

        TextView(this).apply {
            textSize = 20f
            gravity = Gravity.CENTER
            val minutes = currentDeciSecond.div(10) / 60
            val seconds = currentDeciSecond.div(10) % 60
            val deciSeconds = currentDeciSecond % 10
            text = container.childCount.inc().toString() + " " + String.format(
                "%02d:%02d %01d",
                minutes,
                seconds,
                deciSeconds
                )
            // 1. 01:03 0
            setPadding(30)
        }.let{labTextView ->
            container.addView(labTextView, 0)
        }

    }

    private fun showCountdownSettingDialog() {
        AlertDialog.Builder(this).apply {
            val dialogBinding = DialogCountdownSettingBinding.inflate(layoutInflater)
            with(dialogBinding.countdownSecondPicker) {
                maxValue = 20
                minValue = 0
                value = countdownSecond
            }
            setTitle("카운트 다운 설정")
            setView(dialogBinding.root)
            setPositiveButton("확인") { _, _ ->
                countdownSecond = dialogBinding.countdownSecondPicker.value
                currentCountdownDeciSecond = countdownSecond * 10
                binding.countdownTextView.text = String.format("%02d", countdownSecond)
            }
            setNegativeButton("취소", null)
        }.show()
    }


    private fun showAlertdialog() {
        AlertDialog.Builder(this).apply {
            setMessage("종료 할거냐?")
            setPositiveButton("넵") { _, _ ->
                stop()
            }
            setNegativeButton("아니욥", null)
        }.show()
    }


}