
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name my-test-queue
aws --endpoint-url=http://localhost:4566 s3 mb s3://my-test-bucket
# O Lambda precisa de um IAM Role para a sua execução
aws --endpoint http://localhost:4566 --profile localstack \
  iam create-role \
  --role-name lambda-execution \
  --assume-role-policy-document "{\"Version\": \"2012-10-17\",\"Statement\": [{ \"Effect\": \"Allow\", \"Principal\": {\"Service\": \"lambda.amazonaws.com\"}, \"Action\": \"sts:AssumeRole\"}]}"

  # Com a IAM Role criada, vamos incluir a policy de execução:
aws --endpoint http://localhost:4566 --profile localstack \
  iam attach-role-policy \
  --role-name lambda-execution \
  --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole

# Da acesso para lambda ler e gravar o arquivo do S3
aws --endpoint http://localhost:4566 --profile localstack \
  iam attach-role-policy \
  --role-name lambda-execution \
  --policy-arn arn:aws:iam::aws:policy/AmazonS3FullAccess

#  implantação do Lambda no LocalStack com o seguinte comando:
aws --endpoint http://localhost:4566 --profile localstack \
  --region us-east-1 lambda create-function \
  --function-name sendfile \
  --runtime java17 \
  --handler br.com.softwalter.App \
  --memory-size 128 \
  --zip-file fileb://app/build/libs/app.jar \
  --role arn:aws:iam::000000000000:role/lambda-execution --timeout 30000 \
  --environment "Variables={DATABASE_USERNAME=postgres,DATABASE_PASSWORD=postgres,DATABASE_NAME=postgres,DATABASE_SCHEMA=public,DATABASE_PORT=5432,DATABASE_HOST=localhost}"

# mapeamento de fonte de evento entre a fila SQS e a função Lambda
aws --endpoint http://localhost:4566 --profile localstack \
  lambda create-event-source-mapping \
  --function-name sendfile \
  --batch-size 1 \
  --maximum-batching-window-in-seconds 60 \
  --event-source-arn arn:aws:sqs:us-east-1:000000000000:my-test-queue

# Configuração do gatilho S3 para acionar a função Lambda

aws --endpoint-url=http://localhost:4566 sqs send-message --queue-url http://localhost:4566/000000000000/my-test-queue \
  --message-body "{\"id\":\"2\",\"first_name\":\"Francesca\",\"last_name\":\"Spirritt\",\"email\":\"fspirrittj@cisco.com\",\"gender\":\"Agender\",\"ip_address\":\"34.144.83.205\"}"

# Executando o Lambda e verificando se foi instalado corretamente:
aws --endpoint http://localhost:4566 --profile localstack \
  lambda invoke \
  --function-name validacao out.txt \
  --log-type Tail


DATABASE_USERNAME=postgres;DATABASE_PASSWORD=postgres;DATABASE_NAME=postgres;DATABASE_SCHEMA=public;DATABASE_PORT=5432;DATABASE_HOST=localhost;

{
  "id": "2",
  "first_name": "Francesca",
  "last_name": "Spirritt",
  "email": "fspirrittj@cisco.com",
  "gender": "Agender",
  "ip_address": "34.144.83.205"
}
