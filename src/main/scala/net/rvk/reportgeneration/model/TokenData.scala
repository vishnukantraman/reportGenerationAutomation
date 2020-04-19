package net.rvk.reportgeneration.model
import akka.http.scaladsl.model.DateTime

case class UserDetails(id: Int,
                       user_code: String,
                       first_name: String,
                       last_name: String,
                       user_name: String,
                       address: String,
                       city: String,
                       state: String,
                       country: String,
                       contact_no: String,
                       photo: String,
                       photo_path: String,
                       email: String,
                       aadhar_no: String,
                       pan_no: String,
                       date_of_joining: String,
                       qualification: String,
                       dol: String,
                       fte: String,
                       active_flg: Int,
                       last_login: String,
                       token: String,
                       created_by: Int,
                       created_date: String,
                       modified_by: Int,
                       modified_date: String,
                       delete_flg: Option[String])

case class TokenResponse(token: String, user: UserDetails)

case class TokenData(userName: String, token: String, creationDate: DateTime)
