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
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXTAREA;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

//BotCommand makes a class eligible for being considered as an action.
@BotCommand

//CommandPks adds required information to be dispalable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "setstatus", label = "[[SetStatus.label]]",
        node_label = "[[SetStatus.node_label]]", description = "[[SetStatus.description]]", icon = "pkg.svg",

        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "[[SetStatus.return_label]]", return_type = STRING, return_required = true, return_description = "[[SetStatus.return.description]]")
public class SetStatus {
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
            @Pkg(label = "[[SetStatus.statusBody.label]]", description = "[[SetStatus.statusBody.description]]")
            //Ensure that a validation error is thrown when the value is null.
            @NotEmpty
                    String statusBody,

            //Idx 1 would be displayed first, with a text box for entering the value.
            @Idx(index = "3", type = AttributeType.FILE)
            //UI labels.
            @Pkg(label = "[[SetStatus.statusMedia.label]]", description = "[[SetStatus.statusMedia.description]]")
                    String statusMedia) throws IOException {

//        //For troubleshooting, logging can be enabled
//        Logger logger = Logger.getLogger("TwitterPackage");
//        FileHandler fileHandler = new FileHandler("C:\\temp\\twitterpackage.log", true);
//        logger.addHandler(fileHandler);
//        SimpleFormatter formatter = new SimpleFormatter();
//        fileHandler.setFormatter(formatter);
//        logger.info(LocalDateTime.now().toString() + " made it past the field definitions");

        //Internal validation disallow empty strings or strings greater than 280 characters.
        if ("".equals(statusBody.trim()) || statusBody.length() > 280) {
            throw new BotCommandException("Please make sure status body is set and less than or equal to characters");
        }
        List<String> supportedFiles = new ArrayList<>();
        supportedFiles.add("JPG");
        supportedFiles.add("JPEG");
        supportedFiles.add("PNG");
        supportedFiles.add("GIF");
        supportedFiles.add("WEBP");
        supportedFiles.add("MP4");
        supportedFiles.add("MOV");

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
            StatusUpdate status = new StatusUpdate(statusBody);

            String extension = "";

            //Validate if media is included or not
            if (statusMedia != null) {
                //Media Exists, Validate file extension
                if(!statusMedia.trim().isEmpty()) {
                    try {
                        //get file extension
                        extension = FilenameUtils.getExtension(statusMedia);
                        //verify extension is supported
                        if (supportedFiles.contains(extension)) {
                            //set media to status
                            File media = new File(statusMedia);
                            status.setMedia(media);
                        } else {
                            throw new BotCommandException("Please make sure the provided extension is of the expected file format.");
                        }

                    } catch (Exception e) {
                        throw new BotCommandException("Caught Exception: Please make sure the provided extension is of the expected file format.");
                    }
                }
            }

            //Send update to Twitter
            Status tweet = twitter.updateStatus(status);

            //Store result with Tweet ID
            result = "Status updated successfully: id: " + tweet.getId();
        } catch (Exception e) {
            throw new BotCommandException("Error occurred updating status on twitter: " + e.toString());
        }
        //Return result to user
        return new StringValue(result);
    }

    // Ensure that a public setter exists.
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
