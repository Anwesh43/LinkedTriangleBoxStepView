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
val strokeFactor : Int = 90
val color : Int = Color.parseColor("#0277BD")
val  DELAY : Long = 25

fun Int.getInverse() : Float = 1f / this

fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.getInverse(), Math.max(0f, this - i * n.getInverse())) * n

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
    val xGap : Float = size / (lines + 1)
    paint.color = color
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.strokeCap = Paint.Cap.ROUND
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
        translate(-size / 2 + xGap * (j + 1), -size / 6)
        rotate(oDeg)
        drawLine(0f, 0f, 0f, (-size / 3) * sc, paint)
        restore()
    }
    restore()
}

class TriangleBoxStepView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scale.updateScale(dir, lines, 1)
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(DELAY)
                    view.invalidate()
                } catch (ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class TBSNode(var i : Int, val state : State = State()) {

        private var next : TBSNode? = null

        private var prev : TBSNode? = null

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawTBSNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = TBSNode(i + 1)
                next?.prev = this
            }
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : TBSNode {
            var curr : TBSNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class TriangleBoxStep(var i : Int) {
        private val root : TBSNode = TBSNode(0)
        private var curr : TBSNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : TriangleBoxStepView) {

        private val tbs : TriangleBoxStep = TriangleBoxStep(0)
        private val animator : Animator = Animator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#BDBDBD"))
            tbs.draw(canvas, paint)
            animator.animate {
                tbs.update {i, scl ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            tbs.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity: Activity) : TriangleBoxStepView {
            val view : TriangleBoxStepView = TriangleBoxStepView(activity)
            activity.setContentView(view)
            return view
        }
    }
}