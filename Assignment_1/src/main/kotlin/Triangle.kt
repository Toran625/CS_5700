import kotlin.math.abs

class Triangle(pointA: Point, pointB: Point, pointC: Point) {
    private val pointA = pointA.clone()
    private val pointB = pointB.clone()
    private val pointC = pointC.clone()

    init {
        if (getArea() == 0.0)
            throw IllegalArgumentException("Points must not be collinear.")
    }

    fun getPoints(): List<Point> = listOf(pointA.clone(), pointB.clone(), pointC.clone())

    fun getArea(): Double {
        val x1 = pointA.getX();
        val y1 = pointA.getY()
        val x2 = pointB.getX();
        val y2 = pointB.getY()
        val x3 = pointC.getX();
        val y3 = pointC.getY()
        return abs(x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)) / 2
    }

    fun move(dx: Double, dy: Double) {
        pointA.move(dx, dy)
        pointB.move(dx, dy)
        pointC.move(dx, dy)
    }
}
