package net.rvk.reportgeneration.model

case class MasterData(id: String, userName:String, password: String, baseURL: String, loginPath: String, paymentsPath: String, testing: Boolean)
