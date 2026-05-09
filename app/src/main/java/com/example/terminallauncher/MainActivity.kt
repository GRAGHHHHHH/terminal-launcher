package com.example.terminallauncher

import android.app.Activity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.content.Intent
import android.view.WindowManager
import kotlin.math.abs

class MainActivity : Activity() {

    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tell Android this is a proper home screen and attach to the real wallpaper
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER)

        // Prevent the previous app screen from bleeding through
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        gestureDetector = GestureDetector(this, SwipeListener())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    private inner class SwipeListener : GestureDetector.SimpleOnGestureListener() {

        private val SWIPE_THRESHOLD = 80
        private val SWIPE_VELOCITY_THRESHOLD = 80

        // onDown must return true — otherwise Android discards all subsequent events
        override fun onDown(e: MotionEvent): Boolean = true

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val diffY = e2.y - (e1?.y ?: 0f)
            val diffX = e2.x - (e1?.x ?: 0f)

            if (abs(diffY) > abs(diffX) &&
                abs(diffY) > SWIPE_THRESHOLD &&
                abs(velocityY) > SWIPE_VELOCITY_THRESHOLD &&
                diffY < 0
            ) {
                openTerminal()
                return true
            }
            return false
        }
    }

    private fun openTerminal() {
        val intent = Intent(this, TerminalActivity::class.java)
        startActivity(intent)
    }

    // Prevent back button from doing anything on the home screen
    override fun onBackPressed() {}
}
