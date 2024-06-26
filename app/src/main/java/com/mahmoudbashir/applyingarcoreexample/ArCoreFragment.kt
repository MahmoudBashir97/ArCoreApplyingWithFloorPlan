package com.mahmoudbashir.applyingarcoreexample

import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment

class ArCoreFragment : ArFragment() {
    override fun getSessionConfiguration(session: Session?): Config? {
        val config = Config(session)
        config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
        return config
    }
}