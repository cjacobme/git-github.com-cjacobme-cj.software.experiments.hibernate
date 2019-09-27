package cj.software.experiments.hibernate.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

@Entity
@SequenceGenerator(name = "MovieIdGenerator",
		sequenceName = "seq_movie",
		initialValue = 1,
		allocationSize = 1)
public class Movie
		implements
		Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "MovieIdGenerator")
	private Long id;

	@Version
	private int version;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String director;

	@OneToMany(orphanRemoval = true, fetch = FetchType.EAGER, mappedBy = "movie")
	private List<Role> roles = new ArrayList<>();

	@Column(name = "num_roles")
	private int numRoles = 0;

	private Movie()
	{

	}

	public Long getId()
	{
		return this.id;
	}

	public String getTitle()
	{
		return this.title;
	}

	public String getDirector()
	{
		return this.director;
	}

	public int getVersion()
	{
		return this.version;
	}

	public boolean addRole(Role pRole)
	{
		boolean lResult = this.roles.add(pRole);
		if (lResult)
		{
			this.numRoles++;
		}
		return lResult;
	}

	public int getNumRoles()
	{
		return this.numRoles;
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public static class Builder
	{
		protected Movie instance;

		protected Builder()
		{
			this.instance = new Movie();
		}

		public Builder withTitle(String pTitle)
		{
			this.instance.title = pTitle;
			return this;
		}

		public Builder withDirector(String pDirector)
		{
			this.instance.director = pDirector;
			return this;
		}

		public Movie build()
		{
			Movie lResult = this.instance;
			this.instance = null;
			return lResult;
		}
	}
}
