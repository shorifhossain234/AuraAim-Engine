package com.auraim.engine.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: AimOverlayView

    override fun onCreate() {
        super.onCreate()
        startForegroundServiceNotification()
        setupOverlayWindow()
    }

    private fun startForegroundServiceNotification() {
        val channelId = "auraim_service_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "AuraAim Tracking Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("AuraAim Engine Active")
            .setContentText("Real-time Trajectory Prediction is running")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .build()

        startForeground(1001, notification)
    }

    private fun setupOverlayWindow() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        overlayView = AimOverlayView(this)

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        layoutParams.gravity = Gravity.TOP or Gravity.START
        windowManager.addView(overlayView, layoutParams)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        if (::overlayView.isInitialized) {
            windowManager.removeView(overlayView)
        }
    }

    // Inner Custom View for rendering laser aim trajectory lines
    private class AimOverlayView(context: android.content.Context) : View(context) {
        private val laserPaint = Paint().apply {
            color = Color.parseColor("#D946EF") // Neon Purple
            strokeWidth = 6f
            style = Paint.Style.STROKE
            isAntiAlias = true
            pathEffect = DashPathEffect(floatArrayOf(15f, 10f), 0f)
        }

        private val targetPuckPaint = Paint().apply {
            color = Color.parseColor("#22C55E") // Emerald Green for target path
            strokeWidth = 7f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            // Example calculated trajectory lines preview
            // Ray 1: Striker to Wall Bounce
            canvas.drawLine(300f, 1200f, 100f, 600f, laserPaint)
            // Ray 2: Wall Reflection to Target Puck
            canvas.drawLine(100f, 600f, 500f, 400f, laserPaint)
            // Ray 3: Target Puck Line to Pocket
            canvas.drawLine(500f, 400f, 850f, 150f, targetPuckPaint)
        }
    }
}
