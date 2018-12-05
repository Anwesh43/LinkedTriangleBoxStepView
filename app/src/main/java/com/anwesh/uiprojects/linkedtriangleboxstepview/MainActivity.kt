package com.anwesh.uiprojects.linkedtriangleboxstepview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.triangleboxstepview.TriangleBoxStepView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TriangleBoxStepView.create(this)
    }
}
