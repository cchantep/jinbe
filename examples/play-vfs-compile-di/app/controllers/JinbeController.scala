package io.github.cchantep.jinbe.demo.controllers

import scala.concurrent.{ ExecutionContext, Future }

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.FileIO
import akka.util.ByteString

import play.api.libs.json.Json
import play.api.libs.ws.DefaultBodyWritables._
import play.api.mvc.{ BaseController, ControllerComponents }

import io.github.cchantep.jinbe.ObjectStorage
import io.github.cchantep.jinbe.exception.{
  BucketAlreadyExistsException,
  ObjectNotFoundException
}

class JinbeController(
    val controllerComponents: ControllerComponents,
    jinbe: ObjectStorage
  )(implicit
    ec: ExecutionContext,
    mat: Materializer)
    extends BaseController {

  def index = Action {
    Ok(views.html.api.root()).as("text/html")
  }

  def listBuckets = Action.async {
    jinbe.buckets.collect[List]().map { buckets =>
      Ok(Json.toJson(buckets.map(_.name)))
    }
  }

  def createBucket(bucketName: String) = Action.async {
    jinbe
      .bucket(bucketName)
      .create(failsIfExists = true)
      .map { _ => Created(s"$bucketName created") }
      .recover {
        case BucketAlreadyExistsException(_) =>
          Ok(s"$bucketName already exists")
      }
  }

  def deleteBucket(bucketName: String) = Action.async { request =>
    jinbe.bucket(bucketName).delete.ignoreIfNotExists.apply().map { _ =>
      NoContent
    }
  }

  def listObjects(bucketName: String) = Action.async {
    jinbe.bucket(bucketName).objects.collect[List]().map { objects =>
      Ok(Json.toJson(objects.map(_.name)))
    }
  }

  def getObject(bucketName: String, objectName: String) = Action {
    val data = jinbe.bucket(bucketName).obj(objectName).get()
    Ok.chunked(data)
  }

  def objectMetadata(bucketName: String, objectName: String) = Action.async {
    val objectRef = jinbe.bucket(bucketName).obj(objectName)

    objectRef.metadata.map { meta =>
      NoContent.withHeaders(meta.toSeq.flatMap {
        case (name, values) => values.map(name -> _)
      }: _*)
    }.recover { case _: ObjectNotFoundException => NotFound }
  }

  def createObject(
      bucketName: String
    ) = Action.async(parse.multipartFormData) { request =>
    val files = request.body.files.map { file =>
      val source = FileIO.fromPath(file.ref.path)
      val uploaded: Future[NotUsed] = source runWith jinbe
        .bucket(bucketName)
        .obj(file.filename)
        .put[ByteString]
      uploaded
    }
    if (files.isEmpty) Future.successful(BadRequest("No files to upload"))
    else
      Future.sequence(files).map { _ =>
        Ok(s"File ${request.body.files.map(_.filename).mkString(",")} uploaded")
      }
  }

  def deleteObject(bucketName: String, objectName: String) = Action.async {
    jinbe
      .bucket(bucketName)
      .obj(objectName)
      .delete
      .ignoreIfNotExists
      .apply()
      .map { _ => NoContent }
  }
}
