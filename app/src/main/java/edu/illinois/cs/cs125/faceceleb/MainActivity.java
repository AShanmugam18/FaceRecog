package edu.illinois.cs.cs125.faceceleb;

import com.microsoft.projectoxford.face.*;
import com.microsoft.projectoxford.face.contract.*;
import java.io.*;
import android.os.*;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private FaceServiceClient faceServiceClient = new FaceServiceRestClient("your API endpoint", "<Subscription Key>");

    /** Default logging tag for messages from the main activity. */
    private static final String TAG = "My App";

    /** Constant to request an image capture. */
    private static final int IMAGE_CAPTURE_REQUEST_CODE = 1;

//    /** Constant to perform a read file request. */
//    private static final int READ_REQUEST_CODE = 42;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("How Old Do You Look?");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageButton cameraButton = findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "Camera button clicked");
                startCamera();
            }
        });

//        final ImageButton openFile = findViewById(R.id.openFile);
//        openFile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                Log.d(TAG, "Open file button clicked");
//                startOpenFile();
//            }
//        });

    }

    /**
     * Turns on the Camera
     */
    private void startCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, IMAGE_CAPTURE_REQUEST_CODE);
    }

//    /**
//     * Start an open file dialog to look for image files.
//     */
//    private void startOpenFile() {
//        Intent openFileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        openFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
//        openFileIntent.setType("image/*");
//        startActivityForResult(openFileIntent, READ_REQUEST_CODE);
//    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
                                 final Intent resultData) {
        if (resultCode == RESULT_OK) {
            ImageView photoView;
            if (requestCode == IMAGE_CAPTURE_REQUEST_CODE) {
                Bundle extras = resultData.getExtras();
                Bitmap userPhoto = (Bitmap) extras.get("data");
                photoView = findViewById(R.id.imageView);
                if (userPhoto != null) {
                    photoView.setImageBitmap(userPhoto);
                }
                Face[] result = findFace(userPhoto);
                if (result != null) {
                    Toast.makeText(getApplicationContext(), "You Look " + result.toString() + " Years Old", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "No Face Detected", Toast.LENGTH_LONG).show();
                }
                /** Most of it is done. After taking a picture with the camera, the photo will be stored in the "Bitmap photo".
                 *
                 *  What's left to do is:
                 *
                 *  - To give the Bitmap the the API (maybe convert it into a jpeg if needed)
                 *  - Tell the API to do wtv it needs, and get it to return a the photo of the celebrity
                 *  - Convert the photo into either
                 *      - a Uri, and set the Uri to photoURi in CODE A
                 *    or
                 *      - a Bitmap, and set the Bitmap to filePhoto in CODE B
                 */

                /*String[] photos = null;
                AssetManager mngr = getAssets();
                try {
                    photos = mngr.list("faces");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    for (int i = 0; i < photos.length; i++) {
                            InputStream currPhoto = mngr.open("faces" + "/" + photos[i] + ".jpg");
                            Bitmap currPhotoJPG = BitmapFactory.decodeStream(currPhoto);
                            ByteArrayOutputStream currStream = new ByteArrayOutputStream();
                            currPhotoJPG.compress(Bitmap.CompressFormat.JPEG, 100, currStream);
                    }
                } catch (Exception e) {
                    e.printStackTrace();*/
                }
                /**    CODE A - only remove this and others

                 Uri photoUri = _____________________
                 try {
                 filePhoto = MediaStore.Images.Media.getBitmap(
                 this.getContentResolver(), photoUri);
                 } catch (IOException e) {
                 return;
                 }

                 /**   CODE B - only remove this and others

                 filePhoto = ___________________

                 */     // only remove this and others
            }

//            else if (requestCode == READ_REQUEST_CODE) {
//                Bitmap filePhoto;
//                Uri photoUri = resultData.getData();
//                try {
//                    filePhoto = MediaStore.Images.Media.getBitmap(
//                            this.getContentResolver(), photoUri);
//                } catch (IOException e) {
//                    return;
//                }
//
//                photoView = findViewById(R.id.photoView);
//                photoView.setImageBitmap(filePhoto);
//            }
        }

    private Face[] findFace(final Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(outputStream.toByteArray());
        AsyncTask<InputStream, String, Face[]> detectTask =
                new AsyncTask<InputStream, String, Face[]>() {
                    @Override
                    protected Face[] doInBackground(InputStream... params) {
                        try {
                            Face[] result = faceServiceClient.detect(
                                    params[0],
                                    false,
                                    false,
                                    new FaceServiceClient.FaceAttributeType[]{FaceServiceClient.FaceAttributeType.Age}
                            );
                            if (result == null) {
                                return null;
                            }
                                return result;
                        } catch (Exception e) {
                            return null;
                        }
                    }
                };
        return null;
        }
    }
