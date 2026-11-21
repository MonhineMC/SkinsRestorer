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

import io.leangen.geantyref.GenericTypeReflector;
import me.dwyur.danproxy.api.command.BridgedCommand;
import me.dwyur.danproxy.api.command.CommandSource;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.internal.CommandNode;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.Suggestions;
import org.incendo.cloud.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

final class SRDanProxyCommand<C> implements BridgedCommand {

    private final CommandComponent<C> command;
    private final SRDanProxyCommandManager<C> manager;
    private final List<String> aliases;

    SRDanProxyCommand(final CommandComponent<C> command, final SRDanProxyCommandManager<C> manager) {
        this.command = command;
        this.manager = manager;
        this.aliases = List.copyOf(command.alternativeAliases());
    }

    @Override
    public @NotNull String getName() {
        return command.name();
    }

    @Override
    public @NotNull List<String> getAliases() {
        return aliases;
    }

    @Override
    public boolean isVisible(final @NotNull CommandSource source) {
        CommandNode<C> node = manager.commandTree().getNamedNode(command.name());

        if (node == null) {
            return false;
        }

        Map<Type, Permission> access =
                node.nodeMeta().getOrDefault(CommandNode.META_KEY_ACCESS, Collections.emptyMap());
        C sender = manager.senderMapper().map(source);

        for (Map.Entry<Type, Permission> entry : access.entrySet()) {
            if (GenericTypeReflector.isSuperType(entry.getKey(), sender.getClass())
                    && manager.testPermission(sender, entry.getValue()).allowed()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void execute(final @NotNull CommandSource source, final @NotNull String alias,
                        final @NotNull String @NotNull [] arguments) {
        String input = arguments.length == 0 ? command.name() : command.name() + " " + String.join(" ", arguments);
        manager.commandExecutor().executeCommand(manager.senderMapper().map(source), input);
    }

    @Override
    public @NotNull List<String> suggest(final @NotNull CommandSource source, final @NotNull String alias,
                                         final @NotNull String @NotNull [] arguments) {
        Suggestions<C, ?> result = manager.suggestionFactory().suggestImmediately(
                manager.senderMapper().map(source), command.name() + " " + String.join(" ", arguments));
        return result.list().stream()
                .map(Suggestion::suggestion)
                .map(suggestion -> StringUtils.trimBeforeLastSpace(suggestion, result.commandInput()))
                .filter(Objects::nonNull)
                .toList();
    }
}
