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
import me.dwyur.danproxy.api.event.player.PluginMessageEvent;
import net.skinsrestorer.danproxy.wrapper.WrapperDanProxy;
import net.skinsrestorer.shared.listeners.SRProxyMessageAdapter;
import net.skinsrestorer.shared.listeners.event.SRProxyMessageEvent;
import net.skinsrestorer.shared.subjects.SRProxyPlayer;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ProxyMessageListener {

    private final SRProxyMessageAdapter adapter;
    private final WrapperDanProxy wrapper;

    @Subscribe
    void onPluginMessage(final PluginMessageEvent event) {
        adapter.handlePluginMessage(wrap(event));
    }

    private SRProxyMessageEvent wrap(final PluginMessageEvent event) {
        return new SRProxyMessageEvent() {
            @Override
            public SRProxyPlayer getPlayer() {
                return wrapper.player(event.getPlayer());
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public void setCancelled(final boolean cancelled) {
            }

            @Override
            public byte[] getData() {
                return event.getData();
            }

            @Override
            public boolean isSenderServerConnection() {
                return true;
            }

            @Override
            public boolean isReceiverProxyPlayer() {
                return true;
            }

            @Override
            public String getChannel() {
                return event.getChannel();
            }
        };
    }
}
