package com.example.camerasample;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {

	//Camera object
	Camera mCamera;
	//Preview surface
	SurfaceView surfaceView;
	//Preview surface handle for callback
	SurfaceHolder surfaceHolder;
	//Camera button
	Button btnCapture;
	//Note if preview windows is on.
	boolean previewing;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnCapture = (Button) findViewById(R.id.btn_capture);
		btnCapture.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				if (previewing)
					mCamera.takePicture(shutterCallback, rawPictureCallback,
							jpegPictureCallback);				
			}
		});
		
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(new SurfaceViewCallback());
		//surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	ShutterCallback shutterCallback = new ShutterCallback() {
		@Override
		public void onShutter() {
		}
	};	
	
	PictureCallback rawPictureCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) {

		}
	};
	
	PictureCallback jpegPictureCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) {

			String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
					.toString()
					+ File.separator
					+ "PicTest_" + System.currentTimeMillis() + ".jpg";
			File file = new File(fileName);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdir();
			}
			
			try {
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(file));
				bos.write(arg0);
				bos.flush();
				bos.close();		
				scanFileToPhotoAlbum(file.getAbsolutePath());
				Toast.makeText(MainActivity.this, "[Test] Photo take and store in" + file.toString(),Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				Toast.makeText(MainActivity.this, "Picture Failed" + e.toString(),
						Toast.LENGTH_LONG).show();
			}
		};
	};
	
	public void scanFileToPhotoAlbum(String path) {

        MediaScannerConnection.scanFile(MainActivity.this,
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }
	private final class SurfaceViewCallback implements android.view.SurfaceHolder.Callback {   
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) 
		{
			if (previewing) {
				mCamera.stopPreview();
				previewing = false;
			}
	
			try {
				mCamera.setPreviewDisplay(arg0);
				mCamera.startPreview();
				previewing = true;
			} catch (Exception e) {}
		}
		public void surfaceCreated(SurfaceHolder holder) {
				mCamera = Camera.open();
				// get Camera parameters
				Camera.Parameters params = mCamera.getParameters();

				List<String> focusModes = params.getSupportedFocusModes();
				if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
				  // Autofocus mode is supported
				}
			}

			public void surfaceDestroyed(SurfaceHolder holder) {
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
				previewing = false;
			}
	}

}
