package xyz.n7mn.dev;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import xyz.n7mn.dev.message.MessageListeners;
import xyz.n7mn.dev.util.DiscordUtil;
import xyz.n7mn.dev.yomiage.data.TextChannelData;

public class EventListener extends ListenerAdapter {

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent e) {
        AudioManager audioManager = e.getGuild().getAudioManager();
        if (audioManager.isConnected()) {
            AudioChannel audioChannel = audioManager.getConnectedChannel();

            if (audioChannel == null) return;

            int count = 0;

            for (Member member : audioChannel.getMembers()) {
                if (member.getUser().isBot()) {
                    continue;
                }

                count++;
            }

            if (count == 0 || e.getMember().getIdLong() == e.getJDA().getSelfUser().getIdLong()) {
                DiscordUtil.stop(e.getGuild());
            }
        } else if (e.getMember().getIdLong() == e.getJDA().getSelfUser().getIdLong()) {
            DiscordUtil.stop(e.getGuild());
        }
    }


    @Override
    public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent e) {
        if (e.getGuild().getAudioManager().isConnected()) {
            checkNonPlayer(e.getGuild(), e.getNewValue());
            checkNonPlayer(e.getGuild(), e.getOldValue());
        } else if (e.getMember().getIdLong() == e.getJDA().getSelfUser().getIdLong()) {
            DiscordUtil.stop(e.getGuild());
        }
    }

    private void checkNonPlayer(final Guild guild, AudioChannel audioChannel) {
        int count = 0;

        for (Member member : audioChannel.getMembers()) {
            if (member.getUser().isBot()) {
                continue;
            }

            count++;
        }

        final AudioChannel connectedChannel = guild.getAudioManager().getConnectedChannel();

        if (connectedChannel != null && connectedChannel.getIdLong() == audioChannel.getIdLong() && count == 0) {
            DiscordUtil.stop(guild);
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.isFromGuild()) {
            ButtonManager.execute(event);
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.isWebhookMessage() || event.getAuthor().isBot()) {
            return;
        }

        MessageListeners.getListeners().forEach(v -> {
            v.onMessageReceivedEvent(event);
        });

        /*CommandManager.execute(event);
        if (event.isFromGuild() && event.isFromType(ChannelType.TEXT)) {
            CasinoManager.getCasinoData(event.getGuild().getId(), event.getMember().getId())
                    .update(1);

            DiscordData e = new DiscordData(event.getChannel().asTextChannel(), event.getMessage(), null);

            if (e.getVoiceChoice(VoiceEnum.VOICE_ROID)) {
                for (TextChannelData textChannelData : VoiceRoidStartCommand.textChannelId.values()) {
                    if (textChannelData.getTextChannel().getIdLong() == event.getChannel().asTextChannel().getIdLong()) {
                        Path path = Paths.get("");
                        Path newPath = Paths.get(path.toAbsolutePath() + "/" + e.getMessage().getId() + ".wav");
                        //System.out.print(newPath.toString());
                        String message = event.getMessage().getContentRaw();
                        if (e.getMessageText().startsWith("https://") || e.getMessageText().startsWith("http://")) {
                            message = "URL省略";
                        } else if (message.length() > 50) {
                            message = message.substring(0, 45) + "...以下省略";
                        } else if (textChannelData.isFirst()) {
                            message = "接続完了しました";
                            textChannelData.setFirst(false);
                        }
                        //VoiceRoidMain.voiceRoidMain.addQueue(new VoiceRoidData(event.getTextChannel(), newPath.toFile(), message));
                        break;
                    }
                }
            }
        }*/
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        //super.onGuildMessageDelete(event);
    }
}