package com.controllers;

import java.util.Date;

public class StatsData {
	private MODE appMode;
	private Date dt_mealStart;
	private String str_mealStart;
	private int dt_mealDuration;
	private String str_mealDuration="00:00:00";
	private int i_targetInterval;
	private int i_lastInterval;
	private int i_forkServing;
	private int i_success;
	private int i_nextCounter;
	public boolean isSuccess =false;
	public String deviceID;
	public String deviceIDinHex;


	public enum MODE {MANUAL,BLUETOOTH_CONNECT,BLUETOOTH_DISCONNECT}
	public enum STATE{START,PAUSE,END}; //pause is when the app is in bg 

	public StatsData() {

		appMode = MODE.BLUETOOTH_DISCONNECT;

	}


	public MODE getAppMode() {
		return appMode;
	}

	public void setAppMode(MODE appMode) {
		this.appMode = appMode;
	}

	public Date getDt_mealStart() {
		return dt_mealStart;
	}

	public void setDt_mealStart(Date dt_mealStart) {
		this.dt_mealStart = dt_mealStart;
	}

	public String getStr_mealStart() {
		return str_mealStart;
	}

	public void setStr_mealStart(String str_mealStart) {
		this.str_mealStart = str_mealStart;
	}

	public int getDt_mealDuration() {
		return dt_mealDuration;
	}

	public void resetDt_mealDuration(){
		dt_mealDuration = 0;
	}

	public void setDt_mealDuration() {

		this.dt_mealDuration ++;

		if (dt_mealDuration <= 59){

			if(dt_mealDuration <=9){
				str_mealDuration = "0:00:0"+dt_mealDuration;
			}else{
				str_mealDuration = "0:00:"+dt_mealDuration;
			}

		}else {
			if (dt_mealDuration/60 <= 59){
				if(dt_mealDuration/60 <=9){
					if (dt_mealDuration%60 <= 9){
						str_mealDuration = "0:0"+(dt_mealDuration/60)+":0"+(dt_mealDuration%60);
					}else {
						str_mealDuration = "0:0"+(dt_mealDuration/60)+":"+(dt_mealDuration%60);

					}


				}else {
					if (dt_mealDuration%60 <= 9){
						str_mealDuration = "00:"+(dt_mealDuration/60)+":0"+(dt_mealDuration%60);
					}else {
						str_mealDuration = "00:"+(dt_mealDuration/60)+":"+(dt_mealDuration%60);

					}



				}

			}else {

			}
		}

	}

	public String getStr_mealDuration() {
		return str_mealDuration;
	}

	public void setStr_mealDuration(String str_mealDuration) {
		this.str_mealDuration = str_mealDuration;
	}

	public int getI_targetInterval() {
		return i_targetInterval;
	}

	public void setI_targetInterval(int i_targetInterval) {
		this.i_targetInterval = i_targetInterval;
	}

	public int getI_lastInterval() {
		return i_lastInterval;
	}

	public void setI_lastInterval(int i_lastInterval) {
		this.i_lastInterval = i_lastInterval;
	}

	public int getI_forkServing() {
		return i_forkServing;
	}

	public void setI_forkServing(int i_forkServing) {
		this.i_forkServing = i_forkServing;
	}

	public int getI_success() {
		return i_success;
	}

	public void setI_success(int i_success) {
		this.i_success = i_success;
	}

	public int getI_nextCounter() {
		return i_nextCounter;
	}

	public void setI_nextCounter(int i_nextCounter) {
		this.i_nextCounter = i_nextCounter;
	}
}