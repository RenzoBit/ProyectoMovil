package pe.edu.tecsup.proyectomovil.service;

import com.google.android.gms.gcm.GcmListenerService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import pe.edu.tecsup.proyectomovil.PrincipalActivity;

/**
 * Created by Renzo on 08/07/2016.
 */
public class GCMIntentService extends GcmListenerService {

    public GCMIntentService() {

    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String texto = data.getString("message");
        sendNotification(texto);
    }

    @Override
    public void onDeletedMessages() {
        sendNotification("Deleted messages on server");
    }

    @Override
    public void onMessageSent(String msgId) {
        sendNotification("Upstream message sent. Id=" + msgId);
    }

    @Override
    public void onSendError(String msgId, String error) {
        sendNotification("Upstream message send error. Id=" + msgId + ", error" + error);
    }

    private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(android.R.drawable.star_big_on).setContentTitle("Proyecto Móvil").setContentText(msg);

        mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        mBuilder.setLights(Color.RED, 3000, 3000);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mBuilder.setAutoCancel(true);

        Intent notIntent = new Intent(this, PrincipalActivity.class);
        PendingIntent contIntent = PendingIntent.getActivity(this, 0, notIntent, 0);
        mBuilder.setContentIntent(contIntent);
        mNotificationManager.notify(1, mBuilder.build());
    }

}
