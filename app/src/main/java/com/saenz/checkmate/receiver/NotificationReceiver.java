package com.saenz.checkmate.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.saenz.checkmate.R;
import com.saenz.checkmate.notification.NotificationScheduler;

import java.util.Random;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "checkmate_channel";

    // Mensajes motivacionales aleatorios
    private static final String[] MESSAGES = {
            "♟ Un gran jugador practica todos los días. ¿Listo para tu partida?",
            "♟ El ajedrez es un gimnasio para la mente. ¡Entrena hoy!",
            "♟ Cada partida te hace mejor. Abre Checkmate y demuéstralo.",
            "♟ Los grandes campeones no descansan. ¡Tu tablero te espera!",
            "♟ Una partida al día mantiene la derrota alejada."
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        // Seleccionar mensaje aleatorio
        String message = MESSAGES[new Random().nextInt(MESSAGES.length)];

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Crear canal (obligatorio en Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Recordatorio de práctica",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notificaciones motivacionales de Checkmate");
            manager.createNotificationChannel(channel);
        }
        var idAlarma =intent.getIntExtra("IdAlarma1",0);
        // Construir y mostrar la notificación
        NotificationCompat.Builder builder= createBuilder(context, MESSAGES[new Random().nextInt(MESSAGES.length)]);
        manager.notify(idAlarma, builder.build());
    }

    private NotificationCompat.Builder createBuilder (Context context, String message){

        NotificationCompat.Builder returnBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("¡Hora de jugar ajedrez!")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        return returnBuilder;
    }
}