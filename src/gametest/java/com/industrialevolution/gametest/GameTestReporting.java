package com.industrialevolution.gametest;

import com.industrialevolution.IndustrialEvolution;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.parsers.ParserConfigurationException;
import net.minecraft.gametest.framework.GlobalTestReporter;
import net.minecraft.gametest.framework.JUnitLikeTestReporter;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Test-only reporter; never included in the distributable mod. */
@Mod.EventBusSubscriber(modid = IndustrialEvolution.MOD_ID)
public final class GameTestReporting {
    private GameTestReporting() {
    }

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        Path report = Path.of(System.getProperty("industrialevolution.gametest.report",
                "../build/reports/gametest/results.xml")).toAbsolutePath();
        try {
            Files.createDirectories(report.getParent());
            Files.deleteIfExists(report);
            GlobalTestReporter.replaceWith(new JUnitLikeTestReporter(report.toFile()));
        } catch (IOException | ParserConfigurationException exception) {
            throw new IllegalStateException("Cannot initialize GameTest report " + report, exception);
        }
    }
}
