package com.protocol.xml.handlers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.protocol.AppUtil;
import com.obj.UserProfile;
import com.obj.UserProfile.Gender;


import android.os.Handler;

public class LoginResponseHandler extends DefaultResponseHandler {

    protected Handler handler;
    protected boolean isError = false;

    final String TAG_MESSAGE = "message";

    final String TAG_USERID = "user_id";
    final String TAG_FNAME = "firstname";
    final String TAG_LNAME = "lastname";
    final String TAG_EMAIL = "email";
    final String TAG_BDAY = "birthday";
    final String TAG_GENDER = "gender";

    final String TAG_COUNTRY = "country";
    final String TAG_HAPITRACK = "has_hapitrack";
    final String TAG_HAPIFORK = "has_hapifork";
    final String TAG_HAPIWATCH = "has_hapiwatch";
    final String TAG_DATE_JOINED = "date_join";
    final String TAG_DEVICE_CODE = "device_code";
    final String TAG_DEVICE_ID = "device_id";

    final String TAG_USERNAME = "username";
    final String TAG_PASSWORD = "password";
    final String TAG_NUM_FRIENDS = "num_friends";
    final String TAG_NUM_ACTIVITIES = "num_activities";
    final String TAG_NUM_PHOTO = "num_photos";
    final String TAG_LANGUAGE = "language";
    final String TAG_TIMEZONE = "timezone";
    final String TAG_PIC_SMALL = "picture_url_small";
    final String TAG_PIC_MED = "picture_url_medium";
    final String TAG_PIC_LARGE = "picture_url_large";


    final String TAG_HEIGHT = "height";
    final String TAG_SWEIGHT = "start_weight";
    final String TAG_CWEIGHT = "current_weight";
    final String TAG_TWEIGHT = "target_weight";
    final String TAG_APPROVED = "approved";
    final String TAG_LOCKEDOUT = "lockedout";
    final String TAG_TICKETEXPIRY = "expiry_date";
    final String TAG_TICKET = "value";
    final String TAG_HAPIFORK_INTERVAL = "hapifork_interval";


    final String TAG_MESSAGEDETAIL = "message_detail";


    String tempString;

    /*Parser Output*/
    String id;
    String userName;
    boolean approved;
    boolean lockedout;
    String firstName = null;
    String lastName = null;
    String email = null;
    String bday = null;
    Gender gender = null;
    String country = null;
    boolean hasHapitrack = false;
    boolean hasHapifork = false;
    boolean hasHapiwatch = false;
    String dateJoined = null;
    String deviceID = null;
    String deviceCode = null;
    String password = null;

    int hapifork_interval = 10;
    String numFriends;
    String numActivities;
    String numPhoto;

    String url_picSmall = null;
    String url_picMed = null;
    String url_picLarge = null;

    String height = null;
    String startWeight = null;
    String currentWeight = null;
    String targetWeight = null;
    String language = null;
    String timezone = null;


    String ticket = null;
    long ticketExpiry;
    String message;
    boolean success = true;

    UserProfile userProfile_obj;

    public LoginResponseHandler(Handler handler) {
        super(handler);
        this.handler = handler;

    }


    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        userProfile_obj = new UserProfile();
    }


    @Override
    public void endDocument() throws SAXException {
        //return to caller
        System.out.println("ENDDOC@LOGINRESPONSEHANDLER " + success + " " + firstName + " " + hasHapifork);
        if (success) {
            userProfile_obj.setValue(id, userName, approved, lockedout,
                    firstName, lastName, email, bday, gender,
                    country, hasHapitrack, hasHapifork, hasHapiwatch, dateJoined, deviceID, deviceCode,
                    password, numActivities, numPhoto, numFriends,
                    language, timezone,
                    height, startWeight, currentWeight, url_picSmall, url_picMed, url_picLarge,
                    targetWeight, ticket, ticketExpiry);
            userProfile_obj.setHapifork_interval(hapifork_interval);
            this.responseObj = userProfile_obj;
        } else {

            this.responseObj = message;
        }
        super.endDocument();
    }


    @Override
    public void endElement(String uri, String localName, String elementName) throws SAXException {
        System.out.println(localName + " " + tempString);
        if (elementName.equals(TAG_MESSAGE)) {
            if (tempString != null && tempString.length() > 0) {
                if (tempString.trim().compareTo("Failed".trim()) == 0) {
                    success = false;
                }
            }
        } else if (elementName.equals(TAG_MESSAGEDETAIL)) {
            if (tempString != null && tempString.length() > 0) {
                message = tempString;
            }
        } else if (elementName.equals(TAG_USERID)) {
            if (tempString != null && tempString.length() > 0) {
                id = tempString;
            }
        } else if (elementName.equals(TAG_FNAME)) {
            if (tempString != null && tempString.length() > 0)
                firstName = tempString;
        } else if (elementName.equals(TAG_LNAME)) {
            if (tempString != null && tempString.length() > 0)
                lastName = tempString;
        } else if (elementName.equals(TAG_EMAIL)) {
            if (tempString != null && tempString.length() > 0)
                email = tempString;
        } else if (elementName.equals(TAG_BDAY)) {
            if (tempString != null && tempString.length() > 0) {
                bday = tempString;
            }
        } else if (elementName.equals(TAG_GENDER)) {
            if (tempString != null && tempString.length() > 0) {
                gender = AppUtil.StringtoGender(tempString);
            }
        } else if (elementName.equals(TAG_HEIGHT)) {
            if (tempString != null && tempString.length() > 0)
                height = tempString;
        } else if (elementName.equals(TAG_SWEIGHT)) {
            if (tempString != null && tempString.length() > 0)
                startWeight = tempString;
        } else if (elementName.equals(TAG_CWEIGHT)) {
            if (tempString != null && tempString.length() > 0)
                currentWeight = tempString;
        } else if (elementName.equals(TAG_TWEIGHT)) {
            if (tempString != null && tempString.length() > 0)
                targetWeight = tempString;
        } else if (elementName.equals(TAG_USERNAME)) {
            if (tempString != null && tempString.length() > 0)
                userName = tempString;
        } else if (elementName.equals(TAG_APPROVED)) {
            if (tempString != null && tempString.length() > 0) {
                approved = AppUtil.StringtoBooleanFormat(tempString, true);
            }
        } else if (elementName.equals(TAG_LOCKEDOUT)) {
            if (tempString != null && tempString.length() > 0) {
                lockedout = AppUtil.StringtoBooleanFormat(tempString, false);
            }
        } else if (elementName.equals(TAG_TICKET)) {
            if (tempString != null && tempString.length() > 0) {
                ticket = tempString;
            }
        } else if (elementName.equals(TAG_TICKETEXPIRY)) {
            if (tempString != null && tempString.length() > 0) {
                ticketExpiry = Long.parseLong("1399201861");
            }
        } else if (elementName.equals(TAG_COUNTRY)) {
            if (tempString != null && tempString.length() > 0) {
                country = tempString;
            }
        } else if (elementName.equals(TAG_HAPITRACK)) {
            if (tempString != null && tempString.length() > 0) {
                hasHapitrack = AppUtil.StringtoBooleanFormat(tempString, false);
                ;
            }
        } else if (elementName.equals(TAG_HAPIFORK)) {
            if (tempString != null && tempString.length() > 0) {
                hasHapifork = AppUtil.StringtoBooleanFormat(tempString, false);
            }
        } else if (elementName.equals(TAG_HAPIWATCH)) {
            if (tempString != null && tempString.length() > 0) {
                hasHapiwatch = AppUtil.StringtoBooleanFormat(tempString, false);
            }
        } else if (elementName.equals(TAG_DEVICE_CODE)) {
            if (tempString != null && tempString.length() > 0) {
                deviceCode = tempString;
            }
        } else if (elementName.equals(TAG_DEVICE_ID)) {
            if (tempString != null && tempString.length() > 0) {
                deviceID = tempString;
            }
        } else if (elementName.equals(TAG_DATE_JOINED)) {
            if (tempString != null && tempString.length() > 0) {
                dateJoined = tempString;
            }
        } else if (elementName.equals(TAG_PASSWORD)) {
            if (tempString != null && tempString.length() > 0) {
                password = tempString;
            }
        } else if (elementName.equals(TAG_NUM_ACTIVITIES)) {
            if (tempString != null && tempString.length() > 0) {
                numActivities = tempString;
            }
        } else if (elementName.equals(TAG_NUM_FRIENDS)) {
            if (tempString != null && tempString.length() > 0) {
                numFriends = tempString;
            }
        } else if (elementName.equals(TAG_NUM_PHOTO)) {
            if (tempString != null && tempString.length() > 0) {
                numPhoto = tempString;
            }
        } else if (elementName.equals(TAG_LANGUAGE)) {
            if (tempString != null && tempString.length() > 0) {
                language = tempString;
            }
        } else if (elementName.equals(TAG_TIMEZONE)) {
            if (tempString != null && tempString.length() > 0) {
                timezone = tempString;
            }
        } else if (elementName.equals(TAG_PIC_LARGE)) {
            if (tempString != null && tempString.length() > 0) {
                url_picLarge = tempString;
            }
        } else if (elementName.equals(TAG_PIC_MED)) {
            if (tempString != null && tempString.length() > 0) {
                url_picMed = tempString;
            }
        } else if (elementName.equals(TAG_PIC_SMALL)) {
            if (tempString != null && tempString.length() > 0) {
                url_picSmall = tempString;
            }
        } else if (elementName.equals(TAG_HAPIFORK_INTERVAL)) {
            if (tempString != null && tempString.length() > 0) {
                try {
                    hapifork_interval = Integer.parseInt(tempString);
                } catch (Exception e) {
                    hapifork_interval = 10;
                }
            }
        }


    }


    @Override
    public void startElement(String uri, String localName, String elementName, Attributes attrs) throws SAXException {


    }

    @Override
    public Object getResponseObj() {
        return userProfile_obj;
    }

    @Override
    public void characters(char ch[], int start, int length) {
        // get all text value inside the element tag
        tempString = new String(ch, start, length);
        tempString = tempString.trim(); // remove all white-space characters
    }


    @Override
    public Object getResponse() {
        return userProfile_obj;
    }

}
