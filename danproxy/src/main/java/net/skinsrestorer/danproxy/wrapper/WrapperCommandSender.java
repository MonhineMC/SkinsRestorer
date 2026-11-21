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
package net.skinsrestorer.danproxy.wrapper;

import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import me.dwyur.danproxy.api.command.CommandSource;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.skinsrestorer.shared.subjects.AbstractSRCommandSender;
import net.skinsrestorer.shared.subjects.messages.ComponentString;
import net.skinsrestorer.shared.subjects.permissions.Permission;
import net.skinsrestorer.shared.utils.Tristate;

@SuperBuilder
public class WrapperCommandSender extends AbstractSRCommandSender {

    private final @NonNull CommandSource sender;

    @Override
    public <S> S getAs(final Class<S> senderClass) {
        return senderClass.cast(sender);
    }

    @Override
    public void sendMessage(final ComponentString messageJson) {
        sender.sendMessage(GsonComponentSerializer.gson().deserialize(messageJson.jsonString()));
    }

    @Override
    public boolean hasPermission(final Permission permission) {
        return permission.checkPermission(node -> switch (sender.getPermissionValue(node)) {
            case TRUE -> Tristate.TRUE;
            case FALSE -> Tristate.FALSE;
            case NOT_SET -> Tristate.UNDEFINED;
        });
    }
}
