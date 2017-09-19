package com.obj;


import java.util.Date;


public class UserProfile {
	public boolean isFirstSync = true;


	public static enum Gender{
		MALE,FEMALE
	}

	private String id;
	private String firstName;
	private String lastName;
	private String email;
	private String bday;
	private Gender gender;
	private String country;
	private int hapifork_interval = 10; //default interval


	private boolean has_hapitrack = false;
	private boolean has_hapifork = false;
	private boolean has_hapiwatch = false;
	private String device_code;
	private String device_id;
	private String dateJoined;

	private String userName;
	private String password;

	private boolean approved;
	private boolean lockedout;

	private String numFriends;
	private String numActivities;
	private String numPhoto;

	private String url_picSmall;
	private String url_picMed;
	private String url_picLarge;

	private String height;

	private String startWeight;
	private String currentWeight;
	private String targetWeight;
	private String language;
	private String timezone;

	private String ticket;
	private long ticketExpiry; /*UNIX TIME IN UTC ZONE*/

	/**use for flask computations**/
	private float loveflask;
	private float liveflask;
	private float eatflask;

	private int flasklive_today;
	private int flasklove_today;
	private int flaskeat_today;

	private int flaskrelax_today;
	private int flasksleep_today;
	private int flasksteps_today;

	private int steps_today_flask;
	private int steps_goal_today;
	private boolean haspedometer;


	/*use for mystats*/

	private int steps_today;
	private int steps_yesterday;
	private int steps_week;

	private int sleep_today;
	private int sleep_yesterday;
	private int sleep_week;

	private int meditate_today;
	private int meditate_yesterday;
	private int meditate_week;

	private int hapi_today;
	private int hapi_yesterday;
	private int hapi_week;

	private int fork_today;
	private int fork_yesterday;
	private int fork_week;

	private int steps_goal = 10000;//to do: needs to retrieve it from server


	public UserProfile(){

	}

	public String getDevice_id() {
		return device_id;
	}

	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	public String getDevice_code() {
		return device_code;
	}

	public void setDevice_code(String device_code) {
		this.device_code = device_code;
	}

	public void setValue(String id,
						 String userName,
						 boolean approved,
						 boolean lockedout,
						 String firstName,
						 String lastName,
						 String email,
						 String bday,
						 Gender gender,
						 String country,
						 boolean hasHapitrack,
						 boolean hasHapifork,
						 boolean hasHapiwatch,
						 String dateJoined,
						 String device_id,
						 String device_code,
						 String password,
						 String numActivity,
						 String numPhoto,
						 String numfriends,
						 String language,
						 String timezone,
						 String height,
						 String startWeight,
						 String currentWeight,
						 String url_picSmall, String url_picMed, String url_picLarge,
						 String targetWeight, String ticket, long ticketExpiry){

		this.setId(id);
		this.setUserName(userName);
		this.setApproved(approved);
		this.setLockedout(lockedout);
		this.setFirstName(firstName);
		this.setLastName(lastName);
		this.setEmail(email);
		this.setBday(bday);
		this.setGender(gender);
		this.setHeight(height);
		this.setStartWeight(startWeight);
		this.setCurrentWeight(currentWeight);
		this.setTargetWeight(targetWeight);
		this.setTicket(ticket);
		this.setTicketExpiry(ticketExpiry);
		this.setDateJoined(dateJoined);
		this.setDevice_code(device_code);
		this.setDevice_id(device_id);
		this.setHasHapifork(hasHapifork);
		this.setHasHapitrack(hasHapitrack);
		this.setHasHapiwatch(hasHapiwatch);
		this.setNumActivities(numActivity);
		this.setNumFriends(numfriends);
		this.setNumPhoto(numPhoto);
		this.setLanguage(language);
		this.setTimezone(timezone);
		this.setUrl_picLarge(url_picLarge);
		this.setUrl_picMed(url_picMed);
		this.setUrl_picSmall(url_picSmall);
		this.setCountry(country);


	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public long getTicketExpiry() {
		return ticketExpiry;
	}

	public void setTicketExpiry(long ticketExpiry) {
		this.ticketExpiry = ticketExpiry;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public boolean isLockedout() {
		return lockedout;
	}

	public void setLockedout(boolean lockedout) {
		this.lockedout = lockedout;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBday() {
		return bday;
	}

	public void setBday(String bday) {
		this.bday = bday;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getStartWeight() {
		return startWeight;
	}

	public void setStartWeight(String startWeight) {
		this.startWeight = startWeight;
	}

	public String getCurrentWeight() {
		return currentWeight;
	}

	public void setCurrentWeight(String currentWeight) {
		this.currentWeight = currentWeight;
	}

	public String getTargetWeight() {
		return targetWeight;
	}

	public void setTargetWeight(String targetWeight) {
		this.targetWeight = targetWeight;
	}



	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public boolean isHasHapitrack() {
		return has_hapitrack;
	}

	public void setHasHapitrack(boolean hasHapitrack) {
		this.has_hapitrack = hasHapitrack;
	}

	public boolean isHasHapifork() {
		return has_hapifork;
	}

	public void setHasHapifork(boolean hasHapifork) {
		this.has_hapifork = hasHapifork;
	}

	public boolean isHasHapiwatch() {
		return has_hapiwatch;
	}

	public void setHasHapiwatch(boolean hasHapiwatch) {
		this.has_hapiwatch = hasHapiwatch;
	}

	public String getDateJoined() {
		return dateJoined;
	}

	public void setDateJoined(String dateJoined) {
		this.dateJoined = dateJoined;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNumFriends() {
		return numFriends;
	}

	public void setNumFriends(String numFriends) {
		this.numFriends = numFriends;
	}

	public String getNumActivities() {
		return numActivities;
	}

	public void setNumActivities(String numActivities) {
		this.numActivities = numActivities;
	}

	public String getNumPhoto() {
		return numPhoto;
	}

	public void setNumPhoto(String numPhoto) {
		this.numPhoto = numPhoto;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getUrl_picSmall() {
		return url_picSmall;
	}

	public void setUrl_picSmall(String url_picSmall) {
		this.url_picSmall = url_picSmall;
	}

	public String getUrl_picMed() {
		return url_picMed;
	}

	public void setUrl_picMed(String url_picMed) {
		this.url_picMed = url_picMed;
	}

	public String getUrl_picLarge() {
		return url_picLarge;
	}

	public void setUrl_picLarge(String url_picLarge) {
		this.url_picLarge = url_picLarge;
	}

	public float getLoveflask() {
		return loveflask;
	}

	public void setLoveflask(float loveflask) {
		this.loveflask = loveflask;
	}

	public float getLiveflask() {
		return liveflask;
	}

	public void setLiveflask(float liveflask) {
		this.liveflask = liveflask;
	}

	public float getEatflask() {
		return eatflask;
	}

	public void setEatflask(float eatflask) {
		this.eatflask = eatflask;
	}



	public int getSteps_today() {
		return steps_today;
	}

	public void setSteps_today(int steps_today) {
		this.steps_today = steps_today;
	}

	public int getSteps_yesterday() {
		return steps_yesterday;
	}

	public void setSteps_yesterday(int steps_yesterday) {
		this.steps_yesterday = steps_yesterday;
	}

	public int getSteps_week() {
		return steps_week;
	}

	public void setSteps_week(int steps_week) {
		this.steps_week = steps_week;
	}

	public int getSleep_today() {
		return sleep_today;
	}

	public void setSleep_today(int sleep_today) {
		this.sleep_today = sleep_today;
	}

	public int getSleep_yesterday() {
		return sleep_yesterday;
	}

	public void setSleep_yesterday(int sleep_yesterday) {
		this.sleep_yesterday = sleep_yesterday;
	}

	public int getSleep_week() {
		return sleep_week;
	}

	public void setSleep_week(int sleep_week) {
		this.sleep_week = sleep_week;
	}

	public int getMeditate_today() {
		return meditate_today;
	}

	public void setMeditate_today(int meditate_today) {
		this.meditate_today = meditate_today;
	}

	public int getMeditate_yesterday() {
		return meditate_yesterday;
	}

	public void setMeditate_yesterday(int meditate_yesterday) {
		this.meditate_yesterday = meditate_yesterday;
	}

	public int getMeditate_week() {
		return meditate_week;
	}

	public void setMeditate_week(int meditate_week) {
		this.meditate_week = meditate_week;
	}

	public int getHapi_today() {
		return hapi_today;
	}

	public void setHapi_today(int hapi_today) {
		this.hapi_today = hapi_today;
	}

	public int getHapi_yesterday() {
		return hapi_yesterday;
	}

	public void setHapi_yesterday(int hapi_yesterday) {
		this.hapi_yesterday = hapi_yesterday;
	}

	public int getHapi_week() {
		return hapi_week;
	}

	public void setHapi_week(int hapi_week) {
		this.hapi_week = hapi_week;
	}

	public int getFork_today() {
		return fork_today;
	}

	public void setFork_today(int fork_today) {
		this.fork_today = fork_today;
	}

	public int getFork_yesterday() {
		return fork_yesterday;
	}

	public void setFork_yesterday(int fork_yesterday) {
		this.fork_yesterday = fork_yesterday;
	}

	public int getFork_week() {
		return fork_week;
	}

	public void setFork_week(int fork_week) {
		this.fork_week = fork_week;
	}

	public int getSteps_goal() {
		return steps_goal;
	}

	public void setSteps_goal(int steps_goal) {
		this.steps_goal = steps_goal;
	}

	public int getFlasklive_today() {
		return flasklive_today;
	}

	public void setFlasklive_today(int flasklive_today) {
		this.flasklive_today = flasklive_today;
	}

	public int getFlasklove_today() {
		return flasklove_today;
	}

	public void setFlasklove_today(int flasklove_today) {
		this.flasklove_today = flasklove_today;
	}

	public int getFlaskeat_today() {
		return flaskeat_today;
	}

	public void setFlaskeat_today(int flaskeat_today) {
		this.flaskeat_today = flaskeat_today;
	}

	public int getFlaskrelax_today() {
		return flaskrelax_today;
	}

	public void setFlaskrelax_today(int flaskrelax_today) {
		this.flaskrelax_today = flaskrelax_today;
	}

	public int getFlasksleep_today() {
		return flasksleep_today;
	}

	public void setFlasksleep_today(int flasksleep_today) {
		this.flasksleep_today = flasksleep_today;
	}

	public int getFlasksteps_today() {
		return flasksteps_today;
	}

	public void setFlasksteps_today(int flasksteps_today) {
		this.flasksteps_today = flasksteps_today;
	}

	public int getSteps_today_flask() {
		return steps_today_flask;
	}

	public void setSteps_today_flask(int steps_today_flask) {
		this.steps_today_flask = steps_today_flask;
	}

	public int getSteps_goal_today() {
		return steps_goal_today;
	}

	public void setSteps_goal_today(int steps_goal_today) {
		this.steps_goal_today = steps_goal_today;
	}

	public boolean isHaspedometer() {
		return haspedometer;
	}

	public void setHaspedometer(boolean haspedometer) {
		this.haspedometer = haspedometer;
	}

	public int getHapifork_interval() {
		return hapifork_interval;
	}

	public void setHapifork_interval(int hapifork_interval) {
		this.hapifork_interval = hapifork_interval;
	}
}
