package com.ankit.pointofsolution.config;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class Messages {

	public static final String ERROR_PRECEDE = "Error - ";
	public static final String NO_INTERNET = "Unable to process. Internet connection unavailable. ";
	public static final String NO_API = "Unable to process. Server API connection problem occured.";
	public static final String CONN_TIMEDOUT = "Connection timeout.";
	public static final String SERVER_NOT_AVAILABLE = "Server is down. Please contact administrator.";
	public static final String CONFIG_FILE_NOT_AVAIL = "No config file associated in server";
	public static final String CANT_DOWNLOAD_SAVE = "Cannot download and save file.";

	public static final String CONFIRM_CLOSE_APP = "Do you want to close the application?";
	public static final String PLZ_WAIT_AUTHENTICATE = "Please wait while authenticating...";
	public static final String PLZ_WAIT_UPLOADING = "Please wait while uploading...";
	public static final String PLZ_WAIT_DOWNLOADING = "Please wait while downloading...";
	public static final String ERROR_GENERAL = "Error occured while processing.";
	public static final String LOGOUT_WHILE_TEST_RUNNING = "Test is running. Test will be stopped. Are you sure want to logout?";
	public static final String MSG_LOGOUT = "Are you sure want to logout?";
	public static final String TEST_ALREADY_COMPLETED = "Test already completed.";
	public static final String TEST_STOPPED = "Test has been stopped. Results will be uploaded";
	public static final String TEST_ALRDY_RUNNING = "Test is already running!";
	public static final String CONFIRM_STOP_TEST = "Are you sure want to stop the test?";
	public static final String CONFIRM_DOWNLOAD_CONFIG = "Do you want to download Configuration file?";
	public static final String CONFIRM_DATA_LOST = "Data will be lost. Are you sure want to Cancel?";
	public static final String ERROR_ON_PARSING_CONFIG = "Error occured while parsing config. Please try again";
	public static final String EXIT_WHILE_TEST_RUNNING = "Test is running. Test will be stopped. Are you sure want to exit?";
	public static final String PLZ_WAIT_LOADING_CONTACTS = "Please wait while loading contacts";
	public static final String EXT_TEST_STARTED = "External test started!";
	public static final String SELECT_ATLST_ONE_TEST = "Please select any of test to run";
	public static final String MO_TEST_NOT_CONFIGURED_PRP = "Mobile Originated test not configured properly.";
	public static final String MT_TEST_NOT_CONFIGURED_PRP = "Mobile Terminated test not configured properly.";
	public static final String FTP_TEST_NOT_CONFIGURED_PRP = "FTP test not configured properly.";
	public static final String UDP_TEST_NOT_CONFIGURED_PRP = "UDP test not configured properly.";

	public static final String ENTER_VERIFICATION_CODE = "Verification Code cannot be empty";
	public static final String FTP_LOGIN_FAILED = "Failed to connect to FTP server";
	public static final String FTP_UPLOAD_FAILED = "Failed to upload to FTP server";
	public static final String FTP_DOWNLOAD_FAILED = "Failed to download to FTP server";
	public static final String UDP_CONNECTION_FAILED = "Failed to connect to UDP server";
	public static final String UDP_CONNECTION_TIMEOUT = "Timeout occured to connect to UDP server";
	public static final String UDP_UPLOAD_FAILED = "Failed to upload to UDP server";
	public static final String PING_FAILED = "Failed to ping server";
	public static final String BROWER_TEST_FAILED = "Failed to load page";
	public static final String INVALID_USERNAME_PWD = "Invalid username/password or inactive user!";
	//public static final String ACCESS_DENIED = "Access denied";
	public static final String ACCESS_DENIED = "Incorrect verification code. Please contact admin!";
	public static final String NO_FAILED_FILES_TO_UPLOAD = "No failed results to upload!";
	public static final String FAILED_UPLOAD_ALRDY_RUNNING = "Failed result upload process running.";


	public static final String MSG_INVALID_USER = "You are an inactive user!";
	public static final String NO_ITEMS_IN_CART = "Cart is empty. please add items in cart.";

	public static String getEmptyText(String str) {
		return String.format("Please enter %s!", str);
	}

	public static String getEmptyChoose(String str) {
		return String.format("Please choose %s!", str);
	}

	public static String getInvalidText(String str) {
		return String.format("Invalid %s value", str);
	}

	public static String getInvalidConfigText(String str) {
		return String.format("%s test not configured properly.", str);
	}

	public static String getInvalidChoose(String str) {
		return String.format("Please choose valid %s!", str);
	}

	public static String err(Exception e) {
		return ERROR_PRECEDE + e.toString();
	}

	public static String errMsg(Exception ex) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		ex.printStackTrace(printWriter);
		String stacktrace = result.toString();
		printWriter.close();
		return ERROR_PRECEDE + stacktrace;
	}
}
