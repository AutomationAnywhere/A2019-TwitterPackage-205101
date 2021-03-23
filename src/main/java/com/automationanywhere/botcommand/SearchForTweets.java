package com.automationanywhere.botcommand;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import org.apache.commons.io.FilenameUtils;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.*;
import static com.automationanywhere.commandsdk.model.DataType.STRING;
import static com.automationanywhere.commandsdk.model.DataType.TABLE;

//BotCommand makes a class eligible for being considered as an action.
@BotCommand

//CommandPks adds required information to be displayable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "SearchForTweets", label = "[[SearchForTweets.label]]",
        node_label = "[[SearchForTweets.node_label]]", description = "[[SearchForTweets.description]]", icon = "pkg.svg",

        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "[[SearchForTweets.return_label]]", return_type = TABLE, return_required = true)
public class SearchForTweets {
    @Sessions
    private Map<String, Object> sessionMap;

    //Identify the entry point for the action. Returns a Value<String> because the return type is String.
    @Execute
    public Value<Table> action(
            //Idx 1 would be displayed first, with a text box for entering the value.
            @Idx(index = "1", type = TEXT)
            //UI labels.
            @Pkg(label = "[[CreateSession.sessionName.label]]", default_value_type = STRING, default_value = "Default")
            //Ensure that a validation error is thrown when the value is null.
            @NotEmpty
                    String SessionName,
            //Search Criteria
            @Idx(index = "2", type = TEXT)
            //UI labels.
            @Pkg(label = "[[SearchForTweets.searchCriteria.label]]", description = "[[SearchForTweets.searchCriteria.description]]")
            //Ensure that a validation error is thrown when the value is null.
            @NotEmpty
                    String searchCriteria,
            //Max search result
            @Idx(index = "3", type = NUMBER)
            //UI labels.
            @Pkg(label = "[[SearchForTweets.searchCount.label]]", description = "[[SearchForTweets.searchCount.description]]",default_value_type = DataType.NUMBER, default_value = "20")
            //Ensure that a validation error is thrown when the value is null.
            @NotEmpty
                    Double dblsearchCount){
        //Internal validation disallow empty strings or strings greater than 280 characters.
        if ("".equals(searchCriteria.trim())) {
            throw new BotCommandException("Please make sure the search criteria has been set");
        }

        //Internal validation - searchCount must be >0 and <100
        Integer searchCount = dblsearchCount.intValue();
        if (searchCount <1 || searchCount >100) {
            throw new BotCommandException("Please make sure the max search result count is within the range of 1-100");
        }

        //Validate Session and pull values
        Map<String, String> sessionValues = (Map<String, String>) sessionMap.get(SessionName);
        if (sessionValues.get("SessionName") != SessionName)
            throw new BotCommandException("Session " + SessionName + " does not exist. Please be sure to use to CreateSession action before using any subsequent action.");
        //Set return value for user as blank
        String result = "";

        Table searchResult = new Table();
        List<Schema> header = new ArrayList<Schema>();
        header.add(new Schema("createdAt"));
        header.add(new Schema("TwitterHandle"));
        header.add(new Schema("Name"));
        header.add(new Schema("UserID"));
        header.add(new Schema("FollowersCount"));
        header.add(new Schema("id"));
        header.add(new Schema("text"));
        header.add(new Schema("isRetweetedByMe"));
        header.add(new Schema("retweetCount"));
        header.add(new Schema("isFavorited"));
        header.add(new Schema("favoriteCount"));
        header.add(new Schema("isPossiblySensitive"));
        header.add(new Schema("language"));
        searchResult.setSchema(header);

        List<Row> allRows = new ArrayList<Row>();

        //Search for Tweets
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
            //Create Query object with search criteria
            Query query = new Query(searchCriteria);
            //set Query max result count
            query.setCount(searchCount);
            //Execute search
            QueryResult queryResult = twitter.search(query);

            //Loop through search results
            for (Status status : queryResult.getTweets()){
                //create List for current row data
                List<Value> currentRow = new ArrayList<>();
                //Get values from current Status Update
                currentRow.add(new StringValue(status.getCreatedAt().toString()));
                currentRow.add(new StringValue(status.getUser().getScreenName()));
                currentRow.add(new StringValue(status.getUser().getName()));
                currentRow.add(new StringValue(String.valueOf(status.getUser().getId())));
                currentRow.add(new StringValue(String.valueOf(status.getUser().getFollowersCount())));
                currentRow.add(new StringValue(String.valueOf(status.getId())));
                currentRow.add(new StringValue(status.getText()));
                currentRow.add(new StringValue(String.valueOf(status.isRetweetedByMe())));
                currentRow.add(new StringValue(String.valueOf(status.getRetweetCount())));
                currentRow.add(new StringValue(String.valueOf(status.isFavorited())));
                currentRow.add(new StringValue(String.valueOf(status.getFavoriteCount())));
                currentRow.add(new StringValue(String.valueOf(status.isPossiblySensitive())));
                currentRow.add(new StringValue(status.getLang()));

                //Set current row data to row
                Row row = new Row();
                row.setValues(currentRow);
                //Commit current row to allRows
                allRows.add(row);
            }
            //Add allRows to Table
            searchResult.setRows(allRows);

         } catch (Exception e) {
            throw new BotCommandException("Error occurred in search: " + e.toString());
        }
        //Return result to user
        return new TableValue(searchResult);
    }
    // Ensure that a public setter exists.
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
