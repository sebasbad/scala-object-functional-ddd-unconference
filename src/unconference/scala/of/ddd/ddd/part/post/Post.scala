package unconference.scala.of.ddd.ddd.part.post

import java.util.Date

import unconference.scala.of.ddd.ddd.types.NonEmptyString

trait PostDTO {
  def getId: () => String
  def getTitle: () => String
  def getAuthor: () => String
  def getLabels: () => Seq[String]
  def getBody: () => String
  def getPublicationDate: () => Option[Date]
  def isDraft: () => Boolean
  def hasBeenPublished: () => Boolean
}

trait Post {
  def changeTitle: NonEmptyString => Post
  def addLabel: NonEmptyString => Post
  def toDTO: () => PostDTO
}

trait DefaultPostDTO extends Post with PostDTO {
  protected val id: NonEmptyString
  protected val title: NonEmptyString
  protected val labels: Seq[NonEmptyString]
  protected val body: NonEmptyString
  protected val author: NonEmptyString

  def getId = () => id
  def getTitle = () => title
  def getAuthor = () => author
  def getLabels = () => labels.map(_.toString)
  def getBody = () => body
  def getPublicationDate = () => None.asInstanceOf[Option[Date]]
  def isDraft = () => false
  def hasBeenPublished = () => false

  def toDTO = () => this
}

trait DraftAccessPost {
  def draft: () => DraftPost
}

trait PublishablePost {
  protected def toPublishedPost(id: NonEmptyString, title: NonEmptyString, labels: Seq[NonEmptyString],
                                body: NonEmptyString, author: NonEmptyString):
  () => PublishedPost = {
    () => PublishedPost(id, title, labels, body, author, new Date)
  }

  def publish: () => PublishedPost
}

case class VolatilePost(id: NonEmptyString, title: NonEmptyString, labels: Seq[NonEmptyString], body: NonEmptyString, author: NonEmptyString)
  extends DefaultPostDTO with PublishablePost with DraftAccessPost {

  def draft = () => DraftPost(id, title, labels, body, author)
  def changeTitle = (title) => copy(title = title)
  def addLabel = (label) => copy(labels = (labels ++ Seq(label)).distinct)
  def publish = toPublishedPost(id, title, labels, body, author)
}

case class DraftPost(id: NonEmptyString, title: NonEmptyString, labels: Seq[NonEmptyString], body: NonEmptyString, author: NonEmptyString)
  extends DefaultPostDTO with PublishablePost {

  override def isDraft = () => true
  def changeTitle = (title) => copy(title = title)
  def addLabel = (label) => copy(labels = (labels ++ Seq(label)).distinct)
  def publish = toPublishedPost(id, title, labels, body, author)
}

case class PublishedPost(id: NonEmptyString, title: NonEmptyString, labels: Seq[NonEmptyString], body: NonEmptyString, author: NonEmptyString, publicationDate: Date)
  extends DefaultPostDTO {
  override def getPublicationDate = () => Some(publicationDate)

  override def hasBeenPublished = () => true
  def changeTitle = (title) => copy(title = title)
  def addLabel = (label) => copy(labels = (labels ++ Seq(label)).distinct)
}

object Post {
  def apply(id: NonEmptyString, title: NonEmptyString, labels: Seq[NonEmptyString],
            body: NonEmptyString, author: NonEmptyString): VolatilePost = {
    VolatilePost(id, title, labels, body, author)
  }

  def apply(id: NonEmptyString, title: NonEmptyString, labels: Seq[NonEmptyString],
            body: NonEmptyString, author: NonEmptyString, publicationDate: Date): PublishedPost = {
    PublishedPost(id, title, labels, body, author, publicationDate)
  }
}
