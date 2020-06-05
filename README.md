# reportGenerationAutomation
The main purpose of this program is to solve one of the requirements which my Mom has to otherwise do manually. There is a custom CRM application used in my Mom's workplace. My Mom has to take the report on the payment details and show the monthly statistics. The company tool does not have the reporting capability. So it was a manual activity for my Mom to copy paste one record at a time and then do reporting.

This program written in Scala uses Akka Http server to accept the request. The user has to input the start date, end date for the report and also the path where the csv file should be downloaded. Path is Optional. I have used

1. mongoDb to store the credentials.  
2. Token for the service is cached using Scaffeine.  
3. Used Akka Http client for Http requests.  
4. Circe for serialising json to Case class.  
5. Kantan to encode case class data to CSV.  
6. Also, I had to make some manipulations to the incomming data as the API did not fully justify the definition. (Sometimes Int field was empty string thereby parsing issues)  
7. The frontend for this app is a simple react functional component using hooks  

This project is solely for personal custom requirement but the ideas can be reused.  
