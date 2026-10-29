package com.gamaxity.floating

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import kotlin.math.abs

class FloatingService : Service() {
    private lateinit var wm: WindowManager
    private var bubble: TextView? = null
    private var panel: View? = null
    private var panelParams: WindowManager.LayoutParams? = null

    private fun params(width: Int, height: Int, gravity: Int = Gravity.TOP or Gravity.START): WindowManager.LayoutParams =
        WindowManager.LayoutParams(width, height, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT).apply {
            this.gravity = gravity
            x = 24; y = 180
        }

    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        showBubble()
    }

    private fun showBubble() {
        if (bubble != null) return
        val b = TextView(this).apply {
            text = "G"
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
            setBackgroundColor(Color.rgb(0, 229, 255))
            elevation = 12f
            contentDescription = "GAMAXITY floating menu. Tap to open; drag to move."
        }
        val p = params(58, 58)
        var downX = 0f; var downY = 0f; var startX = 0; var startY = 0; var dragged = false
        b.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.rawX; downY = event.rawY; startX = p.x; startY = p.y; dragged = false; true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - downX).toInt(); val dy = (event.rawY - downY).toInt()
                    if (abs(dx) > 5 || abs(dy) > 5) dragged = true
                    p.x = startX + dx; p.y = startY + dy
                    try { wm.updateViewLayout(v, p) } catch (_: Exception) {}
                    true
                }
                MotionEvent.ACTION_UP -> { if (!dragged) togglePanel(); true }
                else -> true
            }
        }
        bubble = b
        wm.addView(b, p)
    }

    private fun togglePanel() {
        if (panel != null) { removePanel(); return }
        val outer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 16, 18, 16)
            setBackgroundColor(Color.rgb(8, 20, 38))
            elevation = 18f
        }
        val heading = TextView(this).apply {
            text = "GAMAXITY COMBAT MENU"
            textSize = 18f; typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.rgb(0, 229, 255)); setPadding(0, 0, 0, 10)
        }
        outer.addView(heading)
        val scroll = ScrollView(this)
        val items = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val features = listOf("Hitbox Visual Demo", "Glow Visual Demo", "Compact Menu", "Keep Bubble Visible")
        features.forEach { label ->
            val sw = Switch(this).apply {
                text = label; textSize = 14f; setTextColor(Color.WHITE); isChecked = label == "Keep Bubble Visible"
                setPadding(0, 6, 0, 6)
                setOnCheckedChangeListener { _, checked ->
                    if (label == "Keep Bubble Visible") bubble?.visibility = if (checked) View.VISIBLE else View.GONE
                    Toast.makeText(this@FloatingService, "$label: ${if (checked) "ON" else "OFF"} (menu demo only)", Toast.LENGTH_SHORT).show()
                }
            }
            items.addView(sw)
        }
        scroll.addView(items)
        outer.addView(scroll)
        val close = Button(this).apply { text = "CLOSE MENU"; setOnClickListener { removePanel() } }
        outer.addView(close)
        val pp = params(300, WindowManager.LayoutParams.WRAP_CONTENT).apply { x = 18; y = 110; flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL }
        panel = outer; panelParams = pp
        try { wm.addView(outer, pp) } catch (e: Exception) { panel = null; panelParams = null }
    }

    private fun removePanel() {
        panel?.let { try { wm.removeView(it) } catch (_: Exception) {} }
        panel = null; panelParams = null
    }

    override fun onDestroy() {
        removePanel()
        bubble?.let { try { wm.removeView(it) } catch (_: Exception) {} }
        bubble = null
        super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? = null
}
