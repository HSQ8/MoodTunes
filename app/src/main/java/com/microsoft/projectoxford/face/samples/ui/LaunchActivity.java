package com.microsoft.projectoxford.face.samples.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.microsoft.projectoxford.face.samples.R;

public class LaunchActivity extends AppCompatActivity {
    String _playlistID;
    String _playlistMessage;
    String _playlistName;
    String _emotion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        _playlistID = getIntent().getStringExtra(MainActivity.PLAY_LIST_ID_ID);
        _playlistMessage = getIntent().getStringExtra(MainActivity.PLAY_LIST_MESSAGE_ID);
        _playlistName = getIntent().getStringExtra(MainActivity.PLAY_LIST_NAME_ID);
        _emotion = getIntent().getStringExtra(MainActivity.PLAY_LIST_EMOTION_ID);

        TextView MessagetextView = (TextView) findViewById(R.id.textViewMessage);
        TextView EmotiontextView = (TextView) findViewById(R.id.emotion);
        TextView MusictextView = (TextView) findViewById(R.id.albumMessage);

        MessagetextView.setText("We detect that you feel\n");
        EmotiontextView.setText(_emotion);
        MusictextView.setText("We reommend " + _playlistName + ", \n" + _playlistMessage);

    }
    public void launch(View v){
        String url = "https://open.spotify.com/user/spotify/playlist/" + _playlistID;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
