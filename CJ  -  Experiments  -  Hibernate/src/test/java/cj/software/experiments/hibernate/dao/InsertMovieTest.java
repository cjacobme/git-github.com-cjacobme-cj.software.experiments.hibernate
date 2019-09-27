package cj.software.experiments.hibernate.dao;

import static org.assertj.core.api.Assertions.*;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.opentable.db.postgres.junit.EmbeddedPostgresRules;
import com.opentable.db.postgres.junit.SingleInstancePostgresRule;

import cj.software.experiments.hibernate.model.Movie;
import cj.software.experiments.hibernate.util.HibernatUtil;

public class InsertMovieTest
{
	private Logger logger = LogManager.getLogger(InsertMovieTest.class);

	@ClassRule
	public static SingleInstancePostgresRule pg = EmbeddedPostgresRules.singleInstance();

	private static SessionFactory sessionFactory;

	@BeforeClass
	public static void setupSessionFactory()
	{
		DataSource lDataSource = pg.getEmbeddedPostgres().getPostgresDatabase();
		sessionFactory = HibernatUtil.createSessionFactory(lDataSource, Movie.class);
	}

	@AfterClass
	public static void cleanUp()
	{
		if (sessionFactory != null)
		{
			sessionFactory.close();
		}
	}

	@Test
	public void insertMovie() throws SQLException
	{
		try (Session lSession = sessionFactory.openSession())
		{
			Transaction lTransaction = lSession.beginTransaction();
			try
			{
				Movie lMovie = Movie
						.builder()
						.withTitle("Apollo 13")
						.withDirector("John Hughes")
						.build();
				lSession.save(lMovie);
				lTransaction.commit();
				Long lId = lMovie.getId();
				assertThat(lId).isNotNull();
				int lVersion = lMovie.getVersion();
				this.logger.info(String.format("movie id = %d, version = %d", lId, lVersion));
			}
			finally
			{
				TransactionStatus lStatus = lTransaction.getStatus();
				if (lStatus != TransactionStatus.NOT_ACTIVE)
				{
					this.logger.warn(String.format("tx rolling back: %s", lStatus));
					lTransaction.rollback();
				}
			}
		}
	}
}
