package net.rvk.reportgeneration.codec

import kantan.csv.RowEncoder
import net.rvk.reportgeneration.model.{IncomingRequestParams, MasterData, PaymentDataFlatFileStructure}
import reactivemongo.api.bson.{BSONDocument, BSONDocumentReader}
import spray.json.DefaultJsonProtocol

object MasterDataImplicit {
  implicit object MasterDataReader extends BSONDocumentReader[MasterData] {
    override def readDocument(doc: BSONDocument) = for {
      id <- doc.getAsTry[String]("_id")
      userName <- doc.getAsTry[String]("userName")
      password <- doc.getAsTry[String]("password")
      baseURL <- doc.getAsTry[String]("baseURL")
      loginPath <- doc.getAsTry[String]("loginPath")
      paymentsPath <- doc.getAsTry[String]("paymentsPath")
    } yield MasterData(id, userName, password, baseURL, loginPath, paymentsPath)
  }
}

object imcomingRequestParams extends DefaultJsonProtocol {
  implicit val IncomingRequestParamsFormat = jsonFormat3(IncomingRequestParams)
}

object PaymentFlatFileStructureImplicit {
  implicit val PaymentDataFlatFileStructureRowEncode: RowEncoder[PaymentDataFlatFileStructure] = RowEncoder.encoder((x: PaymentDataFlatFileStructure) => s"${x.asInstanceOf[PaymentDataFlatFileStructure].id}|${x.asInstanceOf[PaymentDataFlatFileStructure].customerUniqueCode}|${x.asInstanceOf[PaymentDataFlatFileStructure].firstName}|${x.asInstanceOf[PaymentDataFlatFileStructure].lastName}|${x.asInstanceOf[PaymentDataFlatFileStructure].customerName}|${x.asInstanceOf[PaymentDataFlatFileStructure].billingUnit}|${x.asInstanceOf[PaymentDataFlatFileStructure].contactNo}|${x.asInstanceOf[PaymentDataFlatFileStructure].dob}|${x.asInstanceOf[PaymentDataFlatFileStructure].age}|${x.asInstanceOf[PaymentDataFlatFileStructure].address}|${x.asInstanceOf[PaymentDataFlatFileStructure].email}|${x.asInstanceOf[PaymentDataFlatFileStructure].cityName}|${x.asInstanceOf[PaymentDataFlatFileStructure].stateName}|${x.asInstanceOf[PaymentDataFlatFileStructure].zipcode}|${x.asInstanceOf[PaymentDataFlatFileStructure].aadharNumber}|${x.asInstanceOf[PaymentDataFlatFileStructure].panNumber}|${x.asInstanceOf[PaymentDataFlatFileStructure].receiptNumber}|${x.asInstanceOf[PaymentDataFlatFileStructure].paymentDate}|${x.asInstanceOf[PaymentDataFlatFileStructure].totalAmount}|${x.asInstanceOf[PaymentDataFlatFileStructure].notes}|${x.asInstanceOf[PaymentDataFlatFileStructure].paymentDetailId}|${x.asInstanceOf[PaymentDataFlatFileStructure].item}|${x.asInstanceOf[PaymentDataFlatFileStructure].itemId.getOrElse("")}|${x.asInstanceOf[PaymentDataFlatFileStructure].tax.getOrElse("")}|${x.asInstanceOf[PaymentDataFlatFileStructure].cost}|${x.asInstanceOf[PaymentDataFlatFileStructure].qty}|${x.asInstanceOf[PaymentDataFlatFileStructure].itemTotalAmount}|${x.asInstanceOf[PaymentDataFlatFileStructure].serviceType}")
}