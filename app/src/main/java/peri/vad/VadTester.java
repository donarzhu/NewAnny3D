/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peri.vad;

/**
 *
 * @author david
 */
public class VadTester {
    public native void init(int sample_rate, int channels, int sample_size);
    public native void appendData(byte[] data);
    public native boolean isFull();
    public native boolean isStarted();
    public native boolean isEnded();
    public native int getDataLength();
    public native int getBufferLength();
    public native byte[] getData();
    public native void clear();
    public native void compute();
    public native String sayHello();
    public native String stringFromJNI();
}
