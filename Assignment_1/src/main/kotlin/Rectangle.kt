import kotlin.math.abs

open class Rectangle(pointA: Point, pointB: Point) {
    private val pointA: Point = pointA.clone()
    private val pointB: Point = pointB.clone()

    init{
        if(pointA.getX() == pointB.getX() || pointA.getY() == pointB.getY()){
            throw IllegalArgumentException("Rectangle can't have width or height of 0.")
        }
    }

    fun getPoints(): List<Point> = listOf(pointA.clone(), pointB.clone())

    fun getArea(): Double {
        val deltaX = pointB.getX() - pointA.getX()
        val deltaY = pointB.getY() - pointA.getY()

        return abs((deltaX * deltaY))
    }

    fun move(deltaX: Double, deltaY: Double){
        pointA.move(deltaX, deltaY)
        pointB.move(deltaX, deltaY)
    }

}

class Square(pointA: Point, sideLength: Double) : Rectangle(pointA,Point(pointA.getX() + sideLength, pointA.getY() - sideLength))