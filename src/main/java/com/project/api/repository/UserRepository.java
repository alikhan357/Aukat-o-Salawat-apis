package com.project.api.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.project.api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public User save(User user){
        dynamoDBMapper.save(user);
        return user;
    }

    public Optional<User> findByEmail(String email){
        DynamoDBScanExpression expression = new DynamoDBScanExpression();
        expression.addFilterCondition("email",new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(email))
        );

        List<User> user = dynamoDBMapper.scan(User.class,expression);

        return user.size() == 0 ? Optional.empty() : Optional.of(user.get(0));
    }

    public void delete(Integer id){
        dynamoDBMapper.delete(dynamoDBMapper.load(User.class,id));
    }

    public User update (User user){
        dynamoDBMapper.save(user,
                new DynamoDBSaveExpression().withExpectedEntry("id",
                                new ExpectedAttributeValue(
                                        new AttributeValue().
                                                withS(user.getId())))
        );

        return user;
    }

    public Optional<User> findById(String id){
        User user = dynamoDBMapper.load(User.class,id);
        return user == null ? Optional.empty() : Optional.of(user);
    }
}
