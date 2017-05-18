package com.nainaiwang.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.ImageView;
//异步获取图片
public class CanvasImageTask extends AsyncTask<ImageView, Void, Bitmap> {
	private ImageView gView;

	protected Bitmap doInBackground(ImageView... views) {
		Bitmap bmp = null;
		ImageView view = views[0];
		if (view.getTag() != null) try {
			if (URLUtil.isHttpUrl(view.getTag().toString())) {
				URL url = new URL(view.getTag().toString());
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream stream = conn.getInputStream();
				bmp = BitmapFactory.decodeStream(stream);
				stream.close();
			} else {
				bmp = BitmapFactory.decodeFile(view.getTag().toString());
			}
		} catch (Exception e) {
			Log.v("img", e.getMessage());
			return null;
		}
		this.gView = view;
		return bmp;
	}

	protected void onPostExecute(Bitmap bm) {
		if (bm != null) {
			this.gView.setImageBitmap(bm);
			this.gView = null;
		}
	}

}