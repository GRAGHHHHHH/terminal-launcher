package com.example.terminallauncher

import android.app.Activity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.content.Intent
import kotlin.math.abs

class MainActivity : Activity() {

    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gestureDetector = GestureDetector(this, SwipeListener())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    private inner class SwipeListener : GestureDetector.SimpleOnGestureListener() {

        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val diffY = (e2.y) - (e1?.y ?: 0f)
            val diffX = (e2.x) - (e1?.x ?: 0f)

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
}
