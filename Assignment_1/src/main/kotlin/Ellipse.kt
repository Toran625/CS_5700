import kotlin.math.PI

open class Ellipse(center: Point, private val radiusX: Double, private val radiusY: Double) {
    private val center: Point = center.clone()

    init{
        if(radiusX == 0.0 || radiusY == 0.0){
            throw IllegalArgumentException("Ellipse can't have area of 0.")
        }
    }

    fun getCenter(): Point = center.clone()
    fun getRadiusX(): Double = radiusX
    fun getRadiusY(): Double = radiusY
    fun getArea(): Double = PI * radiusX * radiusY

    fun move(deltaX: Double, deltaY: Double){
        center.move(deltaX, deltaY)
    }
}

class Circle(center: Point, radius: Double) : Ellipse(center, radius, radius)