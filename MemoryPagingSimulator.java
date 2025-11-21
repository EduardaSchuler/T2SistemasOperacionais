import java.io.*;
import java.nio.file.*;
import java.util.*;


public class MemoryPagingSimulator {
    public static void main(String[] a) throws Exception {
        String cfgFile = a[0];
        String addrFile = a[1];
        String outFile = a[2];
        Config cfg = Config.fromJson(Files.readString(Path.of(cfgFile)));
        Simulator sim = new Simulator(cfg);
        
        List<Long> addrs = new ArrayList<>();

        for (String line : Files.readAllLines(Path.of(addrFile)))

        if (!line.isBlank()) addrs.add(Long.parseLong(line.trim()));

        sim.run(addrs, outFile);
    }
}