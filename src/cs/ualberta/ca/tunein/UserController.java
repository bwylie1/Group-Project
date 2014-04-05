package cs.ualberta.ca.tunein;

import cs.ualberta.ca.tunein.network.ElasticSearchOperations;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Controller
 * UserController Class:
 * This is a controller class that modifies the user model
 * and will also be used to load a saved username.
 */
public class UserController {
	
	//tag for checking if there exists a profile for this user
	public final static String NEWPROFILE = "cs.ualberta.ca.tunein.profile";
	
	private Commenter user;
	
	/**
	 * Constructor for controller to save and load in the current user's name and id
	 */
	public UserController() {
		this.user = new Commenter();
	}
	
	/**
	 * Construcotr for controller to check userid with profile id and
	 * loading in the profile.
	 * @param aUser The user to be modified
	 */
	public UserController(Commenter aUser)
	{
		this.user = aUser;
	}

	/**
	 * Method to get current user's username
	 * @param cntxt Context of app
	 * @return the username
	 */
	public String loadUsername(Context cntxt) {
		return user.getCurrentName(cntxt);
	}
	
	/**
	 * Method to get the current user's user id
	 * @param cntxt
	 * @return
	 */
	public String loadUserid(Context cntxt) {
		return user.getCurrentUniqueID(cntxt);
	}
	
	public void saveUserid(Context cntxt) {
		//setup an unique id for the user that is attached to the phone
		final TelephonyManager tm = (TelephonyManager) cntxt.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = "" + tm.getDeviceId();
		String androidId = "" + android.provider.Settings.Secure.getString(cntxt.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		String id = deviceId + androidId;
		user.setCurrentUniqueID(id, cntxt);
	}

	public void changeUsername(String name, Context cntxt) {
		user.setCurrentName(name, cntxt);
	}
	
	public boolean checkCurrentUser(Context cntxt, String userID)
	{
		//id of the current viewer
		Commenter aUser = new Commenter();
		String currentID = aUser.getCurrentUniqueID(cntxt);
		return user.getUniqueID().equals(currentID);
	}
	
	public void createProfile(Context cntxt)
	{
		SharedPreferences prefs = cntxt.getSharedPreferences(
			      "cs.ualberta.ca.tunein", Context.MODE_PRIVATE);
		boolean newProfile = prefs.getBoolean(NEWPROFILE, true);
		if(newProfile)
		{
			ElasticSearchOperations eso = new ElasticSearchOperations();
			//create a whole new profile
			Commenter commenter = new Commenter(cntxt);
			eso.postProfileModel(commenter);
			prefs.edit().putBoolean(NEWPROFILE, false).commit();
		}
	}
	
	public void loadProfile(String userID, Context cntxt)
	{
		ElasticSearchOperations eso = new ElasticSearchOperations();
		eso.getProfileModel(userID, user, cntxt);
	}
	
	public void saveProfile(String name, String email, String facebook, String twitter, String bio, Bitmap bmp)
	{
		user.setName(name);
		user.setEmail(email);
		user.setFacebook(facebook);
		user.setTwitter(twitter);
		user.setBio(bio);
		Image img = new Image(bmp);
		user.setHasImage(true);
		user.setAvatar(img);
		ElasticSearchOperations eso = new ElasticSearchOperations();
		eso.putProfileModel(user);
	}
	
	public void clearprefs(Context cntxt)
	{
		SharedPreferences prefs = cntxt.getSharedPreferences(
			      "cs.ualberta.ca.tunein", Context.MODE_PRIVATE);
		prefs.edit().remove(NEWPROFILE).commit();
	}

}
