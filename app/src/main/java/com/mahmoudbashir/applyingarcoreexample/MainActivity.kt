package com.mahmoudbashir.applyingarcoreexample

import android.content.pm.PackageManager
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.ar.core.ArCoreApk
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.ux.ArFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment
    private lateinit var overlayView: OverlayView
    private var transformationMatrix: Matrix? = null

    // Define the initial position of the arrow
    private var initialFloorX: Float = 200f
    private var initialFloorY: Float = 800f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment
        overlayView = findViewById(R.id.overlay_view)

        arFragment.arSceneView.scene.addOnUpdateListener(this::onUpdateFrame)

        setupARCore()
        setInitialPosition()
    }

    private fun onUpdateFrame(frameTime: FrameTime) {
        val frame = arFragment.arSceneView.arFrame

        if (frame != null) {
            val pose = frame.camera.pose

            val x = pose.tx()
            val y = pose.ty()
            val z = pose.tz()

            val result = String.format("X: %f\nY: %f\nZ: %f", x, y, z)
            lifecycleScope.launch {
              //  delay(30000L)
                Log.d("printingArCoordinates"," : $result")
                // Transform ARCore coordinates to floor plan coordinates
                transformToFloorPoint(x, y)
            }
        }
    }

    private fun setupARCore() {
        // Set up ARCore session and other necessary components

        // Example reference points
        val arCoreX1 = 1f
        val arCoreY1 = 1f
        val arCoreX2 = 2f
        val arCoreY2 = 2f

        val floorX1 = initialFloorX - 100f
        val floorY1 = initialFloorY - 100f
        val floorX2 = initialFloorX
        val floorY2 = initialFloorY

        transformationMatrix = calculateTransformationMatrix(
            arCoreX1, arCoreY1, arCoreX2, arCoreY2,
            floorX1, floorY1, floorX2, floorY2
        )
    }

    private fun transformToFloorPoint(arCoreX: Float, arCoreY: Float) {
        transformationMatrix?.let { matrix ->
            val (floorX, floorY) = transformCoordinates(matrix, arCoreX, arCoreY)
            updateArrowPosition(floorX, floorY)
        }
    }


    private fun updateArrowPosition(x: Float, y: Float) {
        runOnUiThread {
            overlayView.updateArrowPosition(x, y)
        }
    }

    private fun setInitialPosition() {
        updateArrowPosition(initialFloorX, initialFloorY)
    }

    override fun onResume() {
        super.onResume()
        requestCamera()
        ArCoreApk.getInstance().checkAvailabilityAsync(this) { availability ->
            if (availability.isSupported){
                if (arFragment.arSceneView.session == null) {
                    // Request camera permissions if not already granted
                    if (!CameraPermissionHelper.hasCameraPermission(this)) {
                        CameraPermissionHelper.requestCameraPermission(this)
                    }
                }
            } else {
                Toast.makeText(this, "ARCore is not supported on this device.", Toast.LENGTH_LONG)
                    .show()
               // finish()
            }
        }
    }

    private fun requestCamera() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                0
            )
        }
    }

    override fun onPause() {
        super.onPause()
        if (arFragment.arSceneView.session != null) {
            arFragment.arSceneView.pause()
        }
    }
}