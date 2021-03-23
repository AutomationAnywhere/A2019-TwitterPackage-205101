package com.automationanywhere.botcommand;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import org.apache.commons.io.FilenameUtils;
import twitter4j.*;
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

//CommandPks adds required information to be dispalable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "FollowUser", label = "[[FollowUser.label]]",
        node_label = "[[FollowUser.node_label]]", description = "[[FollowUser.description]]", icon = "pkg.svg",

        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "[[FollowUser.return_label]]", return_type = STRING, return_required = true, return_description = "[[FollowUser.return.description]]")

public class FollowUser {
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
            @Idx(index = "2", type = TEXT)
            //UI labels.
            @Pkg(label = "[[FollowUser.username.label]]", description = "[[FollowUser.username.description]]")
            //Ensure that a validation error is thrown when the value is null.
            @NotEmpty
                    String userName) throws IOException {

//        //For troubleshooting, logging can be enabled
//        Logger logger = Logger.getLogger("TwitterPackage");
//        FileHandler fileHandler = new FileHandler("C:\\temp\\twitterpackage.log", true);
//        logger.addHandler(fileHandler);
//        SimpleFormatter formatter = new SimpleFormatter();
//        fileHandler.setFormatter(formatter);
//        logger.info(LocalDateTime.now().toString() + " made it past the field definitions");

        //Internal validation disallow empty strings or strings greater than 280 characters.
        if ("".equals(userName.trim())) {
            throw new BotCommandException("Please make sure a valid user/userID to follow is provided");
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
                //Validate if username or userID was provided
                if(isNumeric(userName)){
                    //Create friendship (follow) based on UserID
                    Long userID = Long.parseLong(userName);
                    User user = twitter.createFriendship(userID);
                    result = "Now Following: " + user.getScreenName();
                } else {
                    //Create friendship (follow) based on username
                    User user = twitter.createFriendship(userName);
                    result = "Now Following: " + user.getScreenName();
                }
            } catch (Exception e){
                result = "Attempt to follow " + userName + " was unsuccessful. You may be following them already. Full Error Body:" + e.toString();
            }

        } catch (Exception e) {
            throw new BotCommandException("Error occurred while attempting to follow: " + e.toString());
        }
        //Return result to user
        return new StringValue(result);
    }

    // Ensure that a public setter exists.
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }

    //function for validating if username vs userID
    private static boolean isNumeric(String str){
        try{
            Long testID = Long.parseLong(str);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
