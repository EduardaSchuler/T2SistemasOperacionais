import java.io.*;
import java.util.*;


public class PhysicalMemory {
    private final int[] frames;
    private final long[] lastAccess;


    public PhysicalMemory(int n) {
        frames = new int[n];
        Arrays.fill(frames, -1);
        lastAccess = new long[n];
        Arrays.fill(lastAccess, -1);
    }


    public int allocate(long time) {
        for (int i = 0; i < frames.length; i++) {
            if (frames[i] == -1) { lastAccess[i] = time; return i; }
        }
        int victim = 0;
        long min = Long.MAX_VALUE;
        for (int i = 0; i < frames.length; i++)
        if (lastAccess[i] < min) { victim = i; min = lastAccess[i]; }
        frames[victim] = -1;
        lastAccess[victim] = time;
        return victim;
    }


    public void map(int frame, long vpn) { frames[frame] = (int)vpn; }

    public Long get(int f) { return frames[f] == -1 ? null : (long)frames[f]; }

    public void touch(int f, long t) { lastAccess[f] = t; }


    public void dump(PrintWriter pw) {
        for (int i = 0; i < frames.length; i++)
        pw.println("Frame "+i+" -> vpn="+frames[i]+" last="+lastAccess[i]);
    }
}