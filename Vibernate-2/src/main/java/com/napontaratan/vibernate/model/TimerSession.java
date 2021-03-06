package com.napontaratan.vibernate.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * VibrateTimer model
 * @author Paul, Amelia
 */
public final class TimerSession extends Observable implements Serializable, Comparable<TimerSession>{
	private static final long serialVersionUID = 2881690379292284022L;
	private String name;
	private final Calendar startTime;
	private final Calendar endTime;
	private int id;
	private final boolean[] days;
	private final TimerSessionType type;
	private int color; // rgb value of color
	private boolean isActive;

	public enum TimerSessionType {
		VIBRATE, SILENT
	}

	// Constructor
	public TimerSession (String name, TimerSessionType type, Calendar startTime, Calendar endTime, boolean[] days, int color) {
		this.color = color;
		this.name = name;
		this.type = type;
		this.startTime = startTime;
		this.endTime = endTime;
		this.days = days;
		this.id = -1;
		this.isActive = true;
	}

	/**
	 * Sets the unique id for this timer object, if hasn't been set (which will be -1) and use this as key in DB
	 * This value should only be set once and not changed during it's lifetime
	 * Throws a runtime error if attempted to re-set id
	 * @param id
	 */
	public void setId(int id) {
		// http://stackoverflow.com/questions/14146182/how-to-create-a-variable-that-can-be-set-only-once-but-isnt-final-in-java?lq=1
		// this field is not made final since we only generate the id after it's creation
		if(this.id == -1) {
			this.id = id;
		} else {
			throw new RuntimeException("Timer for this Id has already been set");
		}
	}

	public int getId(){
		return id;
	}

	public boolean getActive() {
		return this.isActive;
	}

	public Calendar getEndTime() {
		return endTime;
	}

	public Calendar getStartTime() {
		return startTime;
	}

	/**
	 * @author daniel
	 * @return name of this timer
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @author daniel
	 * @return int representation of this timer's color in rgb
	 */
	public int getColor()
	{
		return this.color;
	}

	/**
	 * @author daniel
	 * @return the type of timer { SILENT, VIBRATE}
	 */
	public TimerSessionType getType()
	{
		return this.type;
	}

    /**
     * @author daniel
     * compare current timer to other timer by start time
     * @return a negative integer, zero, or a positive integer if this timer is less than, equal to, or greater than
     *          the other timer
     */
    @Override
    public int compareTo(TimerSession another) {
        return this.startTime.compareTo(another.getStartTime());
    }

	public boolean[] getDays() {
		return days;
	}

	public List<Calendar> getStartAlarmCalendars(){
		List<Calendar> calendars = new ArrayList<Calendar>();
		for (int i = 0; i < 7; i++) {
			if (days[i]) {
				Calendar day = Calendar.getInstance();
				day.set(Calendar.DAY_OF_WEEK, getDayOfWeekFromInt(i));
				day.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY));
				day.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE));
				day.set(Calendar.SECOND, startTime.get(Calendar.SECOND));
				calendars.add(day);
			}
		}
		return calendars;
	}

	public List<Calendar> getEndAlarmCalendars() {
		List<Calendar> calendars = new ArrayList<Calendar>();
		for (int i = 0; i < 7; i++) {
			if (days[i]) {
				Calendar day = Calendar.getInstance();
				day.set(Calendar.DAY_OF_WEEK, getDayOfWeekFromInt(i));
				day.set(Calendar.HOUR_OF_DAY, endTime.get(Calendar.HOUR_OF_DAY));
				day.set(Calendar.MINUTE, endTime.get(Calendar.MINUTE));
				day.set(Calendar.SECOND, endTime.get(Calendar.SECOND));
				calendars.add(day);
			}
		}
		return calendars;
	}

	private int getDayOfWeekFromInt(int day) {
		int dayOfWeek = 0;
		switch(day) {
		case 0:
			dayOfWeek = Calendar.SUNDAY;
			break;
		case 1:
			dayOfWeek = Calendar.MONDAY;
			break;
		case 2:
			dayOfWeek = Calendar.TUESDAY;
			break;
		case 3:
			dayOfWeek = Calendar.WEDNESDAY;
			break;
		case 4:
			dayOfWeek = Calendar.THURSDAY;
			break;
		case 5:
			dayOfWeek = Calendar.FRIDAY;
			break;
		case 6:
			dayOfWeek = Calendar.SATURDAY;
			break;
		}
		return dayOfWeek;
	}

    public void setName(String n) {
        name = n;
		notifyChange();
    }

    public void setColor(int c) {
        color = c;
		notifyChange();
    }

	public void setActive(boolean active) {
		this.isActive = active;
		notifyChange();
	}


	@Override
	public String toString() {  // for testing purposes
		String response = "VibrateTimer id: " + getId() + " startTime: " + getStartTime() + " endTime: " + getEndTime();
		return response;   
	}

	// ==== need the following to work with the database =====

	/**
	 * Convert a VibrateTimer object into an array of Bytes to be stored into the Database
	 * @param obj - (Object) VibrateTimer object
	 * @return byte[]
	 * @throws IOException
	 * @author Napon
	 */
	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
		return out.toByteArray();
	}
	/**
	 * Convert an array of Bytes back to its object form
	 * @param  -data
	 * @return (Object) VibrateTimer
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @author Napon
	 */
	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		return is.readObject();
	}

	@Override
	// IntelliJ Default Generated
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TimerSession that = (TimerSession) o;

		if (getId() != that.getId()) return false;
		if (getColor() != that.getColor()) return false;
		if (isActive != that.isActive) return false;
		if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
		if (getStartTime() != null ? !getStartTime().equals(that.getStartTime()) : that.getStartTime() != null)
			return false;
		if (getEndTime() != null ? !getEndTime().equals(that.getEndTime()) : that.getEndTime() != null) return false;
		if (!Arrays.equals(getDays(), that.getDays())) return false;
		return getType() == that.getType();

	}

	@Override
	// IntelliJ Default Generated
	public int hashCode() {
		int result = getName() != null ? getName().hashCode() : 0;
		result = 31 * result + (getStartTime() != null ? getStartTime().hashCode() : 0);
		result = 31 * result + (getEndTime() != null ? getEndTime().hashCode() : 0);
		result = 31 * result + getId();
		result = 31 * result + (getDays() != null ? Arrays.hashCode(getDays()) : 0);
		result = 31 * result + (getType() != null ? getType().hashCode() : 0);
		result = 31 * result + getColor();
		result = 31 * result + (isActive ? 1 : 0);
		return result;
	}

	private void notifyChange() {
		setChanged();
		notifyObservers();
	}
}