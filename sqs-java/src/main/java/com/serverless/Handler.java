package com.serverless;

import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;

import com.amazon.sqs.javamessaging.AmazonSQSExtendedClient;
import com.amazon.sqs.javamessaging.ExtendedClientConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private static final Logger LOG = Logger.getLogger(Handler.class);

    // Default AWS Region
    public static final Regions DEFAULT_REGION = Regions.US_WEST_2;

    // S3 bucket name (need to create first.)
    public static final String S3_BUCKET_NAME = "mec-edi-data";

    // SQS name (standard queue is enough. need to create first.)
    public static final String QUEUE_NAME = "dev-demo-test";
    
	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
	    Response responseBody = null;
		try {
		    LOG.info("received: " + input);

		    //TODO: retrieve payload
	        String payload = "Hello World!";
	        
	        // Create S3 client
	        final AmazonS3ClientBuilder s3ClientBuilder = AmazonS3ClientBuilder.standard().withRegion(DEFAULT_REGION);
	        final AmazonS3 s3 = s3ClientBuilder.build();
	        
	        /*
	         * Set the Amazon SQS extended client configuration with large payload
	         * support enabled.
	         */
	        final ExtendedClientConfiguration extendedClientConfig = new ExtendedClientConfiguration()
	                .withLargePayloadSupportEnabled(s3, S3_BUCKET_NAME).withAlwaysThroughS3(true);
	        
	        // Create SQS client
	        final AmazonSQSClientBuilder sqsClientBuilder = AmazonSQSClientBuilder.standard().withRegion(DEFAULT_REGION);
	        final AmazonSQS sqsExtended = new AmazonSQSExtendedClient(sqsClientBuilder.build(), extendedClientConfig);

	        //TODO: retrieve queue url
	        String queueUrl = "https://sqs.us-west-2.amazonaws.com/101654863091/dev-demo-test";

	        // Send the message.
	        final SendMessageRequest myMessageRequest = new SendMessageRequest(queueUrl, payload);
	        SendMessageResult result = sqsExtended.sendMessage(myMessageRequest);
	        String messageId = result.getMessageId();

	        LOG.info("Sent the message.  MessageId = " + messageId);
	        
	        responseBody = new Response("Message was put to queue successfully!", input);
		} catch (Throwable e) {
		    responseBody = new Response("There is an error while putting message to the queue!", input, e);
		}

		return ApiGatewayResponse.builder()
				.setStatusCode(200)
				.setObjectBody(responseBody)
				.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
				.build();
	}
}
