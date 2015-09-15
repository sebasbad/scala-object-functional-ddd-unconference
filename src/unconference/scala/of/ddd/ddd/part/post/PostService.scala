package unconference.scala.of.ddd.ddd.part.post

import unconference.scala.of.ddd.ddd.types.NonEmptyString

import scala.concurrent.{ExecutionContext, Future}

/**
 * @author kevin 
 * @since 9/14/15.
 */
trait PostService {
  def publish: (String, String, String, Seq[String], String) => Future[PostDTO]
  def draft: (String, String, String, Seq[String], String) => Future[PostDTO]
  def publishDraft: (String) => Future[PostDTO]
}

case class DefaultPostService(postRepository: PostRepository)(implicit ec: ExecutionContext) extends PostService {
  implicit def wrap[T <: Post](post: T): Future[T] = Future.successful(post)

  def publish: (String, String, String, Seq[String], String) => Future[PostDTO] = (id, title, body, labels, author) => for (
    post <- Post(id, title, labels.map(NonEmptyString.apply), body, author) ;
    publishedPost <- post.publish() ;
    savedPost <- postRepository.save(publishedPost)
  ) yield savedPost.toDTO()

  def draft: (String, String, String, Seq[String], String) => Future[PostDTO] = (id, title, body, labels, author) => for (
    post <- Post(id, title, labels.map(NonEmptyString.apply), body, author) ;
    publishedPost <- post.draft() ;
    savedPost <- postRepository.save(publishedPost)
  ) yield savedPost.toDTO()

  def publishDraft: (String) => Future[PostDTO] = (id) => for (
    post <- postRepository.byId[DraftPost](id) ;
    publishedPost <- post.publish() ;
    savedPost <- postRepository.save(publishedPost)
  ) yield savedPost.toDTO()
}

object PostService {
  def apply(postRepository: PostRepository)(implicit ec: ExecutionContext = ExecutionContext.Implicits.global): PostService = {
    DefaultPostService(postRepository)
  }
}

object Scratch {
  implicit object SingleThreadExecutionContext extends ExecutionContext {
    def execute(runnable: Runnable) = runnable.run()
    def reportFailure(cause: Throwable) = throw cause
  }

  def main(args: Array[String]) {
    val service = PostService(PostRepository())

    val result = for (
      draft <- service.draft("id", "title", "body", Seq("labels"), "author") ;
      published <- service.publishDraft("id")
    ) yield published

    result.foreach(println)
  }
}
