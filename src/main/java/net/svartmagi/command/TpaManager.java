package net.svartmagi.command;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import net.svartmagi.SvartmagiConfig;

/**
 * Ventende /tpa-foresporsler i minnet. Utloep sjekkes lazily ved
 * accept/deny - ingen ticking.
 */
public final class TpaManager {
    public record Request(UUID from, UUID to, boolean here, long expiresAtMillis) {
        public boolean expired() {
            return System.currentTimeMillis() > expiresAtMillis;
        }
    }

    /** target -> siste foresporsel til den spilleren */
    private static final Map<UUID, Request> PENDING = new HashMap<>();

    public static void request(UUID from, UUID to, boolean here) {
        PENDING.put(to, new Request(from, to, here,
                System.currentTimeMillis() + SvartmagiConfig.TPA_TIMEOUT_SECONDS.get() * 1000L));
    }

    @Nullable
    public static Request take(UUID target) {
        Request request = PENDING.remove(target);
        return request == null || request.expired() ? null : request;
    }

    @Nullable
    public static Request peek(UUID target) {
        Request request = PENDING.get(target);
        if (request != null && request.expired()) {
            PENDING.remove(target);
            return null;
        }
        return request;
    }

    public static void clear(UUID player) {
        PENDING.remove(player);
        PENDING.values().removeIf(r -> r.from().equals(player));
    }

    public static void clearAll() {
        PENDING.clear();
    }

    private TpaManager() {}
}
