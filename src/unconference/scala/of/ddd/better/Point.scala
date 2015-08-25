package unconference.scala.of.ddd.better

trait Point[T <: Point[_]] { // interface oriented
  def distanceTo(point: T): Double
}

case class Point_2D(private val x: Int, private val y: Int) extends Point[Point_2D] {
  def distanceTo(point: Point_2D) = {
    Math.sqrt(Math.pow(point.x - x, 2) + Math.pow(point.y - y, 2))
  }
  override def toString = s"($x, $y)"
}

object Point {
  def apply(x: Int, y: Int): Point_2D = Point_2D(x, y) // Smart constructor
}

object Test {
  def main(args: Array[String]) {
    val p1 = Point(1, 1)
    val p2 = Point(2, 2)

    println(s"Distance between $p1 and $p2 is ${p1.distanceTo(p2)}")
  }
}
