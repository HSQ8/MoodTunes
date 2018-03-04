//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/Cognitive-Face-Android
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package com.microsoft.projectoxford.face.samples.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Emotion;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.samples.R;
import com.microsoft.projectoxford.face.samples.helper.ImageHelper;
import com.microsoft.projectoxford.face.samples.helper.SampleApp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {private class DetectionTask extends AsyncTask<InputStream, String, Face[]> {
    private boolean mSucceed = true;


    @Override
    protected Face[] doInBackground(InputStream... params) {
        // Get an instance of face service client to detect faces in image.
        FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
        try {
            publishProgress("Detecting...");

            // Start detection.
            return faceServiceClient.detect(
                    params[0],  /* Input stream of image to detect */
                    true,       /* Whether to return face ID */
                    true,       /* Whether to return face landmarks */
                        /* Which face attributes to analyze, currently we support:
                           age,gender,headPose,smile,facialHair */
                    new FaceServiceClient.FaceAttributeType[] {
                            FaceServiceClient.FaceAttributeType.Age,
                            FaceServiceClient.FaceAttributeType.Gender,
                            FaceServiceClient.FaceAttributeType.Smile,
                            FaceServiceClient.FaceAttributeType.Glasses,
                            FaceServiceClient.FaceAttributeType.FacialHair,
                            FaceServiceClient.FaceAttributeType.Emotion,
                            FaceServiceClient.FaceAttributeType.HeadPose,
                            FaceServiceClient.FaceAttributeType.Accessories,
                            FaceServiceClient.FaceAttributeType.Blur,
                            FaceServiceClient.FaceAttributeType.Exposure,
                            FaceServiceClient.FaceAttributeType.Hair,
                            FaceServiceClient.FaceAttributeType.Makeup,
                            FaceServiceClient.FaceAttributeType.Noise,
                            FaceServiceClient.FaceAttributeType.Occlusion
                    });
        } catch (Exception e) {
            mSucceed = false;
            publishProgress(e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog.show();
        populateArrayLists();
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        mProgressDialog.setMessage(progress[0]);

    }

    @Override
    protected void onPostExecute(Face[] result) {
        SendRequestAfterDetection(result,mSucceed);
    }


}

    private void SendRequestAfterDetection(Face[] result, boolean _Succeed){
        mProgressDialog.dismiss();
        setAllButtonsEnabledStatus(true);
        setDetectButtonEnabledStatus(false);
        if (_Succeed) {

            String detectionResult;
            if(result != null) {
                Face overallface = ConcatenateFaces(result);
                String emotion = getEmotion(overallface.faceAttributes.emotion);
                makeSongRequest(m_playlistID, m_PlaylistName, m_PlaylistMessage,emotion);
                detectionResult = "success";
            } else {
                detectionResult = "0 face detected";
            }
        }
        mImageUri = null;
        mBitmap = null;
    }

    public static final String PLAY_LIST_NAME_ID = "PLAY_LIST_NAME_ID";
    public static final String PLAY_LIST_ID_ID = "PLAY_LIST_ID_ID";
    public static final String PLAY_LIST_MESSAGE_ID = "PLAY_LIST_MESSAGE_ID";
    public static final String PLAY_LIST_EMOTION_ID = "PLAY_LIST_EMOTION_ID";


    private void makeSongRequest(String _playlistID,String _PlaylistName, String _PlaylistMessage, String emotion){

        Intent intent = new Intent(getBaseContext(), LaunchActivity.class);
        intent.putExtra(PLAY_LIST_NAME_ID, _PlaylistName);
        intent.putExtra(PLAY_LIST_ID_ID, _playlistID);
        intent.putExtra(PLAY_LIST_MESSAGE_ID, _PlaylistMessage);
        intent.putExtra(PLAY_LIST_EMOTION_ID, emotion);
        startActivity(intent);

    }

    private Face ConcatenateFaces(Face[] faces){
        Face finalface = faces[0];
        for(int i = 1; i < faces.length; ++i){
            finalface.faceAttributes.emotion.anger += faces[i].faceAttributes.emotion.anger;
            finalface.faceAttributes.emotion.anger += faces[i].faceAttributes.emotion.contempt;
            finalface.faceAttributes.emotion.anger += faces[i].faceAttributes.emotion.disgust;
            finalface.faceAttributes.emotion.anger += faces[i].faceAttributes.emotion.fear;
            finalface.faceAttributes.emotion.anger += faces[i].faceAttributes.emotion.happiness;
            finalface.faceAttributes.emotion.anger += faces[i].faceAttributes.emotion.neutral;
            finalface.faceAttributes.emotion.anger += faces[i].faceAttributes.emotion.sadness;
            finalface.faceAttributes.emotion.anger += faces[i].faceAttributes.emotion.surprise;
        }
        return finalface;
    }
    private HashMap<String,String> anxiousFearPlaylists = new HashMap<String, String>();
    private HashMap<String,String> happyPlaylists = new HashMap<String, String>();
    private HashMap<String,String> sadPlaylists = new HashMap<String, String>();
    private HashMap<String,String> angerPlaylists = new HashMap<String, String>();
    private String m_PlaylistName;
    private String m_playlistID;
    private String m_PlaylistMessage = "This is what your emotions sound like.";


    private String pickRandomPlaylist(HashMap<String, String> map){
        List<String> keysAsArray = new ArrayList<String>(map.keySet());
        Random r = new Random();
        return keysAsArray.get(r.nextInt(map.size()));
    }
    private void populateArrayLists() {
        anxiousFearPlaylists.put("37i9dQZF1DX4sWSpwq3LiO"  ,"Peaceful Piano"     );
        anxiousFearPlaylists.put("37i9dQZF1DX3Ogo9pFvBkY"  ,"Ambient Chill"     );
        anxiousFearPlaylists.put("37i9dQZF1DXcCnTAt8CfNe"  ,"Musical Therapy"     );
        anxiousFearPlaylists.put("7A2YimOfIrmAWkCeSIY8Rq"  ,"Calm Classics"     );
        anxiousFearPlaylists.put("37i9dQZF1DWU0ScTcjJBdj"  ,"Relax & Unwind"     );
        anxiousFearPlaylists.put("37i9dQZF1DX3PIPIT6lEg5"  ,"Microtherapy"     );
        anxiousFearPlaylists.put("37i9dQZF1DX1s9knjP51Oa"  ,"Calm Vibes"     );
        anxiousFearPlaylists.put("37i9dQZF1DXa9xHlDa5fc6"  ,"License to Chill"     );
        anxiousFearPlaylists.put("37i9dQZF1DWTkxQvqMy4WW"  ,"Chillin' on a Dirt Road"     );
        anxiousFearPlaylists.put("37i9dQZF1DX8ymr6UES7vc"  ,"Rain Sounds"     );
        anxiousFearPlaylists.put("37i9dQZF1DWZqd5JICZI0u"  ,"Peaceful Meditation"     );

        happyPlaylists.put("37i9dQZF1DX3rxVfibe1L0","Mood Booster");
        happyPlaylists.put("37i9dQZF1DX7KNKjOK0o75","Have a Great Day!");
        happyPlaylists.put("37i9dQZF1DWYBO1MoTDhZI","Good Vibes");
        happyPlaylists.put("37i9dQZF1DXdPec7aLTmlC","Happy Hits");
        happyPlaylists.put("37i9dQZF1DWSkMjlBZAZ07","Happy Folk");
        happyPlaylists.put("37i9dQZF1DX9XIFQuFvzM4","Feelin' Good");
        happyPlaylists.put("37i9dQZF1DX2sUQwD7tbmL","Feel-Good Indie Rock");
        happyPlaylists.put("37i9dQZF1DX0UrRvztWcAU","Wake Up Happy");
        happyPlaylists.put("37i9dQZF1DXaK0O81Xtkis","Happy Chill Good Time Vibes");
        happyPlaylists.put("37i9dQZF1DWSf2RDTDayIx","Happy Beats");

        sadPlaylists.put("37i9dQZF1DX3YSRoSdA634","Life Sucks");
        sadPlaylists.put("37i9dQZF1DWSqBruwoIXkA","Down in the Dumps");
        sadPlaylists.put("37i9dQZF1DWVV27DiNWxkR","Melancholia");

        angerPlaylists.put("37i9dQZF1DWU6kYEHaDaGA","Unleash the Fury");
        angerPlaylists.put("37i9dQZF1DWWJOmJ7nRx0C","Rock Hard");
        angerPlaylists.put("5s7Sp5OZsw981I2OkQmyrz","Rage Quit");
        angerPlaylists.put("37i9dQZF1DWTcqUzwhNmKv","Kickass Metal");
        angerPlaylists.put("37i9dQZF1DWXIcbzpLauPS","Metalcore");

    }

    private String getEmotion(Emotion emotion)
    {
        String emotionType = "";
        double emotionValue = 0.0;
        if (emotion.anger > emotionValue)
        {
            emotionValue = emotion.anger;
            emotionType = "Anger";
            m_PlaylistMessage = "Let out some of your rage with this pick: ";
            m_playlistID = pickRandomPlaylist(angerPlaylists);
            m_PlaylistName = angerPlaylists.get(m_playlistID);
        }
        if (emotion.contempt > emotionValue)
        {
            emotionValue = emotion.contempt;
            emotionType = "Contempt";
            m_PlaylistMessage = "Show everyone your contempt: ";
            m_playlistID = pickRandomPlaylist(angerPlaylists);
            m_PlaylistName = angerPlaylists.get(m_playlistID);
        }
        if (emotion.disgust > emotionValue)
        {
            emotionValue = emotion.disgust;
            emotionType = "Disgust";
            m_PlaylistMessage = "Get disgusted with this: ";
            m_playlistID = pickRandomPlaylist(angerPlaylists);
            m_PlaylistName = angerPlaylists.get(m_playlistID);
        }
        if (emotion.fear > emotionValue)
        {
            emotionValue = emotion.fear;
            emotionType = "Fear";
            m_PlaylistMessage = "Fear not, listen to these chill beats: ";
            m_playlistID = pickRandomPlaylist(anxiousFearPlaylists);
            m_PlaylistName = anxiousFearPlaylists.get(m_playlistID);
        }
        if (emotion.happiness > emotionValue)
        {
            emotionValue = emotion.happiness;
            emotionType = "Happiness";
            m_PlaylistMessage = "Heck yeah let's keep the happy feelings going; ";
            m_playlistID = pickRandomPlaylist(happyPlaylists);
            m_PlaylistName = happyPlaylists.get(m_playlistID);
        }
        if (emotion.neutral > emotionValue)
        {
            emotionValue = emotion.neutral;
            emotionType = "Neutral";
            m_PlaylistMessage = "Neutral feelings call for uplifting beats: ";
            m_playlistID = pickRandomPlaylist(happyPlaylists);
            m_PlaylistName = happyPlaylists.get(m_playlistID);
        }
        if (emotion.sadness > emotionValue)
        {
            emotionValue = emotion.sadness;
            emotionType = "Sadness";
            m_PlaylistMessage = "Sometimes, spilled milk is worth crying for: ";
            m_playlistID = pickRandomPlaylist(sadPlaylists);
            m_PlaylistName = sadPlaylists.get(m_playlistID);
        }
        if (emotion.surprise > emotionValue)
        {
            emotionValue = emotion.surprise;
            emotionType = "Surprise";
            m_PlaylistMessage = "Come down from your surprise with these mellow vibes: ";
            m_playlistID = pickRandomPlaylist(anxiousFearPlaylists);
            m_PlaylistName = anxiousFearPlaylists.get(m_playlistID);

        }
        return emotionType;
    }



    // Flag to indicate which task is to be performed.
    private static final int REQUEST_SELECT_IMAGE = 0;

    // The URI of the image selected to detect.
    private Uri mImageUri;

    // The image selected to detect.
    private Bitmap mBitmap;

    // Progress dialog popped up when communicating with server.
    ProgressDialog mProgressDialog;

    // When the activity is created, set all the member variables to initial state.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getString(R.string.progress_dialog_title));

        // Disable button "detect" as the image to detect is not selected.
        setDetectButtonEnabledStatus(false);

    }

    // Save the activity state when it's going to stop.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("ImageUri", mImageUri);
    }

    // Recover the saved state when the activity is recreated.
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mImageUri = savedInstanceState.getParcelable("ImageUri");
        if (mImageUri != null) {
            mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                    mImageUri, getContentResolver());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT_IMAGE:
                if (resultCode == RESULT_OK) {

                    mImageUri = data.getData();
                    mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            mImageUri, getContentResolver());
                    if (mBitmap != null) {

                        ImageView imageView = (ImageView) findViewById(R.id.image);
                        imageView.setImageBitmap(mBitmap);
                    }
                    setDetectButtonEnabledStatus(true);
                }
                break;
            default:
                break;
        }
    }

    public void selectImage(View view) {
        Intent intent = new Intent(this, SelectImageActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }
// Microsoft's boilerplate code below
    // Called when the "Detect" button is clicked.
    public void detect(View view) {
        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        // Start a background task to detect faces in the image.
        new MainActivity.DetectionTask().execute(inputStream);

        // Prevent button click during detecting.
        setAllButtonsEnabledStatus(false);
    }

    // Set whether the buttons are enabled.
    private void setDetectButtonEnabledStatus(boolean isEnabled) {
        Button detectButton = (Button) findViewById(R.id.detect);
        detectButton.setEnabled(isEnabled);
    }

    // Set whether the buttons are enabled.
    private void setAllButtonsEnabledStatus(boolean isEnabled) {
        Button selectImageButton = (Button) findViewById(R.id.select_image);
        selectImageButton.setEnabled(isEnabled);

        Button detectButton = (Button) findViewById(R.id.detect);
        detectButton.setEnabled(isEnabled);

        //Button ViewLogButton = (Button) findViewById(R.id.view_log);
        //ViewLogButton.setEnabled(isEnabled);
    }


}
