package cs.ualberta.ca.tunein;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * View
 * TopicListActivity Class:
 * This is part of the view of displaying the topic list
 * of comments. This also includes the creation view of creating
 * topic comments through a dialog box.
 */
public class TopicListActivity extends Activity {

	//comment view adapter
	private CommentViewAdapter viewAdapter;
	//discussion thread list
	private ThreadList threadList;
	//variables for adding topic
	private String title;
	private String comment;
	private Image img;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    threadList = new ThreadList();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		//setup the comment listview
		viewAdapter = new CommentViewAdapter(TopicListActivity.this, threadList.getDiscussionThread());
		setContentView(R.layout.topic_list_view);
		ListView listview = (ListView) findViewById(R.id.listViewTopics);
		
		//setup adapter
		threadList.setAdapter(viewAdapter);
		listview.setAdapter(viewAdapter);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * This method is to open the create topic comment dialog box
	 * and create a comment and add to the topic list.
	 * @param v The view passed in.
	 */
	public void createCommentClick(View v)
	{
		LayoutInflater inflater = LayoutInflater.from(TopicListActivity.this);
		final View createView = inflater.inflate(R.layout.create_comment_view, null);

		final TextView inputTitle = (EditText) createView.findViewById(R.id.textViewInputTitle);
		final TextView inputComment = (EditText) createView.findViewById(R.id.editTextComment);
		final ImageView inputImage = (ImageView) createView.findViewById(R.id.imageViewUpload);
		
		AlertDialog dialog = new AlertDialog.Builder(TopicListActivity.this)
		    .setTitle("Create Comment")
		    .setView(createView)
		    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		            title = inputTitle.getText().toString();
		            comment = inputComment.getText().toString();
		            
		            //create comment with image 
		            if (inputImage.getVisibility() == View.VISIBLE) 
		            {
		            	inputImage.buildDrawingCache();
		            	Bitmap bmp = inputImage.getDrawingCache();
		            	img = new Image(bmp);
	            	
		        		//temp geo location
		            	String username = ((User)getApplication()).getName();
		            	String id = ((User) getApplication()).getUniqueID();
		        		Commenter user = new Commenter(username, id);
		        		GeoLocation loc = new GeoLocation(5, 10);
		        		
		        		ThreadController cntrl = new ThreadController(threadList);
		        		Comment newComment  = new Comment(user, title, comment, loc, img);
		        		cntrl.createTopic(newComment);
		        		
		        		viewAdapter.updateThreadView(threadList);
		            } 
		            else 
		            {	                
		            	//temp geo location
		            	String username = ((User)getApplication()).getName();
		            	String id = ((User) getApplication()).getUniqueID();
		        		Commenter user = new Commenter(username, id);
		        		GeoLocation loc = new GeoLocation(5, 10);
		        		
		        		ThreadController cntrl = new ThreadController(threadList);
		        		Comment newComment  = new Comment(user, title, comment, loc);
		        		cntrl.createTopic(newComment);

		        		viewAdapter.updateThreadView(threadList);
		            }
		        }
		    })
		    .setNegativeButton("Cancel", null).create();
		dialog.show();
	}

}
