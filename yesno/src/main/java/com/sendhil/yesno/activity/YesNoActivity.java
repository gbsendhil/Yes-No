package com.sendhil.yesno.activity;

import com.google.gson.Gson;

import com.sendhil.yesno.network.BasicImageDownloader;
import com.sendhil.yesno.R;
import com.sendhil.yesno.pojo.Response;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		mImageButton = (ImageButton) findViewById(R.id.my_button);
		mImageButton.setOnClickListener(this);
		mGifImageView = ((GifImageView)findViewById(R.id.gifImageView));


		AnimationSet animationSet = new AnimationSet(true);


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
		ImageButton b = (ImageButton) findViewById(R.id.my_button);
		b.setClickable(false);
		new LongRunningGetIO().execute();
	}

	private class LongRunningGetIO extends AsyncTask<Void, Void, Void> {

		ProgressDialog mProgressDialog = new ProgressDialog(YesNoActivity.this);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.setMessage(getString(R.string.loading_text));
			mProgressDialog.show();
		}


		@Override
		protected Void doInBackground(Void... params) {

			try {
				Response response = null;
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
						mProgressDialog.dismiss();
						mGifImageView.setImageDrawable(result);
					}
				});

				if (null != response)
					basicImageDownloader.download(response.getImage(), false);
			} catch (MalformedURLException m) {
				m.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);

			mImageButton.setClickable(true);
			mImageButton.setVisibility(View.GONE);
			mGifImageView.setVisibility(View.VISIBLE);
			mProgressDialog.dismiss();
		}
	}

	@Override
	public void onBackPressed() {
		if(mImageButton.getVisibility()==View.VISIBLE){
			super.onBackPressed();
		}else {
			mImageButton.setVisibility(View.VISIBLE);
			mGifImageView.setVisibility(View.GONE);
		}
	}
}