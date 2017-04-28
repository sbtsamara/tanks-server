import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ResultCreator {

    private static final Runtime runtime = Runtime.getRuntime();
    private static Path server;

    public static void main(String[] args) throws IOException, InterruptedException {
        Path work = Paths.get("work");
        Files.walk(work)
                .filter(path -> path.getFileName().toString().startsWith("tanks-server"))
                .findAny().ifPresent(path -> server = path);
        List<Path> bots = Files.walk(work)
                .filter(path -> path.getFileName().toString().endsWith(".jar") && !path.equals(server))
                .collect(Collectors.toList());
        bots.forEach(bot -> bots.subList(bots.indexOf(bot) + 1, bots.size()).forEach(enemy -> game(bot, enemy)));
        writeResult();
    }

    private static void game(Path bot1, Path bot2) {
        try {
            Process serverProcess = runtime.exec("java -jar " + server.toFile().getAbsolutePath());
            TimeUnit.SECONDS.sleep(1);
            Process bot1Process = runtime.exec("java -jar " + bot1.toFile().getAbsolutePath());
            Process bot2Process = runtime.exec("java -jar " + bot2.toFile().getAbsolutePath());
            readInputStream(bot1Process.getInputStream());
            readInputStream(bot2Process.getInputStream());
            readInputStream(bot1Process.getErrorStream());
            readInputStream(bot2Process.getErrorStream());
            serverProcess.waitFor();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void readInputStream(InputStream stream) {
        new Thread(() -> {
            try {
                while (stream.read() != -1) {
                    stream.read();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void writeResult() throws IOException {
        Files.write(Paths.get("result.txt"),
                Files.lines(Paths.get("winners.txt")).collect(
                        Collectors.groupingBy(line -> line.substring(0, line.indexOf("=")),
                                Collectors.summingInt(line -> Integer.valueOf(line.substring(line.indexOf("=") + 1)))))
                        .entrySet().stream()
                        .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                        .map(entry -> entry.getKey() + "=" + entry.getValue())
                        .collect(Collectors.toList()));
    }

}