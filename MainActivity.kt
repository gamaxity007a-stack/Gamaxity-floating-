package com.gamaxity.floating

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 28, 28, 28)
            setBackgroundColor(Color.rgb(7, 17, 31))
        }
        val title = TextView(this).apply {
            text = "GAMAXITY\nFLOATING MENU"
            textSize = 26f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(0, 229, 255))
        }
        val desc = TextView(this).apply {
            text = "Start a draggable bubble and open the on-screen menu. Switches in this demo only change the menu's local demo state; they do not control Minecraft gameplay."
            textSize = 15f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setPadding(0, 22, 0, 22)
        }
        val start = Button(this).apply { text = "ENABLE FLOATING MENU" }
        val stop = Button(this).apply { text = "STOP FLOATING MENU" }
        root.addView(title)
        root.addView(desc)
        root.addView(start, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        root.addView(stop, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        setContentView(root)
        start.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            } else {
                startService(Intent(this, FloatingService::class.java))
            }
        }
        stop.setOnClickListener { stopService(Intent(this, FloatingService::class.java)) }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
            // User can press Enable again after granting permission.
        }
    }
}
