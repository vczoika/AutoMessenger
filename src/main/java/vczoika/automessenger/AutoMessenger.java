package vczoika.automessenger;
import com.typesafe.config.ConfigRenderOptions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.pb4.placeholders.api.TextParserUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AutoMessenger implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("automessenger");
	private int MESSAGE_INTERVAL;
	private int ticksSinceLastMessage = 0;
	private String[] messages;
	private int messageIndex = 0;

	@Override
	public void onInitialize() {
		// Try to find the configuration file. If it doesn't exist, generate a default configuration.
		File configFile = new File("config/automessenger.conf");
		if (!configFile.exists()) {
			LOGGER.info("[AutoMessenger]: Configuration file not found, generating default configuration.");
			generateDefaultConfig(configFile);
		}

		// Set the message interval and messages from the configuration.
		Config config = ConfigFactory.parseFile(configFile);
		MESSAGE_INTERVAL = config.getInt("message-interval");
		messages = config.getStringList("messages").toArray(new String[0]);

		// Register a server tick event to send messages.
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			ticksSinceLastMessage++;
			if (ticksSinceLastMessage >= MESSAGE_INTERVAL) {
				ticksSinceLastMessage = 0;
				String message = messages[messageIndex];
				messageIndex = (messageIndex + 1) % messages.length;

				// Send the message to all players on the server.
				for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
					player.sendMessage(TextParserUtils.formatText(message));
				}
			}
		});
		LOGGER.info("[AutoMessenger]: AutoMessenger initialized!");
	}

	// Generate a default configuration file.
	private void generateDefaultConfig(File configFile) {
		Config defaultConfig = ConfigFactory.parseResources("default-automessenger.conf");
		ConfigRenderOptions options = ConfigRenderOptions.defaults().setOriginComments(false);
		try (FileWriter writer = new FileWriter(configFile)) {
			writer.write(defaultConfig.root().render(options));
		} catch (IOException e) {
			LOGGER.error("[AutoMessenger]: Failed to generate default configuration!", e);
		}
	}
}















