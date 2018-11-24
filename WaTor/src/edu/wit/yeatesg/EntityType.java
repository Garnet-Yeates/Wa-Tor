package edu.wit.yeatesg;

enum EntityType
{	
	FISH("F"), SHARK("S"), SPACE(" ");

	protected String asString;
	
	private EntityType(String toString)
	{
		asString = toString;
	}
	
	public static EntityType fromString(String s)
	{
		for (EntityType e : values())
		{
			if (e.asString.equalsIgnoreCase(s.substring(0, 1)))
			{
				return e;
			}
		}
		return null;
	}	
}