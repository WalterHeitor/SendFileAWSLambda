package br.com.softwalter;

import br.com.softwalter.config.ThymeleafConfiguration;
import br.com.softwalter.entity.Contract;
import br.com.softwalter.dto.PersonDTO;
import br.com.softwalter.entity.ContractType;
import br.com.softwalter.entity.Person;
import br.com.softwalter.repository.ContractRepository;
import br.com.softwalter.util.HibernateUtil;
import br.com.softwalter.util.HtmlGenerator;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfWriter;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class App implements RequestHandler<SQSEvent, Void> {

    private static final Session SESSION = HibernateUtil.getSessionFactory().openSession();
    private final ContractRepository contractRepository = new ContractRepository(SESSION);
    // Logger para registro de mensagens
    private static final Logger logger = LoggerFactory.getLogger(App.class.getName());

    // Cliente para interagir com o serviço S3 da AWS
    private final AmazonS3 s3 = AmazonS3ClientBuilder
            .standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://s3.us-east-1.localhost.localstack.cloud:4566", "us-east-1")) // Configuração do endpoint localstack
            .withCredentials(new DefaultAWSCredentialsProviderChain())
            .withPathStyleAccessEnabled(true)
            .build();

    // Nome do bucket S3 onde os PDFs serão armazenados
    private static final String BUCKET_NAME = "my-test-bucket";

    // Template Engine para processar os templates Thymeleaf
    private final TemplateEngine templateEngine;

    // ObjectMapper para serialização/deserialização de objetos JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Construtor da classe
    public App() {
        // Inicializa a Template Engine
        templateEngine = new ThymeleafConfiguration().templateEngine();
    }
    public final HtmlGenerator htmlGenerator = new HtmlGenerator(new ThymeleafConfiguration().templateEngine());

    // Método principal para processar eventos SQS
    @Override
    public Void handleRequest(SQSEvent sqsEvent, Context context) {
        try {
            for (SQSEvent.SQSMessage msg : sqsEvent.getRecords()) {
                logger.info("SQS message: {}", msg);
                String messageBody = msg.getBody();
                // Converte a mensagem JSON em um objeto Person
                PersonDTO personDTO = parseMessageToPerson(messageBody);
                logger.debug("personDTO: {}", personDTO.toString());
                Person person = personDTO.toEntity();
                logger.debug("person: {}", person.toString());
                List<Contract> contracts = getContractsFromDatabasePostgres(person.getId());
//                List<Contract> contracts = getContractsFromDatabase(person.getId());
                List<String> htmlContents = htmlGenerator.generateHtmlFromTemplates(person, contracts);
                // Gera o conteúdo HTML a partir do template Thymeleaf e dos dados da pessoa

//                contracts.forEach(contract -> htmlContents.add(generateHtmlFromTemplate(person, contract)));

                // Converte o conteúdo HTML em um arquivo PDF
                byte[] pdfBytes = createPdfFromHtml(htmlContents);
                // Envia o PDF gerado para o serviço S3 da AWS
                sendPdfToS3(pdfBytes);
            }
        } catch (IOException e) {
            // Registra um erro caso ocorra uma exceção de E/S
            logger.error("Erro ao processar evento SQS: {}", e.getMessage());
        } catch (DocumentException e) {
            // Lança uma exceção de tempo de execução caso ocorra um erro ao manipular o documento PDF
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<Contract> getContractsFromDatabasePostgres(Long personId) {

        return contractRepository.findContractsByPersonId(personId);
    }

    public List<Contract> getContractsFromDatabase(Long personId) {
        // Lógica para buscar os contratos do banco de dados
        // Neste exemplo, apenas uma lista fictícia é retornada

        // Lista para armazenar os contratos
        List<Contract> contracts = new ArrayList<>();

        // Adicionando contratos fictícios à lista
        Random random = new Random();
        int i = 0;
        for (ContractType contractType : ContractType.values()) {

            // Gerando IDs fictícios para os contratos
            String contractId = "CON" + (i + 1);

            // Título do contrato fictício

            // Descrição do contrato fictícia
            String description = "Este é o contrato " + contractId + ", que contém detalhes importantes.";

            // Datas de início e término fictícias
            Date startDate = new Date(); // Data atual
            Date endDate = new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30)); // 30 dias a partir de hoje

            // Valor do contrato fictício
            double value = 1000 + random.nextInt(9000); // Valor aleatório entre 1000 e 9999

            // Status do contrato fictício
            String status = random.nextBoolean() ? "Ativo" : "Inativo"; // Status aleatório entre Ativo e Inativo

            // Criando um novo contrato e adicionando à lista
            Contract contract = new Contract(Long.parseLong(contractId), contractType, description, startDate, endDate, value, status);
            contracts.add(contract);
            i++;
        }

        return contracts;
    }

    // Método para criar um documento PDF a partir do conteúdo HTML
    private byte[] createPdfFromHtml(List<String> htmlContents) throws IOException, DocumentException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        htmlContents.forEach(htmlContent -> {

            HTMLWorker htmlWorker = new HTMLWorker(document);
            try {
                htmlWorker.parse(new StringReader(htmlContent));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        document.close();
        return outputStream.toByteArray();
    }

    // Método para converter uma mensagem JSON em um objeto Person
    private PersonDTO parseMessageToPerson(String messageBody) throws JsonProcessingException {
        return objectMapper.readValue(messageBody, PersonDTO.class);
    }

    // Método para gerar o conteúdo HTML a partir de um template Thymeleaf e dos dados da pessoa
    private String generateHtmlFromTemplate(Person person, Contract contract) {
        final org.thymeleaf.context.Context thymeleafContext = new org.thymeleaf.context.Context();
        thymeleafContext.setVariable("person", person);
        thymeleafContext.setVariable("contract", contract);
        if (contract.getTitle() != null) {
            if (contract.getTitle().equals(ContractType.BACKEND_JAVA)) {
                return templateEngine.process("person_contract_template_backend", thymeleafContext);
            } else if (contract.getTitle().equals(ContractType.FRONTEND_ANGULAR)) {
                return templateEngine.process("person_contract_template_frontend", thymeleafContext);
            } else if (contract.getTitle().equals(ContractType.MOBILE_IOS)) {
                return templateEngine.process("person_contract_template_mobile_ios", thymeleafContext);
            } else if (contract.getTitle().equals(ContractType.MOBILE_ANDROID)) {
                return templateEngine.process("person_contract_template_mobile_android", thymeleafContext);
            } else if (contract.getTitle().equals(ContractType.DEVOPS)) {
                return templateEngine.process("person_contract_template_devops", thymeleafContext);

            }
        } else {
            throw new IllegalArgumentException("Invalid contract");
        }

        return null;
    }

    // Método para enviar um arquivo PDF para o serviço S3 da AWS
    private void sendPdfToS3(byte[] pdfBytes) {
        String pdfKey = "pdf_" + System.currentTimeMillis() + ".pdf";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfBytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(pdfBytes.length);
        // Faz o upload do PDF para o bucket S3 especificado
        s3.putObject(BUCKET_NAME, pdfKey, inputStream, metadata);
    }

    // Método main para testar a função localmente
    public static void main(String[] args) {
        // Cria uma instância de SQSEvent.SQSMessage para simular uma mensagem do SQS
        SQSEvent.SQSMessage msg = new SQSEvent.SQSMessage();
        msg.setBody("{\"id\":\"2\", \"first_name\":\"Francesca\", \"last_name\":\"Spirritt\", \"email\":\"fspirrittj@cisco.com\", \"gender\":\"Agender\", \"ip_address\":\"34.144.83.205\"}");

        // Cria uma instância de SQSEvent contendo a mensagem
        SQSEvent sqsEvent = new SQSEvent();
        sqsEvent.setRecords(Collections.singletonList(msg));

        // Cria uma instância de App e invoca o método handleRequest
        App app = new App();
        app.handleRequest(sqsEvent, null);
    }
    // criar algorithm
}
