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
package net.skinsrestorer.danproxy;

import lombok.RequiredArgsConstructor;
import me.dwyur.danproxy.api.player.Player;
import me.dwyur.danproxy.api.player.ProfileProperty;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.danproxy.wrapper.WrapperDanProxy;
import net.skinsrestorer.shared.api.SkinApplierAccess;
import net.skinsrestorer.shared.api.event.EventBusImpl;
import net.skinsrestorer.shared.api.event.SkinApplyEventImpl;
import net.skinsrestorer.shared.codec.SRServerPluginMessage;
import net.skinsrestorer.shared.subjects.SRProxyPlayer;
import net.skinsrestorer.shared.utils.ProxyAckTracker;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SkinApplierDanProxy implements SkinApplierAccess<Player> {

    private final WrapperDanProxy wrapper;
    private final EventBusImpl eventBus;
    private final ProxyAckTracker proxyAckTracker;

    @Override
    public void applySkin(final Player player, final SkinProperty property) {
        SkinApplyEventImpl applyEvent = new SkinApplyEventImpl(player, property);
        eventBus.callEvent(applyEvent);

        if (applyEvent.isCancelled()) {
            return;
        }

        SkinProperty appliedProperty = applyEvent.getProperty();
        player.setProfileProperties(updateProperties(player.getProfileProperties(), appliedProperty));

        SRProxyPlayer srPlayer = wrapper.player(player);
        srPlayer.sendToMessageChannel(new SRServerPluginMessage(new SRServerPluginMessage.SkinUpdateV3ChannelPayload(
                appliedProperty,
                proxyAckTracker.shouldAckPayload(srPlayer)
        )));
    }

    public List<ProfileProperty> updateProperties(final List<ProfileProperty> original, final SkinProperty property) {
        List<ProfileProperty> properties = new ArrayList<>(original);
        properties.removeIf(existing -> SkinProperty.TEXTURES_NAME.equals(existing.name()));
        properties.add(new ProfileProperty(SkinProperty.TEXTURES_NAME, property.getValue(), property.getSignature()));
        return properties;
    }

    public Optional<SkinProperty> getSkinProperty(final Player player) {
        return player.getProfileProperties().stream()
                .map(property -> SkinProperty.tryParse(property.name(), property.value(), property.signature()))
                .flatMap(Optional::stream)
                .findFirst();
    }
}
