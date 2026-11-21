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
package net.skinsrestorer.danproxy.command;

import org.incendo.cloud.Command;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.internal.CommandRegistrationHandler;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

final class SRDanProxyRegistrationHandler<C> implements CommandRegistrationHandler<C> {

    private final Set<String> registered = ConcurrentHashMap.newKeySet();
    private SRDanProxyCommandManager<C> manager;

    void initialize(final SRDanProxyCommandManager<C> manager) {
        this.manager = manager;
    }

    @Override
    public boolean registerCommand(final Command<C> command) {
        CommandComponent<C> root = command.rootComponent();

        if (registered.add(root.name())) {
            manager.bridge().register(new SRDanProxyCommand<>(root, manager));
        }

        return true;
    }
}
