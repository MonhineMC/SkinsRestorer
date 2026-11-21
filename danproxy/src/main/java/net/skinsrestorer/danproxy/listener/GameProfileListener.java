/*
 * SkinsRestorer
 * Copyright (C) 2026  SkinsRestorer Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.skinsrestorer.danproxy.listener;

import lombok.RequiredArgsConstructor;
import me.dwyur.danproxy.api.event.Subscribe;
import me.dwyur.danproxy.api.event.connection.GameProfileEvent;
import me.dwyur.danproxy.api.plugin.PluginContext;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.danproxy.SkinApplierDanProxy;
import net.skinsrestorer.shared.listeners.LoginProfileListenerAdapter;
import net.skinsrestorer.shared.listeners.event.SRLoginProfileEvent;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GameProfileListener {

    private final PluginContext context;
    private final SkinApplierDanProxy skinApplier;
    private final LoginProfileListenerAdapter<Void> adapter;

    @Subscribe
    void onGameProfile(final GameProfileEvent event) {
        adapter.handleLogin(wrap(event));
    }

    private SRLoginProfileEvent<Void> wrap(final GameProfileEvent event) {
        return new SRLoginProfileEvent<>() {
            @Override
            public boolean hasOnlineProperties() {
                return event.isOnlineMode();
            }

            @Override
            public UUID getPlayerUniqueId() {
                return event.getUniqueId();
            }

            @Override
            public String getPlayerName() {
                return event.getName();
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public void setResultProperty(final SkinProperty property) {
                event.setProperties(skinApplier.updateProperties(event.getProperties(), property));
            }

            @Override
            public Void runAsync(final Runnable runnable) {
                event.defer(CompletableFuture.runAsync(runnable, context.getScheduler()::runAsync));
                return null;
            }
        };
    }
}
