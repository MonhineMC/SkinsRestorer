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

import me.dwyur.danproxy.api.command.CommandBridge;
import me.dwyur.danproxy.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.SenderMapperHolder;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.slf4j.Logger;

public class SRDanProxyCommandManager<C> extends CommandManager<C> implements SenderMapperHolder<CommandSource, C> {

    private final CommandBridge bridge;
    private final SenderMapper<CommandSource, C> senderMapper;

    @SuppressWarnings("this-escape")
    public SRDanProxyCommandManager(final CommandBridge bridge, final Logger logger,
                                    final ExecutionCoordinator<C> coordinator,
                                    final SenderMapper<CommandSource, C> senderMapper) {
        super(coordinator, new SRDanProxyRegistrationHandler<>());
        this.bridge = bridge;
        this.senderMapper = senderMapper;
        ((SRDanProxyRegistrationHandler<C>) commandRegistrationHandler()).initialize(this);
        registerDefaultExceptionHandlers(
                triplet -> senderMapper.reverse(triplet.first().sender()).sendMessage(Component.text(
                        triplet.first().formatCaption(triplet.second(), triplet.third()), NamedTextColor.RED)),
                pair -> logger.error(pair.first(), pair.second())
        );
    }

    @Override
    public final boolean hasPermission(final C sender, final String permission) {
        return senderMapper.reverse(sender).hasPermission(permission);
    }

    @Override
    public final SenderMapper<CommandSource, C> senderMapper() {
        return senderMapper;
    }

    CommandBridge bridge() {
        return bridge;
    }
}
