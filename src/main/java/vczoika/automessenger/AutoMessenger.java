package vczoika.automessenger;
import eu.pb4.placeholders.api.TextParserUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
public class AutoMessenger implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("automessenger");
    private static final String CONFIGFILE_PATH = ".\\config\\automessenger.properties";
    private static final int DEFAULT_MESSAGE_INTERVAL = 300; // Adjust the interval as desired
    private static int messageInterval = DEFAULT_MESSAGE_INTERVAL; // Interval for sending messages


    // Declare fields for message-related variables
    private static int ticksSinceLastMessage = 0;
    private static List<String> messages = new ArrayList<>();
    private static int messageIndex = 0;
    @Override
    public void onInitialize() {
        checkConfigFile();

        loadConfig();

        // Register a server tick event to send messages.
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ticksSinceLastMessage++;
            if (ticksSinceLastMessage >= messageInterval) {
                ticksSinceLastMessage = 0;
                String message = messages.get(messageIndex);
                messageIndex = (messageIndex + 1) % messages.size();

                // Send the message to all players on the server.
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    player.sendMessage(TextParserUtils.formatText(message));
                }
            }
        });

        registerCommand();
        LOGGER.info("[AutoMessenger]: AutoMessenger initialized! Thanks for using it! Consider donating if it helps you! :)");
    }

    private static void checkConfigFile() {
        if (!Files.exists(Paths.get(CONFIGFILE_PATH))) {
            LOGGER.warn("[AutoMessenger]: Configuration file not found. Creating a default configuration file.");

            // Create a default configuration file if it doesn't exist
            try {
                FileWriter myWriter = new FileWriter(CONFIGFILE_PATH);
                myWriter.write("# This is the default configuration file for the AutoMessenger mod.\n");
                myWriter.write("#\n");
                myWriter.write("# This mod uses Simplified Text Format for its color usage (also MOTD Color Code, but Simplified Text Format has A LOT more customization). Please check the link below to see how to use it (it's pretty simple):\n");
                myWriter.write("# https://placeholders.pb4.eu/user/text-format/#structure \n");
                myWriter.write("# (Shoutout to Patbox! Check him out: https://github.com/Patbox)\n");
                myWriter.write("#\n");
                myWriter.write("#\n");
                myWriter.write("# Change the message-interval to change how often the messages will be sent. default=300 (15 seconds)\n");
                myWriter.write("# 20 ticks = 1 second. 24000 ticks = 1 in-game day, or 20 minutes.\n");
                myWriter.write("message-interval=300\n");
                myWriter.write("\n");
                myWriter.write("\n");
                myWriter.write("# Please check if the messages are in ascending order, otherwise it won't work. Also don't skip any number :c \n");
                myWriter.write("# Change the messages below:\n");
                myWriter.write("\n");
                myWriter.write("message1=<gr:#11dddd:#cccccc>Hello! Thanks a lot for dowloading my first mod! It means a lot!\n");
                myWriter.write("\n");
                myWriter.write("message2=<gold>If you need any help please send me a message! <gray><url:'https://discord.gg/ZdEdE3eGes'><underlined>Discord<reset> (hey, it's clickable!)\n");
                myWriter.write("\n");
                myWriter.write("message3=<aqua>Use <gray><cmd:'/automessenger reload'><underlined>/automessenger reload<reset> <aqua>if you wish to reload the config file changes.<reset> (Click the command to see the magic!)\n");
                myWriter.write("\n");
                myWriter.write("message4=\\u00A74This also works...\n");
                myWriter.write("\n");
                myWriter.write("# Uncomment the following lines if you wish to add more messages (You can add as much as you want!)\n");
                myWriter.write("#message5=change me for something...\n");
                myWriter.write("#message6=something...\n");
                myWriter.write("#message7=something else...\n");
                myWriter.write("#message8=donate something for me ;-; (anything is welcome, shitty economy here)\n");
                myWriter.write("#message9=1 usd = 5 brl (1 can of Monster = 10 BRL) o.o\n");
                myWriter.close();
                LOGGER.info("[AutoMessenger]: Successfully created the default configuration file.");
            } catch (IOException e) {
                LOGGER.error("[AutoMessenger]: ERROR!!! An error occurred while creating the configuration file. CHECK IF YOUR /CONFIG FOLDER EXISTS OTHERWISE THE MOD WILL CRASH!: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void loadConfig() {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(CONFIGFILE_PATH)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            LOGGER.error("[AutoMessenger]: Failed to load configuration file: " + e.getMessage());
            return;
        }


        // Read and parse message interval from the configuration file
        String interval = properties.getProperty("message-interval");
        if (interval != null && !interval.isEmpty()) {
            try {
                messageInterval = Integer.parseInt(interval);
            } catch (NumberFormatException e) {
                LOGGER.warn("[AutoMessenger]: Invalid usage for \"message-interval\". Using the default value: " + DEFAULT_MESSAGE_INTERVAL);
            }
        }


        // Read and parse messages from the configuration file
        messages.clear();
        int index = 1;
        while (true) {
            String message = properties.getProperty("message" + index);
            if (message == null) {
                break;
            }
            if (!message.isEmpty()) {
                messages.add(message);
            }
            index++;
        }

        if (messages.isEmpty()) {
            LOGGER.warn("[AutoMessenger]: No messages found in the configuration file or all messages are empty.");
        }
    }

    private static void registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, server) -> {
            dispatcher.register(CommandManager.literal("automessenger")
                    .then(CommandManager.literal("reload")
                            .executes(context -> reloadConfig(context.getSource()))));
        });
    }
    private static int reloadConfig(ServerCommandSource source) {
        try {
            loadConfig();
            source.sendFeedback(() -> Text.of("AutoMessenger configuration reloaded."), false);
        } catch (Exception e) {
            source.sendError(Text.of("An error occurred while reloading the AutoMessenger configuration."));
            LOGGER.error("[AutoMessenger]: Error reloading configuration: " + e.getMessage());
            e.printStackTrace();
        }
        return 1;
    }







}

