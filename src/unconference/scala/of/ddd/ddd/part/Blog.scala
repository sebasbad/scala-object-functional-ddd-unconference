package unconference.scala.of.ddd.ddd.part

import unconference.scala.of.ddd.ddd.part.post.{PostService, PostDTO}
import unconference.scala.of.ddd.ddd.part.user.{UserService, UserDTO}

import scala.concurrent.{ExecutionContext, Future}

/**
 * @author kevin 
 * @since 9/14/15.
 */

trait Blog {
  def registerUser: (String, String, String) => Future[UserDTO]
  def addPost: (String, String, String, String, Seq[String]) => Future[PostDTO]
  def publishPost: (String, String) => Future[PostDTO]
}

case class DefaultBlog(userService: UserService, postService: PostService)(implicit ec: ExecutionContext) extends Blog {
  def registerUser: (String, String, String) => Future[UserDTO] = (id, email, password) => for (
    user <- userService.register(id, email, password)
  ) yield user

  def addPost: (String, String, String, String, Seq[String]) => Future[PostDTO] = (authorId, postId, title, body, labels) => for (
    user <- userService.registeredUser(authorId) ;
    post <- postService.publish(postId, title, body, labels, user.getEmail())
  ) yield post

  def publishPost: (String, String) => Future[PostDTO] = (authorId, postId) => for (
    user <- userService.registeredUser(authorId) ;
    post <- postService.publishDraft(postId)
  ) yield post
}

object Blog {
  def apply(userService: UserService, postService: PostService)(implicit ec: ExecutionContext): DefaultBlog = {
    new DefaultBlog(userService, postService)
  }
}
