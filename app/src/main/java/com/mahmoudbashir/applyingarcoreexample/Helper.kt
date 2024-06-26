package com.mahmoudbashir.applyingarcoreexample

import android.graphics.Matrix

fun calculateTransformationMatrix(
    arCoreX1: Float, arCoreY1: Float, arCoreX2: Float, arCoreY2: Float,
    floorX1: Float, floorY1: Float, floorX2: Float, floorY2: Float
): Matrix {
    val matrix = Matrix()

    val dx1 = arCoreX1 - arCoreX2
    val dy1 = arCoreY1 - arCoreY2
    val dx2 = floorX1 - floorX2
    val dy2 = floorY1 - floorY2

    val scaleX = dx2 / dx1
    val scaleY = dy2 / dy1

    val rotation = Math.atan2(dy2.toDouble(), dx2.toDouble()) - Math.atan2(dy1.toDouble(), dx1.toDouble())

    matrix.setScale(scaleX, scaleY)
    matrix.postRotate(Math.toDegrees(rotation).toFloat())
    matrix.postTranslate(floorX1 - arCoreX1 * scaleX, floorY1 - arCoreY1 * scaleY)

    return matrix
}

fun transformCoordinates(
    matrix: Matrix,
    arCoreX: Float, arCoreY: Float
): Pair<Float, Float> {
    val src = floatArrayOf(arCoreX, arCoreY)
    val dst = FloatArray(2)
    matrix.mapPoints(dst, src)
    return Pair(dst[0], dst[1])
}