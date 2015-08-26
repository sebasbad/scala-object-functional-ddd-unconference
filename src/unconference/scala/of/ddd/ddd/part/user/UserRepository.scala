package unconference.scala.of.ddd.ddd.part.user

import scala.concurrent.{ExecutionContext, Future}

trait UserRepository {
  def slotOnEmail: String => Future[Unit]
  def save: User => Future[User]
  def byId: String => Future[User]
}

/**
 * A Repository has mutable state by design. This usually is an external database.
 */
class InMemoryUserRepository(private var users: Seq[User])(implicit ec: ExecutionContext) extends UserRepository {
  val noSuchElement = (id: String) => Future.failed[User](new NoSuchElementException(s"Could not find user with id $id"))
  val alreadyExists = (email: String) => Future.failed[Unit](new IllegalArgumentException(s"User with email $email already exists"))

  def slotOnEmail = email => this.users.find(_.toDTO().getEmail() == email).fold(Future.successful[Unit]())(_ => alreadyExists(email))

  def save = user => Future {
    this.users = this.users.filter(_.toDTO().getId() != user.toDTO().getId()) ++ Seq(user)
    user
  }

  def byId = id => this.users.find(_.toDTO().getId() == id).fold(noSuchElement(id))(user => Future.successful(user))
}

object UserRepository {
  def apply()(implicit ec: ExecutionContext = ExecutionContext.Implicits.global): InMemoryUserRepository = {
    new InMemoryUserRepository(Seq())
  }
}