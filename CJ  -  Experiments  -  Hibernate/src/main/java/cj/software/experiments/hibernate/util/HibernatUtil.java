package cj.software.experiments.hibernate.util;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

public class HibernatUtil
{
	public static SessionFactory createSessionFactory(DataSource pDataSource, Class<?>... pClasses)
	{
		Properties lSettings = new Properties();
		lSettings.put(Environment.DATASOURCE, pDataSource);
		lSettings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQL82Dialect");
		lSettings.put(Environment.SHOW_SQL, "true");
		lSettings.put(Environment.FORMAT_SQL, "false");
		lSettings.put(Environment.HBM2DDL_AUTO, "create-drop");

		Configuration lConfiguration = new Configuration();
		lConfiguration.setProperties(lSettings);
		for (Class<?> bClass : pClasses)
		{
			lConfiguration.addAnnotatedClass(bClass);
		}

		StandardServiceRegistryBuilder lRegistryBuilder = new StandardServiceRegistryBuilder()
				.applySettings(lConfiguration.getProperties());
		ServiceRegistry lRegistry = lRegistryBuilder.build();
		SessionFactory lResult = lConfiguration.buildSessionFactory(lRegistry);
		return lResult;
	}
}
