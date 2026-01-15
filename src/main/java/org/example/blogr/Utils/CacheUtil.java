
package org.example.blogr.Utils;

import org.example.blogr.domain.Post;

import java.util.*;
import java.util.concurrent.*;

public class CacheUtil {
    private static final Map<String, CacheEntry> searchResults = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();

    private static final long TTL = TimeUnit.MINUTES.toMillis(5);

    static {
        cleaner.scheduleAtFixedRate(CacheUtil::invalidateExpired, 1, 1, TimeUnit.MINUTES);
    }

    public static void put(String searchTerm, List<Post> posts) {
        String key = searchTerm.toLowerCase();
        searchResults.put(key, new CacheEntry(posts, System.currentTimeMillis() + TTL));
    }

    public static List<Post> get(String query) {
        String key = query.toLowerCase();
        CacheEntry entry = searchResults.get(key);
        if (entry == null || entry.isExpired()) {
            searchResults.remove(key);
            return null;
        }
        return entry.value;
    }

    public static boolean contains(String query) {
        String key = query.toLowerCase();
        CacheEntry entry = searchResults.get(key);
        return entry != null && !entry.isExpired();
    }

    public static void invalidateAll() {
        searchResults.clear();
    }

    public static void invalidate(String searchTerm) {
        searchResults.remove(searchTerm.toLowerCase());
    }

    private static void invalidateExpired() {
        long now = System.currentTimeMillis();
        searchResults.entrySet().removeIf(e -> e.getValue().expiryTime < now);
    }

    private static class CacheEntry {
        List<Post> value;
        long expiryTime;

        CacheEntry(List<Post> value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }
}
