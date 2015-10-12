# Java-Assignment

##REST Service - Web Application
####Configurations
* Data Base Configuration
  * mvc-dispatcher-servlet - assignment_rest\src\main\webapp\WEB-INF\mvc-dispatcher-servlet.xml
    * ```xml
    	.....
        <!-- Data Source -->
	      <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">

    		<property name="driverClassName" value="com.mysql.jdbc.Driver" />
    		<!-- local -->
    		<property name="url" value="jdbc:mysql://localhost:3306/<data_base>" />
    		<property name="username" value="<user_name>" />
    		<property name="password" value="<password>" />
		
	      </bean>
	      <!-- Data Source End -->
	      
	 
####REST Operations
*  List All Hotels - GET /hotel
  *  <http://localhost:8081/assignment_rest/hotel/>
*  Find By Id - GET /hotel/{id}
  *  <http://localhost:8081/assignment_rest/hotel/{id}>
*  Find By Hotel Name - GET /hotel/name/{name}
  *  <http://localhost:8081/assignment_rest/hotel/name/{name}>



  


