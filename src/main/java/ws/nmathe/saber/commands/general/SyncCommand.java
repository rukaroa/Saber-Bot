package ws.nmathe.saber.commands.general;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import ws.nmathe.saber.Main;
import ws.nmathe.saber.commands.Command;

/**
 * Sets a channel to sync to a google calendar address
 */
public class SyncCommand implements Command
{

    @Override
    public String help(boolean brief)
    {
        String invoke = Main.getBotSettingsManager().getCommandPrefix() + "sync";

        String USAGE_EXTENDED = "Using ``" + invoke + " <channel> [<calendar address>]`` will replace all events in the specified channel" +
                "with events imported from a public google calendar. The command imports the next 7 days of events into the channel;" +
                " the channel will then automatically re-sync once every day. If ``<calendar address>`` is not included, " +
                "the address saved in the channel settings will be used.";

        String USAGE_BRIEF = "``" + invoke + "`` - sync a schedule to a google calendar";

        String EXAMPLES = "Ex: ``" + invoke + " #new_schedule g.rit.edu_g4elai703tm3p4iimp10g8heig@group.calendar.google.com``";

        if( brief )
            return USAGE_BRIEF;
        else
            return USAGE_BRIEF + "\n\n" + USAGE_EXTENDED + "\n\n" + EXAMPLES;
    }

    @Override
    public String verify(String[] args, MessageReceivedEvent event)
    {
        if( args.length < 1 )
            return "That's not enough arguments";
        if( args.length > 2)
            return "That's too many arguments";

        if( !Main.getScheduleManager().isASchedule(args[0].replace("<#","").replace(">","")))
            return "Channel " + args[0] + " is not on my list of schedule channels for your guild.";

        if( args.length == 2 && !Main.getCalendarConverter().checkValidAddress( args[1] ) )
            return "I could not connect to google calendar address **" + args[1] + "**";

        if( args.length == 1 && Main.getScheduleManager().getAddress(args[0].replace("<#","").replace(">","")).equals("off"))
            return "That channel is not configured to sync to any calendar!";

        return "";
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event)
    {
        TextChannel channel = event.getGuild().getTextChannelById( args[0].replace("<#","").replace(">","") );
        String address;
        if( args.length == 1 )
            address = Main.getScheduleManager().getAddress(channel.getId());
        else
        {
            address = args[1];
        }

        Main.getCalendarConverter().syncCalendar(address, channel);
        Main.getScheduleManager().setAddress(channel.getId(),address);
    }
}
