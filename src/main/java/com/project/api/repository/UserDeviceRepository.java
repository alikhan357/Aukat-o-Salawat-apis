package com.project.api.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.project.api.model.User;
import com.project.api.model.UserDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDeviceRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public UserDevice save(UserDevice userDevice){
        dynamoDBMapper.save(userDevice);
        return userDevice;
    }

    public Optional<UserDevice> findByDevice(String deviceId){
        DynamoDBScanExpression expression = new DynamoDBScanExpression();
        expression.addFilterCondition("deviceId",new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(deviceId))
        );

        List<UserDevice> user = dynamoDBMapper.scan(UserDevice.class,expression);

        return user.size() == 0 ? Optional.empty() : Optional.of(user.get(0));
    }

    public void delete(Integer id){
        dynamoDBMapper.delete(dynamoDBMapper.load(UserDevice.class,id));
    }

    public UserDevice update (UserDevice user){
        dynamoDBMapper.save(user,
                new DynamoDBSaveExpression().withExpectedEntry("id",
                                new ExpectedAttributeValue(
                                        new AttributeValue().
                                                withS(user.getId())))
        );

        return user;
    }

    public Optional<UserDevice> findById(String id){
        UserDevice user = dynamoDBMapper.load(UserDevice.class,id);
        return user == null ? Optional.empty() : Optional.of(user);
    }
}
