import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Simulator {
    private final Config cfg;
    private final TLB tlb;
    private final PageTable pt;
    private final PhysicalMemory pm;
    private long time = 0;


    public Simulator(Config c) {
        cfg = c;
        tlb = new TLB(cfg.tlbEntries);
        pt = new PageTable(cfg.pageTableLevels);
        pm = new PhysicalMemory(cfg.numFrames);
    }


    public void run(List<Long> addrs, String outPath) throws Exception {
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Path.of(outPath)))) {
            for (long vaddr : addrs) translate(vaddr, pw);
            pw.println("
            --- PAGE TABLE ---"); pt.dump(pw);
            pw.println("
            --- PHYSICAL MEMORY ---"); pm.dump(pw);
            pw.println("
            --- TLB ---"); tlb.dump(pw);
        }
    }


    private void translate(long vaddr, PrintWriter pw) {
        time++;
        int offset = (int)(vaddr & (cfg.pageSize - 1));
        long vpn = vaddr >> cfg.pageSizeBits;
        Integer frame = tlb.lookup(vpn);
        boolean hit = frame != null;


        if (!hit) {
            frame = pt.lookup(vpn);
            if (frame != null) pm.touch(frame, time);
            else {
                int f = pm.allocate(time);
                Long old = pm.get(f);
                if (old != null) pt.remove(old);
                pm.map(f, vpn);
                pt.insert(vpn, f);
                frame = f;
            }
            tlb.add(vpn, frame);
        }

        long paddr = ((long)frame * cfg.pageSize) + offset;
        pw.println("V="+vaddr+" -> P="+paddr+" frame="+frame+" TLB="+(hit?"HIT":"MISS"));
    }
}