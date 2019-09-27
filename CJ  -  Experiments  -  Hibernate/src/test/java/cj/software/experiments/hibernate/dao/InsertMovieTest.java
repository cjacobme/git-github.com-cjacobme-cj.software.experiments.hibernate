package cj.software.experiments.hibernate.dao;

import static org.assertj.core.api.Assertions.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
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

	@Test
	public void insertMovie() throws SQLException
	{
		DataSource lDataSource = pg.getEmbeddedPostgres().getPostgresDatabase();
		SessionFactory lSessionFactory = HibernatUtil
				.createSessionFactory(lDataSource, Movie.class);
		try (Session lSession = lSessionFactory.openSession())
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
			}
			finally
			{
				TransactionStatus lStatus = lTransaction.getStatus();
				if (lStatus != TransactionStatus.COMMITTED)
				{
					this.logger.warn(String.format("tx rolling back: %s", lStatus));
					lTransaction.rollback();
				}
			}
		}
	}
}
