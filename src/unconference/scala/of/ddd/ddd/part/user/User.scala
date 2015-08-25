package unconference.scala.of.ddd.ddd.part.user

import unconference.scala.of.ddd.ddd.types.NonEmptyString

trait UserDTO {
  def getId: () => String
  def getEmail: () => String
  def getPassword: () => String
  def getRoles: () => Seq[String]
}

trait User {
  def changePassword: String => User
  def withRole: String => User
  def toDTO: () => UserDTO
}

case class DefaultUser(id: NonEmptyString, email: NonEmptyString, password: NonEmptyString, roles: Seq[String]) extends User with UserDTO {
  def changePassword = newEmail => copy(email = newEmail)
  def withRole = role => copy(roles = (roles ++ Seq(role)).distinct)

  def toDTO = () => this

  def getId = () => id
  def getEmail = () => email
  def getPassword = () => password
  def getRoles = () => roles
}

object User {
  def apply(id: NonEmptyString, email: NonEmptyString, password: NonEmptyString, roles: Seq[String]): DefaultUser = {
    DefaultUser(id, email, password, roles)
  }
}