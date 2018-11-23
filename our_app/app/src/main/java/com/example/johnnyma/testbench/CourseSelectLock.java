package com.example.johnnyma.testbench;

public class CourseSelectLock {

//    private Object lock;
//    private boolean pressed;
//
//    public CourseSelectLock() {
//        lock = new Object();
//        pressed = false;
//    }
//
//    public boolean acquireSelectLock() {
//        boolean retval = false;
//        synchronized (lock) {
//            retval = !pressed;
//            if(!pressed)
//                pressed = true;
//        }
//        return retval;
//    }
//
//    public void releaseSelectLock() {
//        synchronized (lock) {
//            pressed = false;
//        }
//    }
    public static boolean pressed;
    public static Object lock;
}
