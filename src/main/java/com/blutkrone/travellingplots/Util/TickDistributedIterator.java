package com.blutkrone.travellingplots.Util;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Iterator;

public abstract class TickDistributedIterator<T> {

    private final Collection<T> collection;

    public TickDistributedIterator(Collection<T> collection) {
        this.collection = collection;
    }

    public abstract void forEach(T entry);

    protected Collection<T> getCollection() {
        return collection;
    }

    public void onStart() {

    }

    public void onFinish() {

    }

    public void iterateSync(org.bukkit.plugin.Plugin plugin, int perTick, int delay, int interval) {
        onStart();

        Iterator<T> iterator = getCollection().iterator();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < perTick; i++) {
                    if (iterator.hasNext()) {
                        forEach(iterator.next());
                    } else break;
                }

                if (!iterator.hasNext()) {
                    onFinish();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, delay, interval);
    }

    public void iterateAsync(org.bukkit.plugin.Plugin plugin, int perTick, int delay, int interval) {
        Iterator<T> iterator = getCollection().iterator();
        boolean[] start = {true};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (start[0]) {
                    onStart();
                    start[0] = false;
                }

                for (int i = 0; i < perTick; i++) {
                    if (iterator.hasNext()) {
                        forEach(iterator.next());
                    } else break;
                }

                if (!iterator.hasNext()) {
                    onFinish();
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(plugin, delay, interval);
    }
}
