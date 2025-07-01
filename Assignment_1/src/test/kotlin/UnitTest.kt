import kotlin.test.*

class UnitTest {

    @Test
    fun testPointMoveAndClone() {
        val point = Point(1.0, 2.0)
        val clone = point.clone()
        assertEquals(1.0, clone.getX())
        assertEquals(2.0, clone.getY())

        point.move(3.0, 4.0)
        assertEquals(4.0, point.getX())
        assertEquals(6.0, point.getY())

        assertEquals(1.0, clone.getX())
        assertEquals(2.0, clone.getY())
    }

    @Test
    fun testLineLengthAndSlope() {
        val pointA = Point(0.0, 0.0)
        val pointB = Point(3.0, 4.0)
        val line = Line(pointA, pointB)

        assertEquals(5.0, line.getLength(), 0.0001)
        assertEquals(4.0 / 3.0, line.getSlope(), 0.0001)
    }

    @Test
    fun testLineMove() {
        val line = Line(Point(0.0, 0.0), Point(1.0, 1.0))
        line.move(1.0, 2.0)
        val points = line.getPoints()
        assertEquals(1.0, points[0].getX())
        assertEquals(2.0, points[0].getY())
        assertEquals(2.0, points[1].getX())
        assertEquals(3.0, points[1].getY())
    }

    @Test
    fun testInvalidLineThrows() {
        assertFailsWith<IllegalArgumentException> {
            Line(Point(1.0, 1.0), Point(1.0, 1.0))
        }
    }

    @Test
    fun testRectangleAreaAndMove() {
        val rectangle = Rectangle(Point(0.0, 4.0), Point(3.0, 0.0))
        assertEquals(12.0, rectangle.getArea(), 0.0001)
        rectangle.move(1.0, 1.0)
        val points = rectangle.getPoints()
        assertEquals(1.0, points[0].getX())
        assertEquals(5.0, points[0].getY())
        assertEquals(4.0, points[1].getX())
        assertEquals(1.0, points[1].getY())
    }

    @Test
    fun testInvalidRectangleThrows() {
        assertFailsWith<IllegalArgumentException> {
            Rectangle(Point(1.0, 1.0), Point(1.0, 1.0))
        }
    }

    @Test
    fun testSquareArea() {
        val square = Square(Point(0.0, 2.0), 2.0)
        assertEquals(4.0, square.getArea(), 0.0001)
    }

    @Test
    fun testEllipseAreaAndMove() {
        val ellipse = Ellipse(Point(0.0, 0.0), 3.0, 4.0)
        assertEquals(Math.PI * 3.0 * 4.0, ellipse.getArea(), 0.0001)
        ellipse.move(1.0, 1.0)
        val center = ellipse.getCenter()
        assertEquals(1.0, center.getX())
        assertEquals(1.0, center.getY())
    }

    @Test
    fun testInvalidEllipseThrows() {
        assertFailsWith<IllegalArgumentException> {
            Ellipse(Point(0.0, 0.0), 0.0, 2.0)
        }
    }

    @Test
    fun testCircleAreaAndRadii() {
        val circle = Circle(Point(0.0, 0.0), 2.0)
        assertEquals(Math.PI * 4.0, circle.getArea(), 0.0001)
        val rx = circle.getRadiusX()
        val ry = circle.getRadiusY()
        assertEquals(2.0, rx)
        assertEquals(2.0, ry)
    }

    @Test
    fun testInvalidCircleThrows() {
        assertFailsWith<IllegalArgumentException> {
            Circle(Point(0.0, 0.0), 0.0)
        }
    }

    @Test
    fun testTriangleAreaAndMove() {
        val triangle = Triangle(Point(0.0, 0.0), Point(4.0, 0.0), Point(0.0, 3.0))
        assertEquals(6.0, triangle.getArea(), 0.0001)

        triangle.move(1.0, 1.0)
        val points = triangle.getPoints()

        assertEquals(1.0, points[0].getX())
        assertEquals(1.0, points[0].getY())
    }

    @Test
    fun testInvalidTriangleThrows() {
        assertFailsWith<IllegalArgumentException> {
            Triangle(Point(0.0, 0.0), Point(1.0, 1.0), Point(2.0, 2.0))
        }
    }
}
