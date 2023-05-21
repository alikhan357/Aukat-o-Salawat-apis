package com.project.api.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.project.api.model.Playlist;
import com.project.api.model.Reminder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PlaylistRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public Playlist save(Playlist playlist){
        dynamoDBMapper.save(playlist);
        return playlist;
    }

    public Optional<Playlist> findByEmail(String email){
        DynamoDBScanExpression expression = new DynamoDBScanExpression();
        expression.addFilterCondition("email",new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(email))
        );

        List<Playlist> playlists = dynamoDBMapper.scan(Playlist.class,expression);

        return playlists.size() == 0 ? Optional.empty() : Optional.of(playlists.get(0));
    }

    public void delete(Integer id){
        dynamoDBMapper.delete(dynamoDBMapper.load(Playlist.class,id));
    }

    public Playlist update (Playlist playlist){
        dynamoDBMapper.save(playlist,
                new DynamoDBSaveExpression().withExpectedEntry("id",
                        new ExpectedAttributeValue(
                                new AttributeValue().
                                        withS(playlist.getId())))
        );

        return playlist;
    }

    public Optional<Playlist> findById(String id){
        Playlist playlist = dynamoDBMapper.load(Playlist.class,id);
        return playlist == null ? Optional.empty() : Optional.of(playlist);
    }
}
