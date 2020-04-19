package net.rvk.reportgeneration.services

import java.io.File
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Accept, Authorization, OAuth2BearerToken}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.github.blemale.scaffeine.{AsyncLoadingCache, Scaffeine}
import com.typesafe.config.ConfigFactory
import io.circe.generic.auto._
import io.circe.jawn.decode
import kantan.csv._
import kantan.csv.ops._
import net.rvk.reportgeneration.model.{AllPaymentsData, IncomingRequestParams, MasterData, PaymentDataFlatFileStructure, TokenResponse}
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.{AsyncDriver, DefaultDB, MongoConnection}
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object AkkaHttp {
  def main(args: Array[String]): Unit = {

    class UnauthorizedException(message:String) extends RuntimeException(message)
    class MissingFormData(message:String) extends RuntimeException(message)
    class ServerSideError(message: String) extends RuntimeException(message)
    class GenericError(message: String) extends RuntimeException(message)

    import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
    import net.rvk.reportgeneration.codec.MasterDataImplicit._
    import net.rvk.reportgeneration.codec.PaymentFlatFileStructureImplicit._
    import net.rvk.reportgeneration.codec.imcomingRequestParams._

    implicit val system: ActorSystem = ActorSystem("my-system")
    implicit val ec: ExecutionContextExecutor = system.dispatcher
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val config = ConfigFactory.load()
    val dbname = config.getString("rr.project.dbname")
    val driver = AsyncDriver()
    val mongoUri = s"mongodb://localhost:27017/db"
    val parsedURI = Future.fromTry(MongoConnection.parseURI(mongoUri))
    val futureConnection = parsedURI.flatMap(driver.connect(_))
    def dbConnection: Future[DefaultDB] = futureConnection.flatMap(_.database(dbname))
    def masterDataCollection:Future[BSONCollection] = dbConnection.map(_.collection("masterData"))
    val http = Http(system)
    lazy val defaultDownloadPath: String = System.getProperty("user.home") + s"\\${config.getString("rr.project.defaultDownloadFolder")}\\"

    def fetchData(data: MasterData, startDate: String, endDate: String) = {
      //Fetch token using
      for {
        HttpResponse(status, _, entity, _) <-  tokenRequest(data)
        _ <- checkStatus(status, entity)
        tokenResponse <- loadEntity(entity)
        token <- parseToken(tokenResponse)
        HttpResponse(status, _, entity, _) <- fetchPaymentDetails(token, data, startDate, endDate)
        _ <- checkStatus(status, entity)
        data <- loadEntity(entity)
        paymentData <- parsePaymentData(data.replaceAll("\"itemId\":\"\"", "\"itemId\":null")) //This has to be done because the API is inconsistent. The itemId is defined as Int but returns empty string instead of 0 for empty.
      } yield convertDeepStructureToFlatStructure(paymentData)
    }

    def convertDeepStructureToFlatStructure(billingDetails: AllPaymentsData)= {
      billingDetails.data.flatMap(x => x.paymentDetails.map(y => PaymentDataFlatFileStructure(x.id, x.customerUniqueCode, x.firstName, x.lastName, x.customerName, x.billingUnit, x.contactNo, x.dob.toString, x.age.toString, x.address.filter(_ >= ' ').toString, x.email.toString, x.cityName.toString, x.stateName.toString, x.zipcode.toString, x.aadharNumber.toString, x.panNumber.toString, x.receiptNumber.toString, x.payment_date.toString, x.totalAmount, y.id, y.item, y.itemId, y.tax, y.cost, y.qty, y.total_amount, y.service_type.toString)))
    }

    def parsePaymentData(str: String) = {
      decode[AllPaymentsData](str) match {
        case Right(value) => Future.successful(value)
        case Left(error) => Future.failed(new RuntimeException(error))
      }
    }

    def fetchPaymentDetails(token : String, data: MasterData, startDate: String, endDate: String) : Future[HttpResponse] = {
      val payload =
        s"""
           |{
           |	"searchKeyword":"",
           |	"fromDate":"$startDate",
           |	"toDate":"$endDate",
           |	"receiptNo":"",
           |	"mobileNumber":"",
           |	"limit":9999,
           |	"offset":null,
           |	"getPaymentByPeriod":"",
           |	"sortWith":"",
           |	"sortBy":""
           |}
           |""".stripMargin.toString

      val paymentsRequest = HttpRequest(HttpMethods.POST,
        uri = s"${data.baseURL}${data.paymentsPath}",
        entity = HttpEntity(ContentTypes.`application/json`, payload),
        headers = List(Accept(MediaTypes.`application/json`), Authorization(OAuth2BearerToken(token))))
      http.singleRequest(paymentsRequest)
    }

    def parseToken(str: String) = {
      decode[TokenResponse](str) match {
        case Right(value) => Future.successful(value.token)
        case Left(error) =>Future.failed(new RuntimeException(error))
      }
    }

    def checkStatus(status: StatusCode, entity: ResponseEntity) ={
      if (status == StatusCodes.OK)
        Future.successful()
      else{
        val body = loadEntity(entity)
        status match{
          case StatusCodes.Forbidden => body.flatMap(_ => Future.failed{
            new UnauthorizedException(s"$status -> access denied")
          })
          case StatusCodes.InternalServerError => body.flatMap(msg => Future.failed{
            new ServerSideError(s"$status -> $msg")
          })
          case _ => body.flatMap(msg => Future.failed{
            new GenericError(s"$status -> $msg")
          })
        }
      }
    }

    def loadEntity(entity: ResponseEntity) =
      for {
        strict <- entity.toStrict(10.seconds)
        bytes <- strict.dataBytes.runFold(ByteString.empty)(_ ++ _)
      } yield bytes.decodeString("UTF-8")

    def tokenRequest(data: MasterData) = {
      val cache:AsyncLoadingCache[HttpRequest, HttpResponse] =
        Scaffeine()
          .expireAfterWrite(1.hour)
          .maximumSize(10)
          .buildAsyncFuture(tokenRequest => http.singleRequest(tokenRequest))

      val payload =
        s"""
           |{
           |"username": "${data.userName}",
           |"password": "${data.password}",
           |"webOrMobile" : "1"
           |}
           |""".stripMargin.toString
      val tokenRequest: HttpRequest = HttpRequest(HttpMethods.POST,
        uri = s"${data.baseURL}${data.loginPath}",
        headers = List(Accept(MediaTypes.`application/json`)) ,
        entity = HttpEntity(ContentTypes.`application/json`,payload))
      cache.get(tokenRequest)
    }

    def generateCSV(value: List[PaymentDataFlatFileStructure], startDate: String, endDate: String, path: Option[String]) = {
      val assignedPath = path match {
        case Some(value) => if (new java.io.File(value).exists && value.endsWith("\\")) value else defaultDownloadPath
        case None => defaultDownloadPath
      }
      new File(s"${assignedPath}PaymentReport_${startDate}_to_${endDate}.csv").writeCsv(value,rfc.withHeader("id|customerUniqueCode|firstName|lastName|customerName|billingUnit|contactNo|dob|age|address|email|cityName|stateName|zipcode|aadharNumber|panNumber|receiptNumber|paymentDate|totalAmount|paymentDetailId|item|itemId|tax|cost|qty|itemTotalAmount|serviceType"))
      println(s"File created at location: $assignedPath")
    }

    val route: Route =
      path("getPaymentDetails") {
        concat(
          get {
            parameters("startDate".as[String], "endDate".as[String]) { (startDate, endDate) =>

              // Get mongodb connection
              for {
                data: Option[MasterData] <- masterDataCollection.flatMap(_.find(BSONDocument("_id" -> "RRP")).one[MasterData])
              } yield data match {
                case Some(value) => fetchData(value, startDate, endDate).onComplete{
                  case Success(paymentData) => generateCSV(paymentData, startDate, endDate, None)
                  case Failure(exception) => println(exception)
                }
                case None => println("No master data")
              }
              complete(s"The parameters are $startDate and $endDate")
            }
          },
          post {
            entity(as[IncomingRequestParams]) { message =>
              if (message == null) complete(HttpResponse(StatusCodes.NotFound)) else {
                for {
                  data: Option[MasterData] <- masterDataCollection.flatMap(_.find(BSONDocument("_id" -> "RRP")).one[MasterData])
                } yield data match {
                  case Some(value) => fetchData(value, message.startDate, message.endDate).onComplete{
                    case Success(paymentData) => generateCSV(paymentData, message.startDate, message.endDate, message.path)
                    case Failure(exception) => println(exception)
                  }
                  case None => println("No master data")
                }
                complete(s"The parameters are ${message.startDate} and ${message.endDate}")
              }
            }
          })
      }

    Http().bindAndHandle(route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/")
  }
}
