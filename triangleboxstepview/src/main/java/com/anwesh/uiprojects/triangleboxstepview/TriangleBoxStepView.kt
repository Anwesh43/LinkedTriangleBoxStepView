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