package com.example.quizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.constraintlayout.motion.widget.MotionLayout
import com.example.quizapp.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {

    lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val animTop = AnimationUtils.loadAnimation(this, R.anim.top_amin)
        val animBot = AnimationUtils.loadAnimation(this, R.anim.bottom_anim)
        binding.image.animation = animTop
        binding.welcome.animation = animBot
        binding.madeBy.animation = animBot

        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000) // 3000 is the delayed time in milliseconds.
    }
}