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

import me.dwyur.danproxy.api.Proxy;
import me.dwyur.danproxy.api.plugin.Plugin;
import me.dwyur.danproxy.api.plugin.PluginContext;
import me.dwyur.danproxy.api.plugin.PluginInfo;
import net.skinsrestorer.danproxy.logger.Slf4jLoggerImpl;
import net.skinsrestorer.shared.plugin.SRBootstrapper;
import net.skinsrestorer.shared.plugin.SRProxyPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@PluginInfo(
        name = "SkinsRestorer",
        version = SRDanProxyVersion.VERSION,
        description = "Restores and changes player skins, danproxy platform",
        authors = {"knat", "AlexProgrammerDE", "Blackfire62", "McLive", "dwyur"},
        softDepends = {"floodgate", "viaversion"}
)
public class SRDanProxyBootstrap implements Plugin {

    private volatile Runnable shutdown;

    @Override
    public void onEnable(final @NotNull PluginContext context) {
        SRBootstrapper.startPlugin(
                runnable -> shutdown = runnable,
                List.of(
                        new SRBootstrapper.PlatformClass<>(PluginContext.class, context),
                        new SRBootstrapper.PlatformClass<>(Proxy.class, context.getProxy()),
                        new SRBootstrapper.PlatformClass<>(SRDanProxyBootstrap.class, this)
                ),
                new Slf4jLoggerImpl(context.getLogger()),
                false,
                SRDanProxyAdapter.class,
                SRProxyPlugin.class,
                context.getDataDirectory(),
                SRDanProxyInit.class
        );
    }

    @Override
    public void onDisable(final @NotNull PluginContext context) {
        Runnable current = shutdown;

        if (current != null) {
            current.run();
        }
    }
}
