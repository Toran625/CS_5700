import kotlin.math.hypot

class Line (pointA: Point, pointB: Point){
    private val pointA: Point = pointA.clone()
    private val pointB: Point = pointB.clone()

    init{
        if (pointA.getX() == pointB.getX() && pointA.getY() == pointB.getY()){
            throw IllegalArgumentException("Line can't have length 0.")
        }
    }

    fun getPoints(): List<Point> = listOf(pointA.clone(), pointB.clone())

    fun getSlope(): Double {
        val deltaX = pointB.getX() - pointA.getX()
        val deltaY = pointB.getY() - pointA.getY()

        if (deltaX == 0.0) throw ArithmeticException("Slope is undefined.")

        return deltaY / deltaX
    }

    fun getLength(): Double {
        val deltaX = pointB.getX() - pointA.getX()
        val deltaY = pointB.getY() - pointA.getY()

        return hypot(deltaX, deltaY)
    }

    fun move(deltaX: Double, deltaY: Double){
        pointA.move(deltaX, deltaY)
        pointB.move(deltaX, deltaY)
    }

}