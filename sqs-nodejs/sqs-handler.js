'use strict';

// Load the SDK for JavaScript
const AWS = require('aws-sdk');

// Set the region
AWS.config.update({region: 'us-west-2'});

// Create an SQS service object
const sqs = new AWS.SQS({apiVersion: '2012-11-05'});

module.exports.put_message = (event, context, callback) => {

  let params = {
    DelaySeconds: 10,
    MessageAttributes: {
      "Title": {
        DataType: "String",
        StringValue: "The Whistler"
      },
      "Author": {
        DataType: "String",
        StringValue: "John Grisham"
      },
      "WeeksOn": {
        DataType: "Number",
        StringValue: "6"
      }
    },
    MessageBody: "Information about current NY Times fiction bestseller for week of 12/11/2016.",
    QueueUrl: "https://sqs.us-west-2.amazonaws.com/101654863091/dev-demo-test"
  };

  sqs.sendMessage(params, function(err, data) {
    if (err) {
      console.log("Error", err);

      callback(null, {
        statusCode: 200,
        body: JSON.stringify({
          message: 'Message was failed to sent to the queue!',
          error: err
        }),
      });
    } else {
      console.log("Success", data.MessageId);

      callback(null, {
        statusCode: 200,
        body: JSON.stringify({
          message: 'Message was sent to the queue successfully!',
          messageId: data.MessageId
        }),
      });
    }
  });
};
