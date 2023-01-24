package mine.block.quicksearch.datagen;

import io.umehara.ogmapper.OgMapper;
import io.umehara.ogmapper.domain.OgTags;
import io.umehara.ogmapper.jsoup.JsoupOgMapperFactory;
import io.umehara.ogmapper.service.OgMapperFactory;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinecraftWikiProvider extends FabricLanguageProvider {
    protected MinecraftWikiProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    private static Logger LOGGER = LoggerFactory.getLogger("MinecraftWikiProvider");
    private static OgMapper MAPPER = new JsoupOgMapperFactory().build();

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        HashMap<RegistryKey<Item>, Exception> failed = new HashMap<>();

        try {
            translationBuilder.add(dataOutput.getModContainer().findPath("assets/quicksearch/lang/en_us.base.json").get());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Pattern firstSentencePattern = Pattern.compile("^.*?[.!?](?=\\s[A-Z]|\\s?$)(?!.*\\))");

        Registries.ITEM.getKeys().parallelStream().forEach((key) -> {
            try {
                String descriptionKey = key.getValue().toTranslationKey("description");
                String baseURL = "https://minecraft.fandom.com/wiki/";
                OgTags tags = MAPPER.process(new URL(baseURL + key.getValue().getPath()));
                String description = tags.getDescription();
                int position = description.indexOf('.');
                if (position >= 0)
                {
                    LOGGER.info(key.getValue() + ": " + description.substring(0, position + 1).trim());
                    translationBuilder.add(descriptionKey, description.substring(0, position + 1).trim());
                }
            } catch (Exception e) {
                failed.put(key, e);
            }
        });

        LOGGER.warn("The following items failed to generate descriptions for:");
        failed.forEach((left, right) -> {
            LOGGER.warn(left.getValue() + " failed because: " + right.getMessage());
        });
    }
}
