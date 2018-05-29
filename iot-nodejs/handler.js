'use strict';

//import AWS from 'aws-sdk';
var AWS = require('aws-sdk');

const attachPrincipalPolicy = (policyName, principal) => {
  const iot = new AWS.Iot();
  const params = { policyName, principal };
  return iot.attachPrincipalPolicy(params).promise();
};

const createPolicy = (policyDocument, policyName) => {
  const iot = new AWS.Iot();
  const params = { policyDocument, policyName };
  return iot.createPolicy(params).promise();
};

const buildResponse = (statusCode, body) => ({
  statusCode,
  headers: {
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Credentials': true,
  },
  body: JSON.stringify(body),
});

module.exports.hello = (event, context, callback) => {
  console.log('input event', event);

  const principal = event.requestContext.identity.cognitoIdentityId;
  console.log('principal', principal);

  // const iotPolicy = 'arn:aws:iam::aws:policy/AWSIoTDataAccess';
  
  const iot = new AWS.Iot();

  console.log('Attaching IoT Policy to principal.');
  iot.attachPrincipalPolicy({
    policyName: 'IotPolicy',
    principal
  }, (error, data) => {
    if (error) {
      if (error.statusCode === 409) {
        // Policy already exists for this cognito identity
        callback(null, buildResponse(200, { status: true, error: error }));
      } else {
        console.log('error', error);
        callback(null, buildResponse(500, { status: false, error: error }));
      }
    } else {
      console.log('Complete attaching.');
      callback(null, buildResponse(200, { status: true, data: data }));
    }
  });


  // try {
  //   const iot = new AWS.Iot();

  //   console.log('Attaching IoT Policy to principal.');
  //   iot.attachPrincipalPolicy({}, (error, data) => {
  //     if (error) {
  //       if (error.statusCode === 409) {
  //         // Policy already exists for this cognito identity
  //         callback(null, buildResponse(200, { status: true }));
  //       } else {
  //         console.log('error', error);
  //         callback(null, buildResponse(500, { status: false, error: error }));
  //       }
  //     } else {
  //       console.log('Complete attaching.');
  //       callback(null, buildResponse(200, { status: true }));
  //     }
  //   });
  //   var x = iot.attachPrincipalPolicy(iotPolicy, principal).promise();
  //   x.
  //   console.log('Complete attaching.');

  //   callback(null, buildResponse(200, { status: true }));
  // } catch (e) {
  //   if (e.statusCode === 409) {
  //     // Policy already exists for this cognito identity
  //     callback(null, buildResponse(200, { status: true }));
  //   } else {
  //     console.log('error', e);
  //     callback(null, buildResponse(500, { status: false, error: e }));
  //   }
  // }
};
