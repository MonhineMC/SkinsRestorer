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
import me.dwyur.danproxy.api.plugin.PluginDescription;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.danproxy.command.SRDanProxyCommandManager;
import net.skinsrestorer.danproxy.wrapper.WrapperDanProxy;
import net.skinsrestorer.shared.commands.SoundProvider;
import net.skinsrestorer.shared.info.Platform;
import net.skinsrestorer.shared.info.PluginInfo;
import net.skinsrestorer.shared.plugin.SRProxyAdapter;
import net.skinsrestorer.shared.subjects.SRCommandSender;
import net.skinsrestorer.shared.subjects.SRPlayer;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;

import javax.inject.Inject;
import java.io.InputStream;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SRDanProxyAdapter implements SRProxyAdapter {

    private static final String PLATFORM_NAME = "danproxy";
    private static final String PLATFORM_VENDOR = "Dwyur";

    private final Injector injector;
    private final PluginContext context;
    private final List<Object> keptAlive = new CopyOnWriteArrayList<>();

    @Override
    public Object createMetricsInstance() {
        return null;
    }

    @Override
    public InputStream getResource(final String resource) {
        return SRDanProxyAdapter.class.getClassLoader().getResourceAsStream(resource);
    }

    @Override
    public CommandManager<SRCommandSender> createCommandManager() {
        WrapperDanProxy wrapper = injector.getSingleton(WrapperDanProxy.class);
        return new SRDanProxyCommandManager<>(
                context.getCommandBridge(),
                context.getLogger(),
                ExecutionCoordinator.asyncCoordinator(),
                SenderMapper.create(wrapper::commandSender, wrapper::unwrap)
        );
    }

    @Override
    public void runAsync(final Runnable runnable) {
        context.getScheduler().runAsync(runnable);
    }

    @Override
    public void runAsyncDelayed(final Runnable runnable, final long delay, final TimeUnit timeUnit) {
        context.getScheduler().runLater(runnable, Duration.of(delay, timeUnit.toChronoUnit()));
    }

    @Override
    public void runRepeatAsync(final Runnable runnable, final long delay, final long interval,
                               final TimeUnit timeUnit) {
        context.getScheduler().runTimer(runnable, Duration.of(delay, timeUnit.toChronoUnit()),
                Duration.of(interval, timeUnit.toChronoUnit()));
    }

    @Override
    public void extendLifeTime(final Object plugin, final Object object) {
        keptAlive.add(object);
    }

    @Override
    public boolean supportsDefaultPermissions() {
        return true;
    }

    @Override
    public Class<? extends SoundProvider> getSoundProviderClass() {
        return SoundProvider.NoopSoundProvider.class;
    }

    @Override
    public String getPlatformVersion() {
        return context.getProxy().getVersion();
    }

    @Override
    public String getPlatformName() {
        return PLATFORM_NAME;
    }

    @Override
    public String getPlatformVendor() {
        return PLATFORM_VENDOR;
    }

    @Override
    public Platform getPlatform() {
        return Platform.DANPROXY;
    }

    @Override
    public List<PluginInfo> getPlugins() {
        return context.getProxy().getPlugins().stream().map(SRDanProxyAdapter::info).toList();
    }

    @Override
    public Optional<SkinProperty> getSkinProperty(final SRPlayer player) {
        return injector.getSingleton(SkinApplierDanProxy.class).getSkinProperty(player.getAs(Player.class));
    }

    @Override
    public Collection<SRPlayer> getOnlinePlayers(final SRCommandSender sender) {
        WrapperDanProxy wrapper = injector.getSingleton(WrapperDanProxy.class);
        return context.getProxy().getPlayers().stream().<SRPlayer>map(wrapper::player).toList();
    }

    @Override
    public Optional<SRPlayer> getPlayer(final SRCommandSender sender, final UUID uniqueId) {
        WrapperDanProxy wrapper = injector.getSingleton(WrapperDanProxy.class);
        return Optional.ofNullable(context.getProxy().getPlayer(uniqueId)).map(wrapper::player);
    }

    private static PluginInfo info(final PluginDescription description) {
        return new PluginInfo(
                true,
                description.name(),
                description.name(),
                description.version(),
                description.mainClass(),
                Map.of(),
                description.authors()
        );
    }
}
