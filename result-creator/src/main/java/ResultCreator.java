import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ResultCreator {

    private static final Runtime runtime = Runtime.getRuntime();

    public static void main(String[] args) throws IOException, InterruptedException {
        List<Path> bots = Files.walk(Paths.get("work/"))
                .filter(path -> path.getFileName().toString().endsWith(".jar") && !path.getFileName().toString().equals("server.jar"))
                .collect(Collectors.toList());
        bots.forEach(bot -> bots.subList(bots.indexOf(bot) + 1, bots.size()).forEach(enemy -> game(bot, enemy)));
        writeResult();
    }

    private static void game(Path bot1, Path bot2) {
        try {
            Process server = runtime.exec("java -jar work/server.jar");
            TimeUnit.SECONDS.sleep(1);
            runtime.exec("java -jar " + bot1.toFile().getAbsolutePath());
            runtime.exec("java -jar " + bot2.toFile().getAbsolutePath());
            server.waitFor();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
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