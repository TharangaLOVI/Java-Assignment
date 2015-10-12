# Java-Assignment

##REST Service - Web Application
####Configurations
* Data Base Configuration
  * mvc-dispatcher-servlet - assignment_rest\src\main\webapp\WEB-INF\mvc-dispatcher-servlet.xml
    * ```
        <!-- Data Source -->
	      <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">

    		<property name="driverClassName" value="com.mysql.jdbc.Driver" />
    		<!-- local -->
    		<property name="url" value="jdbc:mysql://localhost:3306/assignment_db" />
    		<property name="username" value="root" />
    		<property name="password" value="" />
		
	      </bean>
	      <!-- Data Source End -->
      ```

  


