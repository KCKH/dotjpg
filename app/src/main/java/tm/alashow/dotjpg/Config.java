/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg;

/**
 * Created by alashov on 30/07/15.
 */
public class Config {

    public static final String SERVER = "https://dotjpg.co/";
    public static final String HEADER_IMAGE = SERVER + "header.jpg";
    public static final String API = "api/";
    public static final String GCM_SENDER_ID = "835054112702";

    public static boolean DEBUG = BuildConfig.DEBUG;
    public static final String LOG_APP_NAME = "dotjpg App";
    public static final String LOCAL_IMAGES_FOLDER = "dotjpg";
    public static final String LOCAL_IMAGES_COMPRESSED_TEMP = ".temp_compressed";

    //Activity tags
    public static final String ACTIVITY_TAG_MAIN = "main";
    public static final String ACTIVITY_TAG_IMAGE_DETAILS = "imageDetails";
    public static final String ACTIVITY_TAG_IMAGE_NEW = "newImage";
    public static final String ACTIVITY_TAG_IMAGES = "images_";
    public static final String ACTIVITY_TAG_PREFERENCES = "preferences";

    //Activity extras
    public static final String EXTRA_URL = "EXTRA_URL";
    public static final String EXTRA_HEIGHT = "EXTRA_HEIGHT";
    public static final String EXTRA_WIDTH = "EXTRA_WIDTH";
    public static final String EXTRA_TOP = "EXTRA_TOP";
    public static final String EXTRA_LEFT = "EXTRA_LEFT";
    public static final String EXTRA_ORIENTATION = "EXTRA_ORIENTATION";

    public static final String EXTRA_IMAGE_FILENAME = "EXTRA_IMAGE_FILENAME";
    public static final String EXTRA_IMAGE = "EXTRA_IMAGE";
    public static final String EXTRA_GALLERY_ID = "EXTRA_GALLERY_ID";
    public static final String EXTRA_IMAGES_TYPE = "EXTRA_IMAGES_TYPE";
    public static final String EXTRA_URLS = "EXTRA_URLS";
    public static final String EXTRA_POSITION = "EXTRA_POSITION";

    //API config
    public static int API_IMAGE_MAX_FILE_SIZE = 20000000; //20 mb
    public static int API_IMAGE_MAX_FILE_COUNT = 20; //20 mb
    public static String[] API_ALLOWED_IMAGE_TYPES = {"jpeg", "jpg", "png", "gif", "bmp"};

    public static final String API_PAGE_PARAM = "page";
    public static final String API_SESSION_ID_PARAM = "session_id";
    public static final String API_REGISTRATION_ID_PARAM = "reg_id";
    public static final String API_GALLERY_ID_PARAM = "gallery_id";

    public static final String API_CONTROLLER_PARAM = "controller";
    public static final String API_CONTROLLER_IMAGE = "image";
    public static final String API_CONTROLLER_USER = "user";

    public static final String API_ACTION_PARAM = "action";

    public static final String API_ACTION_GET_ALL_MY = "getAll";
    public static final String API_ACTION_GET_SPECIAL = "getAllSpecial";
    public static final String API_ACTION_GET_GALLERY = "getGallery";
    public static final String API_ACTION_REGISTER_USER = "registerUser";

    public static final String API_ACTION_UPLOAD_FILE = "fileUpload";
    public static final String API_ACTION_UPLOAD_FILE_PARAM = "images[]";

    public static final String API_ACTION_UPLOAD_URL = "urlUpload";
    public static final String API_ACTION_UPLOAD_URL_PARAM = "images";


    public static final int IMAGE_COMPRESS_MIN_BYTE = 100 * 1024; //100 kb
}
