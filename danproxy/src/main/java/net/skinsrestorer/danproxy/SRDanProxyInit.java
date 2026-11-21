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

import ch.jalu.injector.Injector;
import lombok.RequiredArgsConstructor;
import me.dwyur.danproxy.api.player.Player;
import me.dwyur.danproxy.api.plugin.PluginContext;
import net.skinsrestorer.danproxy.listener.AdminInfoListener;
import net.skinsrestorer.danproxy.listener.GameProfileListener;
import net.skinsrestorer.danproxy.listener.ProxyMessageListener;
import net.skinsrestorer.danproxy.wrapper.WrapperDanProxy;
import net.skinsrestorer.shared.plugin.SRPlugin;
import net.skinsrestorer.shared.plugin.SRProxyPlatformInit;
import net.skinsrestorer.shared.plugin.SRProxyPluginSupportChecker;
import net.skinsrestorer.shared.utils.SRHelpers;

import javax.inject.Inject;

@SuppressWarnings("unused")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SRDanProxyInit implements SRProxyPlatformInit {

    private final Injector injector;
    private final SRPlugin plugin;
    private final SRProxyPluginSupportChecker pluginSupportChecker;
    private final PluginContext context;
    private final WrapperDanProxy wrapper;

    @Override
    public void initSkinApplier() {
        plugin.registerSkinApplier(injector.getSingleton(SkinApplierDanProxy.class), Player.class, wrapper);
    }

    @Override
    public void initLoginProfileListener() {
        context.getEvents().register(injector.newInstance(GameProfileListener.class));
    }

    @Override
    public void initAdminInfoListener() {
        context.getEvents().register(injector.newInstance(AdminInfoListener.class));
    }

    @Override
    public void checkPluginSupport() {
        pluginSupportChecker.checkViaVersionProxyInstall();
    }

    @Override
    public void initMessageChannel() {
        context.registerChannel(SRHelpers.MESSAGE_CHANNEL);
        context.getEvents().register(injector.getSingleton(ProxyMessageListener.class));
    }
}
