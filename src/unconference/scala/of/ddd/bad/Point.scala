package unconference.scala.of.ddd.bad

class Point(private var x: Int, private var y: Int) {
  def setX(x: Int) = this.x = x
  def getX() = this.x

  def setY(y: Int) = this.y = y
  def getY() = this.y

  override def toString = s"($x, $y)"
}

object Test {
  def main(args: Array[String]) {
    val p1 = new Point(1, 1)
    val p2 = new Point(2, 2)

    println(s"Distance between $p1 and $p2 is ${computeDistance(p1, p2)}")
  }

  def computeDistance(p1: Point, p2: Point) = {
    // state inspection
    val x1 = p1 getX()
    val x2 = p2 getX()

    val y1 = p1 getY()
    val y2 = p2 getY()

    // implementation detail (distance in a 2-dimension point)
    Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2))
  }
}