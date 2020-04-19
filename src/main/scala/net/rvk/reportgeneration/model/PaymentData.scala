package net.rvk.reportgeneration.model

case class AllPaymentsData(status: Int, data: List[BillingRecords], count: Int, dashboardCounts: DashboardCounts)
case class DashboardCounts(totalPayments: Int, todayPayments: Int, thisweekPayments: Int, thismonthPayments: Int)
case class PaymentDetails(id: Int, item: String, itemId:Option[Int], tax: Option[Int], cost: Int, qty: Int, total_amount: Int, service_type: String)
case class BillingRecords(id: Int,
                          customerUniqueCode: String,
                          firstName: String,
                          lastName: String,
                          customerName: String,
                          billingUnit: String,
                          contactNo: String,
                          dob: String,
                          age: String,
                          address: String,
                          email: String,
                          cityName:String,
                          stateName: String,
                          zipcode: String,
                          aadharNumber: String,
                          panNumber: String,
                          receiptNumber: String,
                          payment_date: String,
                          totalAmount: Int,
                          paymentDetails: List[PaymentDetails])

case class PaymentDataFlatFileStructure(id: Int,
                                        customerUniqueCode: String,
                                        firstName: String,
                                        lastName: String,
                                        customerName: String,
                                        billingUnit: String,
                                        contactNo: String,
                                        dob: String,
                                        age: String,
                                        address: String,
                                        email: String,
                                        cityName:String,
                                        stateName: String,
                                        zipcode: String,
                                        aadharNumber: String,
                                        panNumber: String,
                                        receiptNumber: String,
                                        paymentDate: String,
                                        totalAmount: Int,
                                        paymentDetailId: Int,
                                        item: String,
                                        itemId:Option[Int],
                                        tax: Option[Int],
                                        cost: Int,
                                        qty: Int,
                                        itemTotalAmount: Int,
                                        serviceType: String)
