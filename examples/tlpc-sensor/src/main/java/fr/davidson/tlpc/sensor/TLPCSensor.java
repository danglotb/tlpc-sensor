package fr.davidson.tlpc.sensor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public class TLPCSensor {

    private TLPCSensor() {

    }

    private static final String[] perfCounterNames = new String[]{
            "INSTRUCTIONS_RETIRED",
            "LLC_MISSES",
            "CYCLES"
    };

    private static final String[] raplCounterNames = new String[]{
            "RAPL_ENERGY_PKG"
    };

    private static IndicatorsPerIdentifier indicatorsPerIdentifier = new IndicatorsPerIdentifier();

    private static Map<String, int[]> groupLeaderPerIdentifier = new HashMap<>();

    private native int[] sensorStart();

    private native long[] sensorStop(int fd_perf, int fd_rapl);

    public static void start(String identifier) {
        groupLeaderPerIdentifier.put(identifier, new TLPCSensor().sensorStart());
    }

    public static void stop(String identifier) {
        final int[] groupLeaderFd = groupLeaderPerIdentifier.get(identifier);
        final long[] indicators = new TLPCSensor().sensorStop(groupLeaderFd[0],groupLeaderFd[1]);
        final IndicatorPerLabel indicatorsPerLabel = new IndicatorPerLabel();
        for (int i = 0; i < perfCounterNames.length; i++) {
            indicatorsPerLabel.put(perfCounterNames[i], indicators[i]);
        }
        for (int i = 0; i < raplCounterNames.length; i++) {
            indicatorsPerLabel.put(raplCounterNames[i], indicators[perfCounterNames.length + i - 1]);
        }
        indicatorsPerIdentifier.put(identifier, indicatorsPerLabel);
    }

    public static void report(String pathname) {
        try (final FileWriter writer = new FileWriter(pathname)) {
            final Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(indicatorsPerIdentifier));
            indicatorsPerIdentifier.clear();
            groupLeaderPerIdentifier.clear();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final String DEFAULT_BASE_DIR = System.getProperty("java.io.tmpdir") + "/libperf";

    private static void extractLibPerf() {
        final String libperfDotSO = "/libperf.so";
        final File fileLib = new File(DEFAULT_BASE_DIR + libperfDotSO);
        final File directory = new File(DEFAULT_BASE_DIR);

        try {
            if (fileLib.exists()) {
                fileLib.delete();
                if (directory.exists()) {
                    directory.delete();
                }
            }
        } catch (Exception ignored) {
            System.err.println("Something went wrong when preparing the directories that contains share library.");
            System.err.println("It might result in an unstable run. Please check it out.");
            System.err.println(directory.getAbsolutePath());
            System.err.println(fileLib.getAbsolutePath());
        }
        try {
            directory.mkdir();
        } catch (Exception ignored) {

        }

        final String extractFilePath = DEFAULT_BASE_DIR + libperfDotSO;
        try (final InputStream resourceAsStream = TLPCSensor.class.getResourceAsStream(libperfDotSO)) {
            try (final FileOutputStream writer = new FileOutputStream(extractFilePath)) {
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                while ((bytesRead = resourceAsStream.read(buffer)) != -1) {
                    writer.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void addDirectoryToLoadedLibraries() {
        try {
            Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);
            String[] paths = (String[]) field.get(null);
            if (Arrays.asList(paths).contains(DEFAULT_BASE_DIR)) {
                return;
            }
            String[] tmp = new String[paths.length + 1];
            System.arraycopy(paths, 0, tmp, 0, paths.length);
            tmp[paths.length] = DEFAULT_BASE_DIR;
            field.set(null, tmp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static {
        extractLibPerf();
        addDirectoryToLoadedLibraries();
        System.loadLibrary("perf");
    }

    public static void test() {
        /**
         *  This code is made avalaible to test the API
         */
        TLPCSensor.start("main");
        TLPCSensor.start("loop1");
        for (int i = 0; i < 100000; i++) {
            System.out.println(i);
        }
        TLPCSensor.stop("loop1");
        TLPCSensor.start("loop2");
        for (int i = 0; i < 100000; i++) {
            System.out.println(i);
        }
        TLPCSensor.stop("loop2");
        TLPCSensor.stop("main");
        TLPCSensor.report("target/report_java.json");
    }

    public static void main(String[] args) {
        test();
    }
}