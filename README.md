# message-push
A universal and configurable message push service.
What’s incllude ：
1. Cross domain resource sharing permissions have been configured to enable secure data exchange between websites with different domain names.
2. Implemented ConverterFactory to convert database field values into corresponding enumerations.
3. Custom exception handling can distinguish business related exceptions from other exceptions, making it easier to troubleshoot and fix errors.
4. Implement global exception handling logic
5. Implement serializers and deserializers
Implement functions:
  1.Batch data push. Modify the status of the pushed data so that it will not be pushed repeatedly.
  2.Restrict the validity of push data. We consider the production data to be invalid beyond the given time limit and will not push them. When a piece of data is pushed, its c_cancel will be equal to 1 and it will not be pushed again.
  3.Data retransmission. The default resend count is set to 5. If this count is exceeded, the data will be set as invalid
