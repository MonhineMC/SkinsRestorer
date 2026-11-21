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
import me.dwyur.danproxy.api.event.EventPriority;
import me.dwyur.danproxy.api.event.Subscribe;
import me.dwyur.danproxy.api.event.connection.LoginEvent;
import net.skinsrestorer.danproxy.wrapper.WrapperDanProxy;
import net.skinsrestorer.shared.listeners.AdminInfoListenerAdapter;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AdminInfoListener {

    private final WrapperDanProxy wrapper;
    private final AdminInfoListenerAdapter adapter;

    @Subscribe(priority = EventPriority.MONITOR)
    void onLogin(final LoginEvent event) {
        adapter.handleConnect(() -> wrapper.player(event.getPlayer()));
    }
}
