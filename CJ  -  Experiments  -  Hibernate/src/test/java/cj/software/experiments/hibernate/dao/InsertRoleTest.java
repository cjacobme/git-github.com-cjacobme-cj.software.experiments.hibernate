package cj.software.experiments.hibernate.dao;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.opentable.db.postgres.junit.EmbeddedPostgresRules;
import com.opentable.db.postgres.junit.SingleInstancePostgresRule;

import cj.software.experiments.hibernate.model.Movie;
import cj.software.experiments.hibernate.model.Role;
import cj.software.experiments.hibernate.util.HibernatUtil;

public class InsertRoleTest
{
	private Logger logger = Logger.getLogger(InsertRoleTest.class);
	@ClassRule
	public static SingleInstancePostgresRule pg = EmbeddedPostgresRules.singleInstance();

	private static SessionFactory sessionFactory;

	@BeforeClass
	public static void setupSessionFactory()
	{
		DataSource lDataSource = pg.getEmbeddedPostgres().getPostgresDatabase();
		sessionFactory = HibernatUtil.createSessionFactory(lDataSource, Movie.class, Role.class);
	}

	@AfterClass
	public static void cleanUp()
	{
		if (sessionFactory != null)
		{
			sessionFactory.close();
		}
	}

	private void saveNativeMovie(Session pSession)
	{
		Transaction lTransaction = pSession.beginTransaction();
		try
		{
			NativeQuery<?> lQuery = pSession
					.createSQLQuery(
							"INSERT INTO Movie (id, version, title, director, num_roles) "
									+ "VALUES(-3, 3, 'Ein Fisch namens Wanda', 'keine Ahnung', 0)");
			lQuery.executeUpdate();
			lTransaction.commit();
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

	private Movie loadMovie(Session pSession)
	{
		CriteriaBuilder lCB = pSession.getCriteriaBuilder();
		CriteriaQuery<Movie> lCQ = lCB.createQuery(Movie.class);
		Root<Movie> lFrom = lCQ.from(Movie.class);
		lCQ = lCQ.select(lFrom).where(lCB.equal(lFrom.get("id"), -3));
		Query<Movie> lCreateQuery = pSession.createQuery(lCQ);
		Movie lMovie = lCreateQuery.getSingleResult();
		return lMovie;
	}

	private void saveRole(Session pSession, Role pRole)
	{
		Transaction lTransaction = pSession.beginTransaction();
		try
		{
			Movie lMovie = this.loadMovie(pSession);
			lMovie.addRole(pRole);
			pSession.save(lMovie);
			pSession.save(pRole);
			lTransaction.commit();
			this.logger
					.info(
							String
									.format(
											"Role %s saved with id %d version %d",
											pRole.getName(),
											pRole.getId(),
											pRole.getVersion()));
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

	@Test
	public void insertRolesSequentially()
	{
		try (Session pSession = sessionFactory.openSession())
		{
			this.saveNativeMovie(pSession);
			Movie lMovie = this.loadMovie(pSession);
			for (int bCounter = 1; bCounter <= 3; bCounter++)
			{
				Role lRole = Role
						.builder()
						.withMovie(lMovie)
						.withAppearanceTime(bCounter)
						.withName(String.format("Role #%d", bCounter))
						.build();

				this.saveRole(pSession, lRole);
			}

			Movie lLoadedAfterInsertions = this.loadMovie(pSession);
			int lVersion = lLoadedAfterInsertions.getVersion();
			this.logger.info(String.format("version is %d", lVersion));
			assertThat(lVersion).as("version").isEqualTo(6);
		}
	}
}
