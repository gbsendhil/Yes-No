package com.sendhil.yesno.activity;

import com.google.gson.Gson;

import com.sendhil.yesno.network.BasicImageDownloader;
import com.sendhil.yesno.R;
import com.sendhil.yesno.pojo.Response;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class YesNoActivity extends Activity implements OnClickListener {

	private static final String TAG = YesNoActivity.class.getSimpleName();
	private GifImageView mGifImageView = null;
	private ImageButton mImageButton = null;
	private LinearLayout mLinearLayout = null;
	private ImageView mImageView  =null;
	private AnimationSet animationSet = null;
	private TextView mTextView = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		mImageButton = (ImageButton) findViewById(R.id.my_button);
		mImageButton.setOnClickListener(this);
		mGifImageView = ((GifImageView)findViewById(R.id.gifImageView));
		mLinearLayout = (LinearLayout) findViewById(R.id.loadingLayout);
		mImageView = (ImageView) findViewById(R.id.loadingDrawable);
		mTextView = (TextView) findViewById(R.id.loadingText);

		animationSet = new AnimationSet(true);


		ScaleAnimation fade_in =  new ScaleAnimation(2f, 0.75f, 2f, 0.75f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		fade_in.setDuration(5000);
		fade_in.setRepeatCount(Animation.INFINITE);

		ScaleAnimation fade_out =  new ScaleAnimation(0.75f, 2f, 0.75f, 2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		fade_out.setDuration(5000);
		fade_out.setRepeatCount(Animation.INFINITE);


		animationSet.addAnimation(fade_in);
		animationSet.addAnimation(fade_out);

		mImageButton.startAnimation(animationSet);
	}

	@Override
	public void onClick(View arg0) {

		toggleQuestion(false);

		new LongRunningGetIO().execute();
	}

	private class LongRunningGetIO extends AsyncTask<Void, Void, Response> {


		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mLinearLayout.setVisibility(View.VISIBLE);
			mImageView.setBackgroundResource(R.drawable.animation_progress);
			AnimationDrawable frameAnimation = (AnimationDrawable) mImageView.getBackground();
			frameAnimation.start();
		}


		@Override
		protected Response doInBackground(Void... params) {
			Response response = null;
			try {

				String responseString = "", data = "";

				URL url = new URL("http://yesno.wtf/api/");
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
				while ((data = reader.readLine()) != null) {
					responseString += data + "\n";
				}

				Gson gson = new Gson();
				response = gson.fromJson(responseString, Response.class);

				BasicImageDownloader basicImageDownloader = new BasicImageDownloader(new BasicImageDownloader.OnImageLoaderListener() {
					@Override
					public void onError(BasicImageDownloader.ImageError error) {

					}

					@Override
					public void onProgressChange(int percent) {

					}

					@Override
					public void onComplete(GifDrawable result) {
						mLinearLayout.setVisibility(View.GONE);
						mGifImageView.setImageDrawable(result);
						mImageButton.setClickable(true);
						mImageButton.setVisibility(View.GONE);
						mGifImageView.setVisibility(View.VISIBLE);
						mLinearLayout.setVisibility(View.GONE);
					}
				});

				if (null != response)
					basicImageDownloader.download(response.getImage(), false);
			} catch (MalformedURLException m) {
				m.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			return response;
		}

		@Override
		protected void onPostExecute(Response response) {
			super.onPostExecute(response);

			if(null==response){
				showNetworkError();
			}
		}
	}


	@Override
	public void onBackPressed() {
		if(mImageButton.getVisibility()==View.VISIBLE){
			super.onBackPressed();
		}else {
			toggleQuestion(true);
			mLinearLayout.setVisibility(View.GONE);
			mGifImageView.setVisibility(View.GONE);
		}
	}

	private void toggleQuestion(boolean isEnabled){
		if(isEnabled){
			mImageButton.setClickable(true);
			mImageButton.setVisibility(View.VISIBLE);
			mImageButton.startAnimation(animationSet);
		}else{
			mImageButton.setClickable(false);
			mImageButton.setVisibility(View.GONE);
			mImageButton.setAnimation(null);
		}
	}

	private void showNetworkError(){

		mTextView.setText(R.string.loading_text_error);
	}
}