package com.project.api.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.*;
import com.project.api.model.Reminder;
import com.project.api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ReminderRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public Reminder save(Reminder reminder){
        dynamoDBMapper.save(reminder);
        return reminder;
    }

    public Optional<Reminder> findByEmail(String email){
        DynamoDBScanExpression expression = new DynamoDBScanExpression();
        expression.addFilterCondition("email",new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(email))
        );

        List<Reminder> reminders = dynamoDBMapper.scan(Reminder.class,expression);

        return reminders.size() == 0 ? Optional.empty() : Optional.of(reminders.get(0));
    }

    public Optional<Reminder> findByEmailAndNamaz(String email, String namaz) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":email", new AttributeValue().withS(email));
        expressionAttributeValues.put(":namaz", new AttributeValue().withS(namaz));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("email = :email AND #namaz = :namaz")
                .withExpressionAttributeNames(Map.of("#namaz", "namaz"))
                .withExpressionAttributeValues(expressionAttributeValues);

        List<Reminder> reminders = dynamoDBMapper.scan(Reminder.class,scanExpression);

        return reminders.size() == 0 ? Optional.empty() : Optional.of(reminders.get(0));
    }

    public void delete(Integer id){
        dynamoDBMapper.delete(dynamoDBMapper.load(Reminder.class,id));
    }

    public Reminder update (Reminder reminder){
        dynamoDBMapper.save(reminder,
                new DynamoDBSaveExpression().withExpectedEntry("id",
                        new ExpectedAttributeValue(
                                new AttributeValue().
                                        withS(reminder.getId())))
        );

        return reminder;
    }

    public Optional<Reminder> findById(String id){
        Reminder reminder = dynamoDBMapper.load(Reminder.class,id);
        return reminder == null ? Optional.empty() : Optional.of(reminder);
    }
}
