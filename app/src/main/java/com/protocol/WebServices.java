package com.protocol;

public class WebServices {

    public WebServices() {

    }

    // public static CONNECTION ConnectionType = CONNECTION.QC;
    public static CONNECTION ConnectionType = CONNECTION.LIVE;
    public static final String COOKIE_NAME = "HapiAccountTicket";
    public static final String DOMAIN_NAME = ".hapilabs.com";
    public static final String DOMAIN_NAME_WEBKIT = "https://www.hapi.com";

    public static class URL {

        /* staging */
        public static final String SENDCONNECT_STAGING = "http://api.hapilabs.com/beta/product/common/v1/post?cmd=send_connection";
        public static final String SENDDATA_STAGING = "http://api.hapilabs.com/beta/product/hapifork/v1/post?cmd=send_data";
        public static final String LOGIN_STAGING = "http://staging.api.hapilabs.com/mobilehapifork/login?command=post_mobilelogin";
        public static final String UPLOADIMG_STAGING = "http://staging.api.hapilabs.com/mobilehapifork/uploadphoto?command=post_mobilephoto&";

        /* live */
        public static final String GET_DEVICEID_STATUS = "http://api.hapilabs.com/product/hapifork/v2/get?cmd=get_device_id_status&";
        public static final String GET_DEVICEID_STATUS_QC = "http://qc.api.hapilabs.com/product/hapifork/v2/get?cmd=get_device_id_status&";
        public static final String GET_DEVICE_PROFILE = "http://api.hapilabs.com/product/hapifork/v2/get?cmd=get_profile&";
        public static final String GET_DEVICE_PROFILE_QC = "http://api.hapilabs.com/product/hapifork/v2/get?cmd=get_profile&";
        public static final String SENDCONNECT_LIVE = "http://api.hapilabs.com/product/common/v1/post?cmd=send_connection";
        public static final String SENDDISCONNECT_LIVE = "http://api.hapilabs.com/product/common/v1/post?cmd=send_connection";
        public static final String SENDDATA_LIVE = "http://api.hapilabs.com/product/hapifork/v1/post?cmd=send_data";
        public static final String SENDACCOUNT_LINK = "http://api.hapilabs.com/product/common/v2/post?cmd=send_account_link";
        public static final String SENDACCOUNT_LINK_QC = "http://qc.api.hapilabs.com/product/common/v2/post?cmd=send_account_link";
        public static final String SENDACCOUNT_UNLINK = "http://api.hapilabs.com/product/common/v1/post?cmd=send_account_unlink";
        public static final String SENDACCOUNT_UNLINK_QC = "http://qc.api.hapilabs.com/product/common/v1/post?cmd=send_account_unlink";
        public static final String SEND_CONNECTION = "http://api.hapilabs.com/product/common/v2/post?cmd=send_connection";
        public static final String SEND_DISCONNECTION = "http://api.hapilabs.com/product/common/v2/post?cmd=send_disconnection";

        public static final String LOGIN_LIVE = "http://api.hapilabs.com/mobilehapifork/login?command=post_mobilelogin";
        public static final String UPLOADIMG_LIVE = "http://api.hapilabs.com/mobilehapifork/uploadphoto?command=post_mobilephoto";

        /* qc */
        public static final String SENDCONNECT_QC = "http://qc.api.hapilabs.com/beta/product/common/v1/post?cmd=send_connection";
        public static final String SENDDATA_QC = "http://qc.api.hapilabs.com/beta/product/hapifork/v1/post?cmd=send_data";

        public static final String LOGIN_QC = "http://qc.api.hapilabs.com/mobilehapifork/login?command=post_mobilelogin";
        public static final String UPLOADIMG_QC = "http://qc.api.hapilabs.com/mobilehapifork/uploadphoto?command=post_mobilephoto";

    }

    public static class COMMAND {

        public static final String SEND_CONNECT = "send_connection";
        public static final String SEND_DATA = "send_data";
        public static final String LOGIN = "post_mobilelogin";
        public static final String UPLOAD_IMAGE = "post_mobilephoto";
        public static final String GET_DEVICEID_STATUS = "get_device_id_status";
        public static final String GET_DEVICE_PROFILE = "get_profile";
        public static final String SEND_ACCOUNT_LINK = "send_account_link";
        public static final String SEND_ACCOUNT_UNLINK = "send_account_unlink";
        public static final String SEND_CONNECTION = "send_connection";
        public static final String SEND_DISCONNECTION = "send_disconnection";

    }

    public enum SERVICES {
        SEND_CONNECT,
        SEND_DATA,
        LOGIN,
        UPLOAD_IMAGE,
        GET_DEVICE_ID_STATUS,
        GET_DEVICE_PROFILE,
        SEND_ACCOUNT_LINK,
        SEND_ACCOUNT_UNLINK,
        SEND_CONNECTION,
        SEND_DISCONNECTION
    }

    public enum CONNECTION {
        STAGING, LIVE, QC
    }

    public String getCommand(SERVICES service) {
        String command = null;

        switch (service) {
            case SEND_CONNECT:
                command = COMMAND.SEND_CONNECT;
                break;
            case SEND_DATA:
                command = COMMAND.SEND_DATA;
                break;
            case LOGIN:
                command = COMMAND.LOGIN;
                break;
            case UPLOAD_IMAGE:
                command = COMMAND.UPLOAD_IMAGE;
                break;
            case GET_DEVICE_ID_STATUS:
                command = COMMAND.GET_DEVICEID_STATUS;
                break;
            case GET_DEVICE_PROFILE:
                command = COMMAND.GET_DEVICE_PROFILE;
                break;
            case SEND_ACCOUNT_LINK:
                command = COMMAND.SEND_ACCOUNT_LINK;
                break;
            case SEND_ACCOUNT_UNLINK:
                command = COMMAND.SEND_ACCOUNT_UNLINK;
                break;
            case SEND_CONNECTION:
                command = COMMAND.SEND_CONNECTION;
                break;
            case SEND_DISCONNECTION:
                command = COMMAND.SEND_DISCONNECTION;
                break;

            default:
                break;
        }
        return command;
    }

    public String getURL(SERVICES service) {
        String url = null;

        switch (service) {
            case SEND_CONNECT:
                url = (ConnectionType == CONNECTION.LIVE) ? URL.SENDCONNECT_LIVE
                        : URL.SENDCONNECT_QC;
                break;
            case SEND_DATA:
                url = (ConnectionType == CONNECTION.LIVE) ? URL.SENDDATA_LIVE
                        : URL.SENDDATA_QC;
                break;
            case LOGIN:
                url = (ConnectionType == CONNECTION.LIVE) ? URL.LOGIN_LIVE
                        : URL.LOGIN_QC;
                break;
            case UPLOAD_IMAGE:
                url = (ConnectionType == CONNECTION.LIVE) ? URL.UPLOADIMG_LIVE
                        : URL.UPLOADIMG_QC;
                break;
            case GET_DEVICE_ID_STATUS:
                url = (ConnectionType == CONNECTION.LIVE) ? URL.GET_DEVICEID_STATUS : URL.GET_DEVICEID_STATUS;
                break;
            case GET_DEVICE_PROFILE:
                url = (ConnectionType == CONNECTION.LIVE) ? URL.GET_DEVICE_PROFILE : URL.GET_DEVICE_PROFILE;
                break;
            case SEND_ACCOUNT_LINK:
                url = (ConnectionType == CONNECTION.LIVE) ? URL.SENDACCOUNT_LINK : URL.SENDACCOUNT_LINK;
                break;
            case SEND_ACCOUNT_UNLINK:
                url = (ConnectionType == CONNECTION.LIVE) ? URL.SENDACCOUNT_UNLINK : URL.SENDACCOUNT_UNLINK;
                break;
            case SEND_CONNECTION:
                url = (ConnectionType == CONNECTION.LIVE) ? URL.SEND_CONNECTION : URL.SEND_CONNECTION;
                break;
            case SEND_DISCONNECTION:
                url = (ConnectionType == CONNECTION.LIVE) ? URL.SEND_DISCONNECTION : URL.SEND_DISCONNECTION;
                break;
            default:
                break;
        }
        return url;
    }

}
