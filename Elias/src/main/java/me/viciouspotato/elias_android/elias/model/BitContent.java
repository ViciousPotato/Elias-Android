package me.viciouspotato.elias_android.elias.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class BitContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<BitItem> ITEMS = new ArrayList<BitItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, BitItem> ITEM_MAP = new HashMap<String, BitItem>();

    static {
        // Add 3 sample items.
        addItem(new BitItem("1", "Item 1"));
        addItem(new BitItem("2", "Item 2"));
        addItem(new BitItem("3", "Item 3"));
    }

    private static void addItem(BitItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class BitItem {
        public String id;
        public String content;

        public BitItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
