package com.github.dr.serverbot.data.global;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Data {
	public static final String Plugin_Path 						= "/plugins";
	public static final String Plugin_Data_Path 				= "/plugins/Server-Mdt-Bot";
	public static final String Plugin_Lib_Path 					= "/plugins/Server-Mdt-Bot/lib";
	public static final String Get 								= "/api/get/";
	public static final String Set 								= "/api/set/";

	public static List<Long> QunData = new ArrayList<Long>();

	// PINGS
	public static final ThreadPoolExecutor Thred_service 		= new ThreadPoolExecutor(10000,11000,1,TimeUnit.MINUTES,new LinkedBlockingDeque<Runnable>(50000));
	public static final ThreadPoolExecutor ReRun 				= new ThreadPoolExecutor(10,10,1,TimeUnit.MINUTES,new LinkedBlockingDeque<Runnable>());

}