package cj.software.experiments.hibernate.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

@Entity(name = "role_")
@SequenceGenerator(name = "RoleGenerator",
		sequenceName = "seq_role",
		initialValue = 1,
		allocationSize = 1)
public class Role
		implements
		Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "RoleGenerator")
	private Long id;

	@Version
	private int version;

	@Column(nullable = false, updatable = false)
	private String name;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	private Movie movie;

	private int appearanceTime;

	private Role()
	{
	}

	public Long getId()
	{
		return this.id;
	}

	public int getVersion()
	{
		return this.version;
	}

	public String getName()
	{
		return this.name;
	}

	public int getAppearanceTime()
	{
		return this.appearanceTime;
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public static class Builder
	{
		protected Role instance;

		protected Builder()
		{
			this.instance = new Role();
		}

		public Builder withMovie(Movie pMovie)
		{
			this.instance.movie = pMovie;
			return this;
		}

		public Builder withName(String pName)
		{
			this.instance.name = pName;
			return this;
		}

		public Builder withAppearanceTime(int pValue)
		{
			this.instance.appearanceTime = pValue;
			return this;
		}

		public Role build()
		{
			Role lResult = this.instance;
			this.instance = null;
			return lResult;
		}
	}
}
