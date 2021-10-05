package kim.nested.recyclerview;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;


public class playService extends Service {
    //StorageReference mStorageRef;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static playService playServiceInstance;
    public static MediaPlayer mediaPlayer = null;
    MediaSessionCompat mediaSessionCompat;
    NotificationManager notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", "n", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this, "1")
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
        sharedpreferences = getSharedPreferences("" + R.string.app_name, MODE_PRIVATE);
        editor = sharedpreferences.edit();
        playServiceInstance = this;
        mediaSessionCompat = new MediaSessionCompat(this, "My_Media_tag");
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        try {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case "playPause":
                        playpause(this);
                        break;
                    case "prev":
                        prev(this);
                        break;
                    case "next":
                        next(this);
                        break;
                    case "play":
                        play(this);
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }

    public void playpause(Context context) {
        if (mediaPlayer != null && sharedpreferences.getString("play", " ").equals(sharedpreferences.getString("playTopic", " "))) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                Intent Bintent = new Intent("UI");
                Bintent.putExtra("key", "playImg");
                LocalBroadcastManager.getInstance(this).sendBroadcast(Bintent);

                showNotification(context, true, R.drawable.ic_baseline_play_arrow_24);

            } else {
                mediaPlayer.start();
                Intent Bintent = new Intent("UI");
                Bintent.putExtra("key", "pauseImg");
                LocalBroadcastManager.getInstance(this).sendBroadcast(Bintent);

                showNotification(context, true, R.drawable.ic_baseline_pause_24);
            }

        } else {
            play(context);
        }
    }

    public void play(Context context) {

        Intent Bintent = new Intent("UI");
        Bintent.putExtra("key", "playImg");
        LocalBroadcastManager.getInstance(this).sendBroadcast(Bintent);

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        try {
            if(sharedpreferences.getString(sharedpreferences.getString("play", "/None/None/None"), "/None/None/None").contains("http")) {
                String[] splitStr = sharedpreferences.getString("play", "/None/None/None").split("/");
                //Log.i("ttt",""+ tutorials.indexOf(splitStr[2]));

                //Log.i("ttts",splitStr[0]+" "+splitStr[1]+" "+splitStr[2]);
                keepString("topic",splitStr[2]);
                keepString("subject",splitStr[0]+"/"+splitStr[1] );
                keepString("img",sharedpreferences.getString(sharedpreferences.getString("subject"," ")+"/image"," "));
                keepString("text", "preparing " +sharedpreferences.getString("topic"," ") );

                showNotification(context, false, R.drawable.ic_baseline_play_arrow_24);
                mediaPlayer = new MediaPlayer();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mediaPlayer.setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build()
                    );
                }
                mediaPlayer.setDataSource(sharedpreferences.getString(sharedpreferences.getString("play", "/None/None/None"), "/None/None/None"));
                mediaPlayer.prepareAsync();


                mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                    mediaPlayer.start();
                    //playingSubject = subject;
                    keepString("playTopic", sharedpreferences.getString("play", " "));

                    Bintent.putExtra("key", "PauseMax");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(Bintent);

                    keepString("text", sharedpreferences.getString("topic", " "));
                    showNotification(context, true, R.drawable.ic_baseline_pause_24);

                });
                mediaPlayer.setOnCompletionListener(mediaPlayer -> {

                    Bintent.putExtra("key", "playImg");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(Bintent);

                    showNotification(context, true, R.drawable.ic_baseline_play_arrow_24);

                });
                mediaPlayer.setOnErrorListener((mediaPlayer, i, i1) -> {

                    Bintent.putExtra("key", "playImg");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(Bintent);

                    keepString("text", "Try again " + sharedpreferences.getString("topic", ""));
                    showNotification(context, true, R.drawable.ic_baseline_play_arrow_24);

                    return false;
                });

            }

            } catch (IOException e) {
                keepString("text", e.getMessage());
            }

    }

    public void prev(Context context) {
        Set<String> set = sharedpreferences.getStringSet(sharedpreferences.getString("list"," "), null);
        if(set!=null) {
            ArrayList<String> tutorials = new ArrayList<>(set);
            Collections.sort(tutorials);
            String[] splitStr = sharedpreferences.getString("play", "/None/None/None").split("/");
            //Log.i("ttt", "" + tutorials.indexOf(splitStr[2]));
            int prev = tutorials.indexOf(splitStr[2]) - 1;
            if (prev > -1) {
                //keepInt("spinner2", prev);
                keepString("play", splitStr[0] + "/" + splitStr[1] + "/" + tutorials.get(prev));
                play(context);
            }
        }
    }

    public void next(Context context) {
        Set<String> set = sharedpreferences.getStringSet(sharedpreferences.getString("list"," "), null);
        if(set!=null) {
            ArrayList<String> tutorials = new ArrayList<>(set);
            Collections.sort(tutorials);
            String[] splitStr = sharedpreferences.getString("play", "/None/None/None").split("/");
            //Log.i("ttt", "" + tutorials.indexOf(splitStr[2]));

            int next = tutorials.indexOf(splitStr[2]) + 1;
            if (next < tutorials.size()) {
                keepString("play", splitStr[0] + "/" + splitStr[1] + "/" + tutorials.get(next));
                play(context);
            }
        }
    }

    Bitmap img ;

    @SuppressLint("UnspecifiedImmutableFlag")
    public void showNotification(Context context, boolean showButtons, int playPause) {
        Intent notificationIntent = new Intent(context, MainActivity2.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //subject = sharedpreferences.getString("subject", " ");
        if (!showButtons) {

            img = null;

            Notification notification = new NotificationCompat.Builder(context, "1")
                    .setSmallIcon(android.R.drawable.stat_sys_headset)
                    .setContentTitle(sharedpreferences.getString("subject","None"))
                    .setContentText(sharedpreferences.getString("text", " "))
                    .setContentIntent(contentIntent)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setOnlyAlertOnce(true)
                    .build();

            notificationManager.notify(1, notification);


            Picasso.get().load(sharedpreferences.getString("img"," ")).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    try {
                        img = bitmap;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    // place holder to set here
                }
            });


        } else {

            Intent playI, prevI, nextI;
            PendingIntent playPI, prevPI, nextPI;
            playI = new Intent(context, playService.class).setAction("playPause");
            prevI = new Intent(context, playService.class).setAction("prev");
            nextI = new Intent(context, playService.class).setAction("next");

            playPI = PendingIntent.getService(context, 2, playI, PendingIntent.FLAG_UPDATE_CURRENT);
            prevPI = PendingIntent.getService(context, 3, prevI, PendingIntent.FLAG_UPDATE_CURRENT);
            nextPI = PendingIntent.getService(context, 4, nextI, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = null;
            notification = new NotificationCompat.Builder(context, "1")
                    .setSmallIcon(android.R.drawable.stat_sys_headset)
                    .setLargeIcon(img)
                    .setContentTitle(sharedpreferences.getString("subject","None"))
                    .setContentText(sharedpreferences.getString("text", " "))
                    .setContentIntent(contentIntent)
                    .addAction(R.drawable.ic_baseline_skip_previous_24, "prev", prevPI)
                    .addAction(playPause, "play", playPI)
                    .addAction(R.drawable.ic_baseline_skip_next_24, "next", nextPI)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setOnlyAlertOnce(true)
                    .build();

            notificationManager.notify(1, notification);

        }

    }

    public static playService getInstance() {
        return playServiceInstance;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
        } else {
            notificationManager.cancel(1);
        }
        stopSelf();

    }

    private void keepInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    private void keepString(String keyStr1, String valueStr1) {
        editor.putString(keyStr1, valueStr1);
        editor.apply();
    }

    private void keepBool(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

}
