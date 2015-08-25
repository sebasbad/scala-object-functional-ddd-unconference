package unconference.scala.of.ddd.ddd.part.user

import scala.concurrent.{ExecutionContext, Future}

trait UserService {
  def register: (String, String, String) => Future[UserDTO]
  def addRole: (String, String) => Future[UserDTO]
}

case class DefaultUserService(repository: UserRepository)(implicit ec: ExecutionContext) extends UserService {
  implicit def wrap(user: User): Future[User] = Future.successful(user)

  def register: (String, String, String) => Future[UserDTO] = (id, email, password) => for (
    slot <- repository.slotOnEmail(email) ;
    userToSave <- User(id, email, password, Seq("everyone")) ;
    savedUser <- repository.save(userToSave)
  ) yield savedUser.toDTO()

  def addRole: (String, String) => Future[UserDTO] = (userId, role) => for (
    user <- repository.byId(userId) ;
    changedUser <- user.withRole(role) ;
    savedUser <- repository.save(changedUser)
  ) yield savedUser.toDTO()
}

object UserService {
  def apply(repository: UserRepository)(implicit ec: ExecutionContext = ExecutionContext.Implicits.global): DefaultUserService = {
    DefaultUserService(repository)
  }
}

object Test {
  implicit object SingleThreadExecutionContext extends ExecutionContext {
    def execute(runnable: Runnable) = runnable.run()
    def reportFailure(cause: Throwable) = throw cause
  }

  def main(args: Array[String]) {
    val service = UserService(UserRepository(SingleThreadExecutionContext))(SingleThreadExecutionContext)
    val result = for (
      registeredUser <- service.register("userId", "email@wow.com", "soSecure") ;
      adminUser <- service.addRole(registeredUser.getId(), "administrator")
    ) yield adminUser

    result.foreach(println)
  }
}