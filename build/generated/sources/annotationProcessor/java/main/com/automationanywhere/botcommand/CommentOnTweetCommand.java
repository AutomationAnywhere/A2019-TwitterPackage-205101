package com.automationanywhere.botcommand;

import com.automationanywhere.bot.service.GlobalSessionContext;
import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import java.lang.ClassCastException;
import java.lang.Deprecated;
import java.lang.Object;
import java.lang.String;
import java.lang.Throwable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CommentOnTweetCommand implements BotCommand {
  private static final Logger logger = LogManager.getLogger(CommentOnTweetCommand.class);

  private static final Messages MESSAGES_GENERIC = MessagesFactory.getMessages("com.automationanywhere.commandsdk.generic.messages");

  @Deprecated
  public Optional<Value> execute(Map<String, Value> parameters, Map<String, Object> sessionMap) {
    return execute(null, parameters, sessionMap);
  }

  public Optional<Value> execute(GlobalSessionContext globalSessionContext,
      Map<String, Value> parameters, Map<String, Object> sessionMap) {
    logger.traceEntry(() -> parameters != null ? parameters.entrySet().stream().filter(en -> !Arrays.asList( new String[] {}).contains(en.getKey()) && en.getValue() != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).toString() : null, ()-> sessionMap != null ?sessionMap.toString() : null);
    CommentOnTweet command = new CommentOnTweet();
    HashMap<String, Object> convertedParameters = new HashMap<String, Object>();
    if(parameters.containsKey("SessionName") && parameters.get("SessionName") != null && parameters.get("SessionName").get() != null) {
      convertedParameters.put("SessionName", parameters.get("SessionName").get());
      if(convertedParameters.get("SessionName") !=null && !(convertedParameters.get("SessionName") instanceof String)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","SessionName", "String", parameters.get("SessionName").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("SessionName") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","SessionName"));
    }

    if(parameters.containsKey("originalTweet") && parameters.get("originalTweet") != null && parameters.get("originalTweet").get() != null) {
      convertedParameters.put("originalTweet", parameters.get("originalTweet").get());
      if(convertedParameters.get("originalTweet") !=null && !(convertedParameters.get("originalTweet") instanceof String)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","originalTweet", "String", parameters.get("originalTweet").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("originalTweet") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","originalTweet"));
    }

    if(parameters.containsKey("statusBody") && parameters.get("statusBody") != null && parameters.get("statusBody").get() != null) {
      convertedParameters.put("statusBody", parameters.get("statusBody").get());
      if(convertedParameters.get("statusBody") !=null && !(convertedParameters.get("statusBody") instanceof String)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","statusBody", "String", parameters.get("statusBody").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("statusBody") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","statusBody"));
    }

    if(parameters.containsKey("statusMedia") && parameters.get("statusMedia") != null && parameters.get("statusMedia").get() != null) {
      convertedParameters.put("statusMedia", parameters.get("statusMedia").get());
      if(convertedParameters.get("statusMedia") !=null && !(convertedParameters.get("statusMedia") instanceof String)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","statusMedia", "String", parameters.get("statusMedia").get().getClass().getSimpleName()));
      }
    }

    command.setSessionMap(sessionMap);
    try {
      Optional<Value> result =  Optional.ofNullable(command.action((String)convertedParameters.get("SessionName"),(String)convertedParameters.get("originalTweet"),(String)convertedParameters.get("statusBody"),(String)convertedParameters.get("statusMedia")));
      return logger.traceExit(result);
    }
    catch (ClassCastException e) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.IllegalParameters","action"));
    }
    catch (BotCommandException e) {
      logger.fatal(e.getMessage(),e);
      throw e;
    }
    catch (Throwable e) {
      logger.fatal(e.getMessage(),e);
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.NotBotCommandException",e.getMessage()),e);
    }
  }
}
