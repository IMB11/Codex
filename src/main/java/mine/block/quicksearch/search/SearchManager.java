package mine.block.quicksearch.search;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

public class SearchManager {
    public static HashMap<String, SearchResult> SEARCH_MAP = new HashMap<>();

    public static void initialize() {
        for (Map.Entry<RegistryKey<Item>, Item> registryKeyItemEntry : Registries.ITEM.getEntrySet()) {
            var key = registryKeyItemEntry.getKey();
            var item = registryKeyItemEntry.getValue();
            SEARCH_MAP.put(I18n.translate(item.getTranslationKey()), new SearchResult(Text.translatable(item.getTranslationKey()), item, key));
        }
    }
}
