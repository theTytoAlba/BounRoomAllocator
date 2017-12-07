package com.thetytoalba.bounroomallocator;

/**
 * Created by vivian on 3.12.2017.
 */

class Constants {
    static String HOST_IP ="192.168.4.193";
    static int HOST_PORT = 60015;

    // Connection types
    static String TAG_CONNECTION_TYPE = "connectionType";
    static String TAG_LOGIN_CONNECTION = "loginConnection";
    static String TAG_SIGN_UP_CONNECTION = "signUpConnection";
    static String TAG_ADD_BUILDING_CONNECTION = "addBuildingConnection";
    static String TAG_DELETE_BUILDING_CONNECTION = "deleteBuildingConnection";
    static String TAG_ADD_ROOM_CONNECTION = "addRoomConnection";
    static String TAG_DELETE_ROOM_CONNECTION = "deleteRoomConnection";
    static String TAG_GET_ROOMS_CONNECTION = "getRoomsConnection";
    static String TAG_GET_WEEK_CONNECTION = "getWeekConnection";
    static String TAG_GET_AVAILABLE_ROOMS_CONNECTION = "getAvailableRoomsConnection";
    static String TAG_ADD_LECTURE_CONNECTION = "addLectureConnection";

    // Return tags
    static String TAG_SUCCESS = "success";
    static String TAG_CREDENTIAL = "credential";
    static String TAG_USERNAME = "username";
    static String TAG_PASSWORD = "password";
    static String TAG_USER_TYPE = "userType";
    static String TAG_STUDENT = "student";
    static String TAG_TEACHER = "teacher";

    // Manager related
    static String TAG_MANAGER = "manager";
    static String TAG_BUILDING_NAME = "buildingName";
    static String TAG_BUILDING = "building";
    static String TAG_ROOMS = "rooms";
    static String TAG_ROOM = "room";
    static String TAG_ROOM_NAME = "roomName";
    static String TAG_ROOM_CAPACITY = "roomCapacity";

    // Teacher related
    static String TAG_WEEK = "week";
    static String TAG_MONDAY = "Monday";
    static String TAG_TUESDAY = "Tuesday";
    static String TAG_WEDNESDAY = "Wednesday";
    static String TAG_THURSDAY = "Thursday";
    static String TAG_FRIDAY = "Friday";
    static String TAG_SATURDAY = "Saturday";
    static String TAG_SUNDAY = "Sunday";
    static String TAG_DETAILS = "details";
    static String TAG_LECTURE_NAME = "lectureName";
}
