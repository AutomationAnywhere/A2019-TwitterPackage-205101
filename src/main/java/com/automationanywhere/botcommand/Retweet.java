package com.automationanywhere.botcommand;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import org.apache.commons.io.FilenameUtils;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXTAREA;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

//BotCommand makes a class eligible for being considered as an action.
@BotCommand

//CommandPks adds required information to be displayable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "Retweet", label = "[[Retweet.label]]",
        node_label = "[[Retweet.node_label]]", description = "[[Retweet.description]]", icon = "pkg.svg",

        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "[[Retweet.return_label]]", return_type = STRING, return_required = true, return_description = "[[Retweet.return_label.description]]")
public class Retweet {
    @Sessions
    private Map<String, Object> sessionMap;

    //Identify the entry point for the action. Returns a Value<String> because the return type is String.
    @Execute
    public Value<String> action(
            //Idx 1 would be displayed first, with a text box for entering the value.
            @Idx(index = "1", type = TEXT)
            //UI labels.
            @Pkg(label = "[[CreateSession.sessionName.label]]", default_value_type = STRING, default_value = "Default")
            //Ensure that a validation error is thrown when the value is null.
            @NotEmpty
                    String SessionName,
            //Idx 1 would be displayed first, with a text box for entering the value.
            @Idx(index = "2", type = TEXTAREA)
            //UI labels.
            @Pkg(label = "[[Retweet.statusID.label]]", description = "[[Retweet.statusID.description]]")
            //Ensure that a validation error is thrown when the value is null.
            @NotEmpty
                    String statusID) throws IOException {

//        //For troubleshooting, logging can be enabled
//        Logger logger = Logger.getLogger("TwitterPackage");
//        FileHandler fileHandler = new FileHandler("C:\\temp\\twitterpackage.log", true);
//        logger.addHandler(fileHandler);
//        SimpleFormatter formatter = new SimpleFormatter();
//        fileHandler.setFormatter(formatter);
//        logger.info(LocalDateTime.now().toString() + " made it past the field definitions");

        //Internal validation disallow empty strings or strings greater than 280 characters.
        if ("".equals(statusID.trim())) {
            throw new BotCommandException("Please make sure a statusID provided for retweeting");
        }

        //Validate Session and pull values
        Map<String, String> sessionValues = (Map<String, String>) sessionMap.get(SessionName);
        if (sessionValues.get("SessionName") != SessionName)
            throw new BotCommandException("Session " + SessionName + " does not exist. Please be sure to use to CreateSession action before using any subsequent action.");
        //Set return value for user as blank
        String result = "";
        //Update Status
        try {
            //Twitter4J configuration setting
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(sessionValues.get("ConsumerKey"))
                    .setOAuthConsumerSecret(sessionValues.get("ConsumerSecret"))
                    .setOAuthAccessToken(sessionValues.get("AccessToken"))
                    .setOAuthAccessTokenSecret(sessionValues.get("TokenSecret"));
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();
            try{
                //Retweet Status based on ID
                Status retweetResult = twitter.retweetStatus(Long.parseLong(statusID));
                //Store result with new Tweet ID
                result = "Retweeted successfully: id: " + retweetResult.getId();
            }catch (Exception e){
                if (e.toString().contains("request is understood")){
                    //Tweet has previously been retweeted
                    result = "This tweet has already been retweeted by this account";
                }
                else {
                    throw new BotCommandException("Error in retweeting provided tweet: " + e.toString());
                }
            }

        } catch (Exception e) {
            throw new BotCommandException("Error occurred retweeting on twitter: " + e.toString());
        }
        //Return result to user
        return new StringValue(result);
    }
    // Ensure that a public setter exists.
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
