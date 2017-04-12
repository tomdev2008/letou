package com.lefu.letou.common;
 
import javax.sql.DataSource;


/**
 * @Description: MariaDB数据源配置
 * @see: MariaDBConfig 此处填写需要参考的类
 * @version 2015年6月9日 上午10:44:34
 * @author chi.chen
 */
public class MysqlConfig {
	/** 数据库驱动名称 */
	private String driverClassName;
	/** 数据库连接URL */
	private String url;
	/** 数据库连接用户名 */
	private String username;
	/** 数据库连接密码 */
	private String password;
	/** 数据库连接初始大小 */
	private int initialSize;
	/** 数据库连接最大活动大下 */
	private int maxActive;

	public DataSource dataSource() {
//		LefuPooledDataSource dataSource = new LefuPooledDataSource();
//		dataSource.setDataSourceName("");
		// ds_financial_persion_rw 生产的
//		dataSource.setDataSourceName("ds_financial_persion_rw");
//		return dataSource;

		return null;
		// DruidDataSource dataSource = new DruidDataSource();
		// dataSource.setDriverClassName(driverClassName);
		// dataSource.setUrl(url);
		// dataSource.setUsername(username);
		// dataSource.setPassword(password);
		// dataSource.setInitialSize(initialSize);
		// dataSource.setMaxActive(maxActive);
		// return dataSource;
	}

}
