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
}

trait Post {
  def changeTitle: NonEmptyString => Post
  def addLabel: NonEmptyString => Post
  def toDTO: () => PostDTO
}

trait DefaultPostDTO extends Post with PostDTO {
  protected val id: NonEmptyString
  protected val title: NonEmptyString
  protected val labels: Seq[String]
  protected val body: NonEmptyString
  protected val author: NonEmptyString

  def getId = () => id
  def getTitle = () => title
  def getAuthor = () => author
  def getLabels = () => labels
  def getBody = () => body
  def getPublicationDate = () => None.asInstanceOf[Option[Date]]
  def isDraft = () => false

  def toDTO = () => this
}

trait DraftAccessPost {
  def draft: () => DraftPost
}

trait PublishablePost {
  def publish: () => PublishedPost
}

case class VolatilePost(id: NonEmptyString, title: NonEmptyString, labels: Seq[String], body: NonEmptyString, author: NonEmptyString)
  extends DefaultPostDTO with PublishablePost with DraftAccessPost {

  def draft = () => DraftPost(id, title, labels, body, author)
  def changeTitle = (title) => copy(title = title)
  def addLabel = (label) => copy(labels = (labels ++ Seq(label.toString)).distinct)
  def publish = () => PublishedPost(id, title, labels, body, author, new Date)
}

case class DraftPost(id: NonEmptyString, title: NonEmptyString, labels: Seq[String], body: NonEmptyString, author: NonEmptyString)
  extends DefaultPostDTO with PublishablePost {

  override def isDraft = () => true
  def changeTitle = (title) => copy(title = title)
  def addLabel = (label) => copy(labels = (labels ++ Seq(label.toString)).distinct)
  def publish = () => PublishedPost(id, title, labels, body, author, new Date)
}

case class PublishedPost(id: NonEmptyString, title: NonEmptyString, labels: Seq[String], body: NonEmptyString, author: NonEmptyString, publicationDate: Date)
  extends DefaultPostDTO {
  override def getPublicationDate = () => Some(publicationDate)

  def changeTitle = (title) => copy(title = title)
  def addLabel = (label) => copy(labels = (labels ++ Seq(label.toString)).distinct)
}

object Post {
  def apply(id: NonEmptyString, title: NonEmptyString, labels: Seq[String], body: NonEmptyString, author: NonEmptyString): VolatilePost = {
    VolatilePost(id, title, labels, body, author)
  }

  def apply(id: NonEmptyString, title: NonEmptyString, labels: Seq[String], body: NonEmptyString, author: NonEmptyString, publicationDate: Date): PublishedPost = {
    PublishedPost(id, title, labels, body, author, publicationDate)
  }
}