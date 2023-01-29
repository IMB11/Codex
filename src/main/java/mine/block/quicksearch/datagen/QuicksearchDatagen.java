package mine.block.quicksearch.datagen;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class QuicksearchDatagen implements DataGeneratorEntrypoint {
    public static void downloadFileInto(CharSequence stringURL,
                                        File directory) {//from  w  w w.  j ava2s  .  c  o  m
        try {
            URL url = new URL(stringURL.toString());
            unzipIntoDirectory(url.openStream(), directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void unzipIntoDirectory(InputStream inputStream,
                                          File directory) {
        if (directory.isFile())
            return;
        directory.mkdirs();

        try {
            inputStream = new BufferedInputStream(inputStream);
            inputStream = new ZipInputStream(inputStream);

            for (ZipEntry entry = null; (entry = ((ZipInputStream) inputStream)
                    .getNextEntry()) != null;) {
                StringBuilder pathBuilder = new StringBuilder(
                        directory.getPath()).append('/').append(
                        entry.getName());
                File file = new File(pathBuilder.toString());

                if (entry.isDirectory()) {
                    file.mkdirs();
                    continue;
                }

                pathBuilder.append(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static HashMap<String, JsonObject> LANGUAGES = new HashMap<>();

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        Path pathToLangFolder = Path.of("D:/Temp/Languages/");
        File langFolder = new File(pathToLangFolder.toString());
        Gson gson = new Gson();
        for (File file : langFolder.listFiles()) {
            try {
                JsonReader reader = new JsonReader(new FileReader(file));
                JsonObject lg = gson.fromJson(reader, JsonObject.class);
                System.out.println("Loaded: " + file.getName());
                LANGUAGES.put(file.getName().replace(".json", ""), lg);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(CreateWikiProvider::new);
//        pack.addProvider(SpanishMinecraftWikiProvider::new);
//        pack.addProvider(MinecraftWikiProvider::new);
    }
}
