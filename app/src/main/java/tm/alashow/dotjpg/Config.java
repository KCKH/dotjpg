/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg;

/**
 * Created by alashov on 30/07/15.
 */
public class Config {

    public static String SERVER = "http://dotjpg.co/";
    public static String API = "api/";

    public static boolean DEBUG = BuildConfig.DEBUG;
    public static String LOG_APP_NAME = "dotjpg App";
    public static String LOCAL_IMAGES_FOLDER = "dotjpg";

    //Activity tags
    public static String ACTIVITY_TAG_MAIN = "main";
    public static String ACTIVITY_TAG_IMAGE_DETAILS = "main";
    public static String ACTIVITY_TAG_IMAGE_NEW = "main";
    public static String ACTIVITY_TAG_IMAGES = "images_";
    public static String ACTIVITY_TAG_PREFERENCES = "preferences";

    //Activity extras
    public static String EXTRA_URL = "EXTRA_URL";
    public static String EXTRA_HEIGHT = "EXTRA_HEIGHT";
    public static String EXTRA_WIDTH = "EXTRA_WIDTH";
    public static String EXTRA_TOP = "EXTRA_TOP";
    public static String EXTRA_LEFT = "EXTRA_LEFT";
    public static String EXTRA_ORIENTATION = "EXTRA_ORIENTATION";

    public static String EXTRA_IMAGE_FILENAME = "EXTRA_IMAGE_FILENAME";
    public static String EXTRA_IMAGE = "EXTRA_IMAGE";
    public static String EXTRA_GALLERY_ID = "EXTRA_GALLERY_ID";
    public static String EXTRA_IMAGES_TYPE = "EXTRA_IMAGES_TYPE";
    public static String EXTRA_URLS = "EXTRA_URLS";
    public static String EXTRA_POSITION = "EXTRA_POSITION";

    //API config
    public static int API_IMAGE_MAX_FILE_SIZE = 20000000; //20 mb
    public static int API_IMAGE_MAX_FILE_COUNT = 20; //20 mb

    public static String API_PAGE_PARAM = "page";
    public static String API_SESSION_ID_PARAM = "session_id";

    public static String API_CONTROLLER_PARAM = "controller";
    public static String API_CONTROLLER_IMAGE = "image";

    public static String API_ACTION_PARAM = "action";

    public static String API_ACTION_GET_ALL_MY = "getAll";
    public static String API_ACTION_GET_SPECIAL = "getAllSpecial";
    public static String API_ACTION_GET_GALLERY = "getGallery";

    public static String API_ACTION_UPLOAD_FILE = "fileUpload";
    public static String API_ACTION_UPLOAD_FILE_PARAM = "images[]";

    public static String API_ACTION_UPLOAD_URL = "urlUpload";
    public static String API_ACTION_UPLOAD_URL_PARAM = "images";
}
