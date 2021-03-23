package com.automationanywhere.botcommand;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.core.security.SecureString;

import java.util.HashMap;
import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.CREDENTIAL;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

//BotCommand makes a class eligible for being considered as an action.
@BotCommand

//CommandPks adds required information to be displayable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "createsession", label = "[[CreateSession.label]]",
        node_label = "[[CreateSession.node_label]]", description = "[[CreateSession.description]]", icon = "pkg.svg",

        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "[[CreateSession.return_label]]", return_type = STRING, return_required = true)
public class CreateSession {

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

            @Idx(index = "2", type = CREDENTIAL)
            @Pkg(label = "[[CreateSession.consumerKey.label]]", description = "[[CreateSession.fieldDescription]]")
            @NotEmpty
                    SecureString ConsumerKey,

            @Idx(index = "3", type = CREDENTIAL)
            //UI labels.
            @Pkg(label = "[[CreateSession.consumerSecret.label]]", description = "[[CreateSession.fieldDescription]]")
            //Ensure that a validation error is thrown when the value is null.
            @NotEmpty
                    SecureString ConsumerSecret,

            @Idx(index = "4", type = CREDENTIAL)
            @Pkg(label = "[[CreateSession.accessToken.label]]", description = "[[CreateSession.fieldDescription]]")
            @NotEmpty
                    SecureString AccessToken,

            @Idx(index = "5", type = CREDENTIAL)
            @Pkg(label = "[[CreateSession.tokenSecret.label]]", description = "[[CreateSession.fieldDescription]]")
            @NotEmpty
                    SecureString TokenSecret) {

        //Internal validation, to disallow empty strings. No null check needed as we have NotEmpty on all fields.
        if ("".equals(SessionName.trim()) || "".equals(ConsumerKey.getInsecureString().trim()) || "".equals(ConsumerSecret.getInsecureString().trim()) || "".equals(AccessToken.getInsecureString().trim()) || "".equals(TokenSecret.getInsecureString().trim()))
            throw new BotCommandException("Please make sure all input fields are filled with non-null values");


        String result = "";
        //Saving values to session
        try {
            Map<String, String> sessionValues = new HashMap<String, String>();
            //Adding all session variables to a MAP object for later retrieval in subsequent actions
            sessionValues.put("SessionName", SessionName);
            sessionValues.put("ConsumerKey", ConsumerKey.getInsecureString());
            sessionValues.put("ConsumerSecret", ConsumerSecret.getInsecureString());
            sessionValues.put("AccessToken", AccessToken.getInsecureString());
            sessionValues.put("TokenSecret", TokenSecret.getInsecureString());

            //Create session - must be String/Object Map
            //SessionName is a string, sessionValues is a map (object) which has session name, and other session values.
            sessionMap.put(SessionName, sessionValues);

            //Set String result for session creation
            result = "Successfully Created Session";
        } catch (Exception e) {
            result = "Error occurred in establishing session:" + e.toString();
            throw new BotCommandException("Error occured updating status on twitter: " + e.toString());
        }

        //Return StringValue.
        return new StringValue(result);
    }
    // Ensure that a public setter exists.
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
