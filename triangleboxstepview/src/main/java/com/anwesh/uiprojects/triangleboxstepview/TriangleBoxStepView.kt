package com.anwesh.uiprojects.triangleboxstepview

/**
 * Created by anweshmishra on 06/12/18.
 */

import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.app.Activity
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Path

val nodes : Int = 5
val lines : Int = 3
val scDiv : Double = 0.51
val scGap : Float = 0.05f
val sizeFactor : Float = 2.3f
val strokeFactor : Int = 50
val color : Int = Color.parseColor("#0277BD")

fun Int.getInverse() : Float = 1f / this

fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.getInverse(), Math.max(0f, this - i * n.getInverse()))

fun Float.scaleFactor() : Float = Math.floor(this / scDiv).toFloat()

fun Float.mirrorValue(a : Int, b : Int) : Float = (1 - scaleFactor()) * a.getInverse() + scaleFactor() * b.getInverse()

fun Float.updateScale(dir : Float, a : Int, b : Int) : Float = scGap * dir * mirrorValue(a, b)

fun Canvas.drawTBSNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / (nodes + 1)
    val sc1 : Float = scale.divideScale(0, 2)
    val sc2 : Float = scale.divideScale(1, 2)
    val size : Float = gap / sizeFactor
    val xGap : Float = size / (nodes + 1)
    save()
    translate(w/2, gap * (i + 1))
    rotate(90f * sc2)
    val path : Path = Path()
    path.moveTo(0f, size)
    path.lineTo(-size/2, 0f)
    path.lineTo(size/2, 0f)
    drawPath(path, paint)
    for (j in 0..(lines - 1)) {
        val sc : Float = sc1.divideScale(j, lines)
        val oDeg : Float = -30f + j * 30f
        save()
        translate(-size / 2 + xGap * (j + 1), size / 6)
        rotate(oDeg)
        drawLine(0f, 0f, 0f, (-size / 3) * sc, paint)
        restore()
    }
    restore()
}