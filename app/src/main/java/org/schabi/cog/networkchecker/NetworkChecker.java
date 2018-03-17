package org.schabi.cog.networkchecker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChecker {

	Context context;
	public NetworkChecker(Context context) {
		
		this.context=context;
		
	}
	
	public boolean isConnectingNetwork() {
		
		ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
		if(connectivityManager!=null)
		{
			NetworkInfo info[]= connectivityManager.getAllNetworkInfo();
			if(info!=null)
			{
				for (int i = 0; i < info.length; i++) {
					
					if(info[i].getState()==NetworkInfo.State.CONNECTED)
					{
						return true;
					}
				}
			}
			
		}
		return false;
		
		
	}
	
	
	public void showInternetDialog() {
		
		AlertDialog.Builder alertDialog=new AlertDialog.Builder(this.context);
		alertDialog.setMessage("No internet connection!");
		alertDialog.setCancelable(false);
		alertDialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				
			}
		});
		alertDialog.show();
		
		
		
	}
}
