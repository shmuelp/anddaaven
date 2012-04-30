package com.saraandshmuel.anddaaven;

import android.text.format.Time;

public class TefillaModel implements TefillaModelInterface {
	
	public TefillaModel() {
		time = new Time();
		time.setToNow();
		hebrewDate = new HebrewDate(time);
	}

	/* (non-Javadoc)
	 * @see com.saraandshmuel.anddaaven.TefillaModelInterface#getDateString()
	 */
	public String getDateString() {
		return hebrewDate.toString();
	}

	/* (non-Javadoc)
	 * @see com.saraandshmuel.anddaaven.TefillaModelInterface#getOmerString()
	 */
	public String getOmerString() {
		int omerNum = hebrewDate.getOmerNumber();
		int omerWeek = omerNum / 7;
		int omerDay = omerNum % 7;
		
		StringBuilder sb = new StringBuilder();
		sb.append("Today is the ").append(omerNum);
		sb.append(" day of the omer, which is ").append(omerWeek);
		sb.append(" weeks and ").append(omerDay).append(" days of the omer");
		
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see com.saraandshmuel.anddaaven.TefillaModelInterface#inAfternoon()
	 */
	public boolean inAfternoon() {
		if ( time.hour > 12 )
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see com.saraandshmuel.anddaaven.TefillaModelInterface#advanceDay()
	 */
	public void advanceDay() {
		++time.monthDay;
		time.normalize(false);
		hebrewDate = new HebrewDate(time);
	}
	
	Time time;
	HebrewDate hebrewDate;
}
