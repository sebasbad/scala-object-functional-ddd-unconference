package unconference.scala.of.ddd.best

trait Metrics[C, V] {
  def subtraction: C => C
  def norm: () => V
  def distanceTo: C => V
}

trait Point[P] extends Metrics[P, Double]

case class Point_2D(x: Int, y: Int) extends Point[Point_2D] {
  import Math._

  def subtraction = p => copy(p.x - x, p.y - y)
  def norm = () => sqrt(pow(x, 2) + pow(y, 2))
  def distanceTo = subtraction andThen (_.norm())

  override def toString = s"($x, $y)"
}

object Point {
  def apply(x: Int, y: Int): Point_2D = Point_2D(x, y)
}

object Test {
  def main(args: Array[String]) {
    val p1 = Point(0, 0)
    val p2 = Point(1, 1)

    println(s"Distance between $p1 and $p2 is ${p1.distanceTo(p2)}")
  }
}
