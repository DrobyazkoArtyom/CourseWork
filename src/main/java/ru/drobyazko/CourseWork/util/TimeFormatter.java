package ru.drobyazko.CourseWork.util;

public class TimeFormatter {

    static public String formatTime(int time) {
        return String.format("%02d:%02d:%02d", (time/60/24), (time/60%24), (time%60));
    }

}
