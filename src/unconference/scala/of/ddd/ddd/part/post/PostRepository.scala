package unconference.scala.of.ddd.ddd.part.post

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

trait PostRepository {
  def save[T <: Post](post: T): Future[T]
  def byId[T <: Post](id: String)(implicit c: ClassTag[T]): Future[T]
}

class InMemoryPostRepository(private var posts: Seq[Post])(implicit ec: ExecutionContext) extends PostRepository {
  def save[T <: Post](post: T) = Future {
    this.posts = this.posts.filterNot(_.toDTO().getId() == post.toDTO().getId()) ++ Seq(post)
    post
  }

  def byId[T <: Post](id: String)(implicit c: ClassTag[T]) = {
    val post = this.posts.find(_.toDTO().getId() == id)
    post.filter(_.getClass == c.runtimeClass).fold[Future[T]](Future.failed(new NoSuchElementException()))(p => Future.successful(p.asInstanceOf[T]))
  }
}

object PostRepository {
  def apply()(implicit ec: ExecutionContext = ExecutionContext.Implicits.global): PostRepository = new InMemoryPostRepository(Seq())
}
