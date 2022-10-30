// package com.maven.eclipse;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class Reduce extends Reducer<IntWritable, RecommendedFriend, IntWritable, Text> {

    /**
    * @param key        the key corresponding to person we want to recommend other friends
    * @param recFriends all the potential recommended friends that were generated through the map
    * @param context    context object, used to output the final recommendation
    * Reduce function, that filters out potential recommendations that are already friends and sorts the rest by number
    * of mutual friends to get the top 10 recommended friends.
    */
    @Override
    public void reduce(IntWritable key, Iterable<RecommendedFriend> recFriends, Context context)
            throws IOException, InterruptedException {

        // Key is the recommended friend, and value is the list of mutual friends
        HashMap<Integer, List<Integer>> mutualFriends = new HashMap<Integer, List<Integer>>();

        for (RecommendedFriend value : recFriends) {
            Integer user = value.recommendedFriend;
            Integer mutualFriend = value.mutualFriend;
            Boolean isAlreadyFriend;
            isAlreadyFriend = value.mutualFriend == -1;

            // Recommended user has already been added
            if (mutualFriends.containsKey(user) && isAlreadyFriend)
                 mutualFriends.put(user, null);
            else if (mutualFriends.containsKey(user) && mutualFriends.get(user) != null)
                 mutualFriends.get(user).add(mutualFriend);
            else if (!isAlreadyFriend) {
                // Recommended user has not been added yet
                ArrayList<Integer> mutualFriendsList = new ArrayList<Integer>();
                mutualFriendsList.add(mutualFriend);
                mutualFriends.put(user, mutualFriendsList);
            }
        }

        // TreeMap with automatic sorting in descending order when adding new elements
        SortedMap<Integer, List<Integer>> sortedMutualFriends = new TreeMap<Integer, List<Integer>>(new Comparator<Integer>() {
            @Override
            public int compare(Integer key1, Integer key2) {
                Integer i1 = mutualFriends.get(key1).size();
                Integer i2 = mutualFriends.get(key2).size();
                return ((i1 > i2) || (i1.equals(i2) && key1 < key2)) ? -1 : 1;
            }
        });

        // Populate sortedMap
        for (Entry<Integer, List<Integer>> entry : mutualFriends.entrySet()) {
            if (entry.getValue() != null)
                sortedMutualFriends.put(entry.getKey(), entry.getValue());
        }

        int index = 0;
        String output_line = "";
        final int RECOMMENDED_FRIENDS_LIMIT = 10;
        
        // Loop through sorted map to build best recommended friends list
        for (Entry<Integer, List<Integer>> entry : sortedMutualFriends.entrySet()) {
        	
        	// Stop looping if limit of recommended friends is reached
        	if (index == RECOMMENDED_FRIENDS_LIMIT)
        		break;
        	
        	// Create output line
        	output_line += (index != 0 ? "," : "") + entry.getKey().toString();
        
        	index++;
        }
        
        // Write output
        context.write(key, new Text(output_line));
    }
}