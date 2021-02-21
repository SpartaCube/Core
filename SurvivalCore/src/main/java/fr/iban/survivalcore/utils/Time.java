package fr.iban.survivalcore.utils;

import java.util.concurrent.TimeUnit;

public class Time {
	
	public static String calculateTime(long secondes) {
        int days = (int) TimeUnit.SECONDS.toDays(secondes);
        long hours = TimeUnit.SECONDS.toHours(secondes)%24;
        long minutes = TimeUnit.SECONDS.toMinutes(secondes)%60;
        long seconds = secondes%60;

        StringBuilder sb = new StringBuilder();

        if(days > 0) {
            sb.append(" " + days + (days == 1 ? " jour " : " jours "));
        }
        if(hours > 0) {
            sb.append(hours + (hours == 1 ? " heure " : " heures "));
        }
        if(minutes > 0) {
            sb.append(minutes + (minutes == 1 ? " minute " : " minutes "));
        }
        if(seconds > 0) {
            sb.append(seconds + (seconds == 1 ? " seconde " : " secondes "));
        }

        return sb.toString();
    }
}
