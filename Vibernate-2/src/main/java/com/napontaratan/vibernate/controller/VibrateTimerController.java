package com.napontaratan.vibernate.controller;

import java.util.Calendar;
import java.util.List;

import com.napontaratan.vibernate.database.VibernateDB;
import com.napontaratan.vibernate.model.TimerSession;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public final class VibrateTimerController {

	private static final int WEEK_MILLISECONDS = 604800000;
	private VibernateDB datastore;
	private AlarmManager am; 
	private Context parent;

	public VibrateTimerController(Context context){
		am = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		datastore = VibernateDB.getInstance(context);
		parent = context;
	}

	/**
	 * Start a repeating vibrate service at the start time and
	 * a repeating ringtone service at the end time 
	 * @param vt is the vibrateAlarm object to create a timer for
	 * @author Napon, Sunny
	 */
	public void setAlarm(TimerSession vt){
		int timerId = vt.getId();
		if(!datastore.contains(timerId))
			datastore.addToDB(vt);
		List<Calendar> startTimes = vt.getStartAlarmCalendars();
		List<Calendar> endTimes = vt.getEndAlarmCalendars();
		// Timer on
		Intent activateVibration = null;
		if(vt.getType() == TimerSession.TimerSessionType.VIBRATE) {
			activateVibration = new Intent(parent, VibrateOnBroadcastReceiver.class);
		} else if(vt.getType() == TimerSession.TimerSessionType.SILENT) {
			activateVibration = new Intent(parent, SilentOnBroadcastReceiver.class);
		}
		for (Calendar startTime : startTimes) {
			int id = timerId + startTime.get(Calendar.DAY_OF_WEEK);
			createSystemTimer(startTime.getTimeInMillis(), id, activateVibration);
		}
		// Timer off
		Intent disableVibration = new Intent(parent, OffBroadcastReceiver.class);
		for(Calendar endTime : endTimes){
			int id = timerId + endTime.get(Calendar.DAY_OF_WEEK) + 10;
			createSystemTimer(endTime.getTimeInMillis(), id, disableVibration);
		}
	}

	/**
	 * Remove the VibrateTimer object from db and cancel the services
	 * @param vt VibrateTimer object to cancel
	 * @author Napon, Sunny
	 */
	public void removeAlarm(TimerSession vt){
		datastore.remove(vt);
		cancelAlarm(vt);
	}

	/**
	 * Cancel the services corresponding to the VibrateTimer object
	 */
	public void cancelAlarm(TimerSession vt) {
		List<Calendar> times = vt.getStartAlarmCalendars();
		Intent intent = null;
		if(vt.getType() == TimerSession.TimerSessionType.VIBRATE) {
			intent = new Intent(parent, VibrateOnBroadcastReceiver.class);
		} else if (vt.getType() == TimerSession.TimerSessionType.SILENT) {
			intent = new Intent(parent, SilentOnBroadcastReceiver.class);
		}
		PendingIntent pi = null;
		for(Calendar time : times){
			int id = vt.getId() + time.get(Calendar.DAY_OF_WEEK);
			pi = PendingIntent.getBroadcast(parent, id,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);
			pi.cancel();
			am.cancel(pi);
			pi = PendingIntent.getBroadcast(parent, id+10,
					new Intent(parent, OffBroadcastReceiver.class),
					PendingIntent.FLAG_UPDATE_CURRENT);
			pi.cancel();
			am.cancel(pi);
		}
	}

	public List<TimerSession> getVibrateTimers() {
		// TODO add this to Timers
		return datastore.getAllTimers();
	}

	/**
	 * Create a PendingIntent that will activate at the specified time
	 * @param time - time in milliseconds
	 * @param id - unique id from generateNextId(context)
	 * @param intent - either VibrateOn or VibrateOff
	 * @author Napon, Sunny
	 */
	private void createSystemTimer(long time, int id, Intent intent){
		PendingIntent startVibrating = PendingIntent.getBroadcast(parent,
				id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		am.setRepeating(AlarmManager.RTC, time, WEEK_MILLISECONDS, startVibrating); 
	}

	/**
	 * Generate a unique id for each alarm.
	 * @param context
	 * @return a unique alarm id
	 * @author Napon
	 */
	public static int generateNextId(Context context) {
		SharedPreferences prefs = context.getSharedPreferences("idcounter", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		int counter = prefs.getInt("idcounter", -1);
		if(counter == -1) {
			counter = 0;
			editor.putInt("idcounter", counter);
			editor.commit();
		} else {
			counter = counter + 20;
			editor.putInt("idcounter", counter);
			editor.commit();
		}
		return counter;
	}
}