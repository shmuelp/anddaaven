package com.saraandshmuel.anddaaven;

import android.text.format.Time;
//import android.util.Log;

// From http://emr.cs.uiuc.edu/~reingold/calendar.C
// Retrieved 2010-02-07

// The following C++ code is translated from the Lisp code
// in ``Calendrical Calculations'' by Nachum Dershowitz and
// Edward M. Reingold, Software---Practice & Experience,
// vol. 20, no. 9 (September, 1990), pp. 899--928.

// This code is in the public domain, but any use of it
// should publically acknowledge its source.

// Classes GregorianDate, JulianDate, IsoDate, IslamicDate,
// and HebrewDate

// Absolute dates

// "Absolute date" means the number of days elapsed since the Gregorian date
// Sunday, December 31, 1 BC. (Since there was no year 0, the year following
// 1 BC is 1 AD.) Thus the Gregorian date January 1, 1 AD is absolute date
// number 1.

// Hebrew dates

class HebrewDate {
	private final static int HebrewEpoch = -1373429; // Absolute date of start of Hebrew calendar

	public static boolean HebrewLeapYear(int year) {
		// True if year is an Hebrew leap year
		  
		  if ((((7 * year) + 1) % 19) < 7)
		    return true;
		  else
		    return false;
		}

	public boolean HebrewLeapYear() {
		// True if year is an Hebrew leap year
		  
		  if ((((7 * year) + 1) % 19) < 7)
		    return true;
		  else
		    return false;
		}

	public static int LastMonthOfHebrewYear(int year) {
	// Last month of Hebrew year.
	  
	  if (HebrewLeapYear(year))
	    return 13;
	  else
	    return 12;
	}

	public static int HebrewCalendarElapsedDays(int year) {
	// Number of days elapsed from the Sunday prior to the start of the
	// Hebrew calendar to the mean conjunction of Tishri of Hebrew year.
	  
	  int MonthsElapsed =
	    (235 * ((year - 1) / 19))           // Months in complete cycles so far.
	    + (12 * ((year - 1) % 19))          // Regular months in this cycle.
	    + (7 * ((year - 1) % 19) + 1) / 19; // Leap months this cycle
	  int PartsElapsed = 204 + 793 * (MonthsElapsed % 1080);
	  int HoursElapsed =
	    5 + 12 * MonthsElapsed + 793 * (MonthsElapsed  / 1080)
	    + PartsElapsed / 1080;
	  int ConjunctionDay = 1 + 29 * MonthsElapsed + HoursElapsed / 24;
	  int ConjunctionParts = 1080 * (HoursElapsed % 24) + PartsElapsed % 1080;
	  int AlternativeDay;
	  if ((ConjunctionParts >= 19440)        // If new moon is at or after midday,
	      || (((ConjunctionDay % 7) == 2)    // ...or is on a Tuesday...
	          && (ConjunctionParts >= 9924)  // at 9 hours, 204 parts or later...
	          && !(HebrewLeapYear(year)))   // ...of a common year,
	      || (((ConjunctionDay % 7) == 1)    // ...or is on a Monday at...
	          && (ConjunctionParts >= 16789) // 15 hours, 589 parts or later...
	          && (HebrewLeapYear(year - 1))))// at the end of a leap year
	    // Then postpone Rosh HaShanah one day
	    AlternativeDay = ConjunctionDay + 1;
	  else
	    AlternativeDay = ConjunctionDay;
	  if (((AlternativeDay % 7) == 0)// If Rosh HaShanah would occur on Sunday,
	      || ((AlternativeDay % 7) == 3)     // or Wednesday,
	      || ((AlternativeDay % 7) == 5))    // or Friday
	    // Then postpone it one (more) day
	    return (1+ AlternativeDay);
	  else
	    return AlternativeDay;
	}

	public static int DaysInHebrewYear(int year) {
	// Number of days in Hebrew year.
	  
	  return ((HebrewCalendarElapsedDays(year + 1)) -
	          (HebrewCalendarElapsedDays(year)));
	}

	public boolean LongHeshvan(int year) {
	// True if Heshvan is long in Hebrew year.
	  
	  if ((DaysInHebrewYear(year) % 10) == 5)
	    return true;
	  else
	    return false;
	}

	boolean ShortKislev(int year) {
	// True if Kislev is short in Hebrew year.
	  
	  if ((DaysInHebrewYear(year) % 10) == 3)
	    return true;
	  else
	    return false;
	}

	int LastDayOfHebrewMonth(int month, int year) {
	// Last day of month in Hebrew year.
	  
	  if ((month == 2)
	      || (month == 4)
	      || (month == 6)
	      || ((month == 8) && !(LongHeshvan(year)))
	      || ((month == 9) && ShortKislev(year))
	      || (month == 10)
	      || ((month == 12) && !(HebrewLeapYear(year)))
	      || (month == 13))
	    return 29;
	  else
	    return 30;
	}

	
  private int year;   // 1...
  private int month;  // 1..LastMonthOfHebrewYear(year)
  private int day;    // 1..LastDayOfHebrewMonth(month, year)
  

  public HebrewDate(int m, int d, int y) { 
	  month = m; day = d; year = y; 
  }
  
  public HebrewDate(int d) {
	  setFromAbsoluteDate(d);
  }
  //Computes the Hebrew date from the absolute date.
  private void setFromAbsoluteDate( int d ) {
	    year = (d + HebrewEpoch) / 366; // Approximation from below.
	    // Search forward for year from the approximation.
	    while (d >= new HebrewDate(7,1,year + 1).getAbsoluteDate() )
	      year++;
	    // Search forward for month from either Tishri or Nisan.
	    if (d < new HebrewDate(1, 1, year).getAbsoluteDate())
	      month = 7;  //  Start at Tishri
	    else
	      month = 1;  //  Start at Nisan
	    while (d > new HebrewDate(month, (LastDayOfHebrewMonth(month,year)), year).getAbsoluteDate())
	      month++;
	    // Calculate the day by subtraction.
	    day = d - new HebrewDate(month, 1, year).getAbsoluteDate() + 1;
  }
  
  public HebrewDate( Time t ) {
	  System.setProperty("log.tag.HebrewDate", "VERBOSE");
	  int d=GetAbsoluteDateGregorian(t);
	  setFromAbsoluteDate(d);
  }
  
  public HebrewDate() {
	  System.setProperty("log.tag.HebrewDate", "VERBOSE");
	  Time t = new Time();
	  t.setToNow();
	  int d=GetAbsoluteDateGregorian(t);
	  setFromAbsoluteDate(d);
  }
  
public int getAbsoluteDate() { // Computes the absolute date of Hebrew date.
    int DayInYear = day; // Days so far this month.
    if (month < 7) { // Before Tishri, so add days in prior months
                     // this year before and after Nisan.
      int m = 7;
      while (m <= (LastMonthOfHebrewYear(year))) {
        DayInYear = DayInYear + LastDayOfHebrewMonth(m, year);
        m++;
      };
      m = 1;
      while (m < month) {
        DayInYear = DayInYear + LastDayOfHebrewMonth(m, year);
        m++;
      }
    }
    else { // Add days in prior months this year
      int m = 7;
      while (m < month) {
        DayInYear = DayInYear + LastDayOfHebrewMonth(m, year);
        m++;
      }
    }
    return (DayInYear +
            (HebrewCalendarElapsedDays(year)// Days in prior years.
             + HebrewEpoch));         // Days elapsed before absolute date 1.
  }
  
  public int GetMonth() { return month; }
  public int GetDay() { return day; }
  public int GetYear() { return year; }

  @Override
  public String toString() {
	String result = new String();
	result += "" + GetDay() + ' ' + MonthNames[month] + ' ' + GetYear();
	
	// TODO: refactor into a separate class soon
	if ( ( month == 1 && day > 15 ) || 
		 month == 2 || 
		 ( month == 3 && day < 6 ) ) {
		int omerday = getOmerNumber();
		result += "- Day " + omerday + " of the Omer";
	}
	
	return result;
  }

	public int getOmerNumber() {
		return getAbsoluteDate() - (new HebrewDate(1, 15, year)).getAbsoluteDate();
	}
  
  final static String MonthNames[] = { "", "Nissan", "Iyar", "Sivan", "Tamuz", 
	  								   "Av", "Elul", "Tishrei", "Cheshvan",
	  								   "Kislev", "Teves", "Shvat", "Adar", 
	  								   "Adar Sheini"};
  
  private static int GetAbsoluteDateGregorian( Time t ) {
	  int result = GetAbsoluteDateGregorian(t.month+1, t.monthDay, t.year);
//	  Log.d("HebrewDate", "GetAbsoluteDateGregorian(" + t + '=' + t.year + '-' + t.month + '-' + t.monthDay + ")=" + result);
	  return result;
  }
  
  private static int GetAbsoluteDateGregorian( int month, int day, int year ) {
//	  Log.d("HebrewDate", "GetAbsoluteDateGregorian(" + month + ',' + day + ',' + year + ')');
	  
	  int N = day;           // days this month
	  for (int m = month - 1;  m > 0; m--) // days in prior months this year
	  {
//		  Log.v("HebrewDate", "GetAbsoluteDateGregorian(): m=" + m + ", N=" + N);
		  N = N + LastDayOfGregorianMonth(m, year);
	  }
//	  Log.v("HebrewDate", "GetAbsoluteDateGregorian(): N=" + N);
	  return
	      (N                    // days this year
	       + 365 * (year - 1)   // days in previous years ignoring leap days
	       + (year - 1)/4       // Julian leap days before this year...
	       - (year - 1)/100     // ...minus prior century years...
	       + (year - 1)/400);   // ...plus prior years divisible by 400
  }

  private static int LastDayOfGregorianMonth(int month, int year) {
	// Compute the last date of the month for the Gregorian calendar.
	  
	  switch (month) {
	  case 2:
	    if ((((year % 4) == 0) && ((year % 100) != 0))
	        || ((year % 400) == 0))
	      return 29;
	    else
	      return 28;
	  case 4:
	  case 6:
	  case 9:
	  case 11: return 30;
	  default: return 31;
	  }
	}
};

