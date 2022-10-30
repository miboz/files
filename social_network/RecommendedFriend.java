// package com.maven.eclipse;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class RecommendedFriend implements Writable {
	public Integer recommendedFriend;
	public Integer mutualFriend;

    /**
    * RecommendedFriend Constructor
    * @param Integer recommendedFriend the recommended friend
    * @param Integer mutualFriend the mutual friend
    * Builds a RecommendedFriend object with the specified parameters
    */
	public RecommendedFriend(Integer recommendedFriend, Integer mutualFriend) {
		this.recommendedFriend = recommendedFriend;
		this.mutualFriend = mutualFriend;
	}

    /**
    * RecommendedFriend Constructor
    * Builds a RecommendedFriend object with the default parameters (-1, -1)
    */
	public RecommendedFriend() {
		this.recommendedFriend = new Integer(-1);
		this.mutualFriend = new Integer(-1);
	}

    /**
    * @param input  the output to be written to
    * Writes two saved value to an output
    */
	@Override
	public void write(DataOutput output) throws IOException {
		output.writeInt(recommendedFriend);
		output.writeInt(mutualFriend);
	}

    /**
    * @param input  the input to be red
    * Reads the two fields of an input and stores them
    */
	@Override
	public void readFields(DataInput input) throws IOException {
		recommendedFriend = input.readInt();
		mutualFriend = input.readInt();
	}
}