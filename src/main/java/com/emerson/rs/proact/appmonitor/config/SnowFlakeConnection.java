package com.emerson.rs.proact.appmonitor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

@Configuration
@Service
public class SnowFlakeConnection {
	@Value("${snowflake.db.driver}")
	private String driver;
	@Value("${snowflake.db.user}")
	private String userName;
	@Value("${snowflake.db.account}")
	private String account;
	@Value("${snowflake.db.password}")
	private String password;
	@Value("${snowflake.db.warehouse}")
	private String warehouse;
	@Value("${snowflake.db.db}")
	private String db;
	@Value("${snowflake.db.schema}")
	private String schema;
	@Value("${snowflake.db.role}")
	private String role;
	@Value("${snowflake.db.url}")
	private String url;
	
	public Connection getConnection() throws Exception {
		Class.forName(driver);
		Properties properties = new Properties();
		properties.put("user", userName);
		properties.put("password", password);
		properties.put("account", account);
		properties.put("warehouse", warehouse);
		properties.put("db", db);
		properties.put("schema", schema);
		properties.put("role", role);

		//String url = "jdbc:snowflake://emersoncomres.east-us-2.azure.snowflakecomputing.com/";

		return DriverManager.getConnection(url, properties);
	}
}
