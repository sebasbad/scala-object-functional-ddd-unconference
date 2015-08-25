package unconference.scala.of.ddd.ddd.types

/**
 * @author kevin 
 * @since 8/26/15.
 */
case class NonEmptyString(value: String) {
  require(value != null && value.trim.nonEmpty)

  override def toString = value
}

object NonEmptyString {
  implicit def strToNonEmpty(a: String): NonEmptyString = NonEmptyString(a)
  implicit def nonEmptyToStr(a: NonEmptyString): String = a.toString
}
