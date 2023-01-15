package mine.block.quicksearch.search;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;

public class SearchResult {
    public SearchResult(Text name, Item entry, RegistryKey<Item> key) {
        this.name = name;
        this.entry = entry;
        this.key = key;
    }

    public Text getName() {
        return name;
    }

    public Item getEntry() {
        return entry;
    }
    public RegistryKey<Item> getKey() {
        return key;
    }

    private final Text name;
    private final Item entry;
    private final RegistryKey<Item> key;
}
