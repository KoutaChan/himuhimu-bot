package xyz.n7mn.dev.commandprocessor;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandName {

    String help() default "まだ説明文が追加されていません";

    String[] command();

    CommandType commandType();

    ChannelType channelType() default ChannelType.TEXT;

    Permission[] permission() default Permission.UNKNOWN;

    boolean maintenance() default false;

    String maintenanceMessage() default "";

    boolean startWith() default false;
}