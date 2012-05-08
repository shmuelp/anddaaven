package com.saraandshmuel.anddaaven;

public interface TefillaModelInterface {

	public abstract String getDateString();

	public abstract String getOmerString();

	public abstract boolean inAfternoon();

	public abstract void advanceDay();

}