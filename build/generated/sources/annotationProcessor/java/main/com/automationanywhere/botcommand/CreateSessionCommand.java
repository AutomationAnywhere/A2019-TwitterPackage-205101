package com.automationanywhere.botcommand;

import com.automationanywhere.bot.service.GlobalSessionContext;
import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import com.automationanywhere.core.security.SecureString;
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

public final class CreateSessionCommand implements BotCommand {
  private static final Logger logger = LogManager.getLogger(CreateSessionCommand.class);

  private static final Messages MESSAGES_GENERIC = MessagesFactory.getMessages("com.automationanywhere.commandsdk.generic.messages");

  @Deprecated
  public Optional<Value> execute(Map<String, Value> parameters, Map<String, Object> sessionMap) {
    return execute(null, parameters, sessionMap);
  }

  public Optional<Value> execute(GlobalSessionContext globalSessionContext,
      Map<String, Value> parameters, Map<String, Object> sessionMap) {
    logger.traceEntry(() -> parameters != null ? parameters.entrySet().stream().filter(en -> !Arrays.asList( new String[] {}).contains(en.getKey()) && en.getValue() != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).toString() : null, ()-> sessionMap != null ?sessionMap.toString() : null);
    CreateSession command = new CreateSession();
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

    if(parameters.containsKey("ConsumerKey") && parameters.get("ConsumerKey") != null && parameters.get("ConsumerKey").get() != null) {
      convertedParameters.put("ConsumerKey", parameters.get("ConsumerKey").get());
      if(convertedParameters.get("ConsumerKey") !=null && !(convertedParameters.get("ConsumerKey") instanceof SecureString)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","ConsumerKey", "SecureString", parameters.get("ConsumerKey").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("ConsumerKey") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","ConsumerKey"));
    }

    if(parameters.containsKey("ConsumerSecret") && parameters.get("ConsumerSecret") != null && parameters.get("ConsumerSecret").get() != null) {
      convertedParameters.put("ConsumerSecret", parameters.get("ConsumerSecret").get());
      if(convertedParameters.get("ConsumerSecret") !=null && !(convertedParameters.get("ConsumerSecret") instanceof SecureString)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","ConsumerSecret", "SecureString", parameters.get("ConsumerSecret").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("ConsumerSecret") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","ConsumerSecret"));
    }

    if(parameters.containsKey("AccessToken") && parameters.get("AccessToken") != null && parameters.get("AccessToken").get() != null) {
      convertedParameters.put("AccessToken", parameters.get("AccessToken").get());
      if(convertedParameters.get("AccessToken") !=null && !(convertedParameters.get("AccessToken") instanceof SecureString)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","AccessToken", "SecureString", parameters.get("AccessToken").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("AccessToken") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","AccessToken"));
    }

    if(parameters.containsKey("TokenSecret") && parameters.get("TokenSecret") != null && parameters.get("TokenSecret").get() != null) {
      convertedParameters.put("TokenSecret", parameters.get("TokenSecret").get());
      if(convertedParameters.get("TokenSecret") !=null && !(convertedParameters.get("TokenSecret") instanceof SecureString)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","TokenSecret", "SecureString", parameters.get("TokenSecret").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("TokenSecret") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","TokenSecret"));
    }

    command.setSessionMap(sessionMap);
    try {
      Optional<Value> result =  Optional.ofNullable(command.action((String)convertedParameters.get("SessionName"),(SecureString)convertedParameters.get("ConsumerKey"),(SecureString)convertedParameters.get("ConsumerSecret"),(SecureString)convertedParameters.get("AccessToken"),(SecureString)convertedParameters.get("TokenSecret")));
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
