package cj.software.experiments.hibernate.model;

public class MovieBuilder
		extends Movie.Builder
{
	public MovieBuilder()
	{
		super.withTitle("Apollo 13").withDirector("John Hughes");
	}
}
