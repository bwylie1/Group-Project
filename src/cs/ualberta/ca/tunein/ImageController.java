package cs.ualberta.ca.tunein;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;

/**
 * Controller
 * ImageController Class:
 * This is a controller used to upload images when creating
 * or editing comments.
 * This code is from:
 * http://stackoverflow.com/questions/4455558/allow-user-to-select-camera-or-gallery-for-image
 */
public class ImageController {

	public static int SELECT_PICTURE_REQUEST_CODE = 12345;
	private Uri outputFileUri;
	private Activity act;
	
	public ImageController(Activity act)
	{
		this.act = act;
	}
	
	public Uri openImageIntent() {

		
		// Determine Uri of camera image to save.
		final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "TuneIn" + File.separator);
		root.mkdirs();
		final String fname = "img_"+ String.valueOf(System.currentTimeMillis()) + ".jpg";
		final File sdImageMainDirectory = new File(root, fname);
		outputFileUri = Uri.fromFile(sdImageMainDirectory);
		

		/*
		String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TuneIn/" + String.valueOf(System.currentTimeMillis()) + ".jpg";
		File file = new File(filePath);
		file.mkdirs();
		outputFileUri = Uri.fromFile(file);
		*/
		
		    // Camera.
		    final List<Intent> cameraIntents = new ArrayList<Intent>();
		    final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		    final PackageManager packageManager = act.getPackageManager();
		    final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
		    for(ResolveInfo res : listCam) {
		        final String packageName = res.activityInfo.packageName;
		        final Intent intent = new Intent(captureIntent);
		        intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
		        intent.setPackage(packageName);
		        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		        cameraIntents.add(intent);
		    }

		    // Filesystem.
		    final Intent galleryIntent = new Intent();
		    galleryIntent.setType("image/*");
		    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

		    // Chooser of filesystem options.
		    final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

		    // Add the camera options.
		    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

		    act.startActivityForResult(chooserIntent, SELECT_PICTURE_REQUEST_CODE);
			return outputFileUri;
		}
}
