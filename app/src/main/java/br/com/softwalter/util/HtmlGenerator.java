package br.com.softwalter.util;

import br.com.softwalter.entity.Contract;
import br.com.softwalter.entity.Person;
import org.thymeleaf.TemplateEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
public class HtmlGenerator {
    private final TemplateEngine templateEngine;

    public HtmlGenerator(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public List<String> generateHtmlFromTemplates(Person person, List<Contract> contracts) {
        List<String> htmlContents = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<Future<String>> futures = new ArrayList<>();
        for (Contract contract : contracts) {
            Future<String> future = executorService.submit(() -> generateHtml(person, contract));
            futures.add(future);
        }

        for (Future<String> future : futures) {
            try {
                String htmlContent = future.get();
                htmlContents.add(htmlContent);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();
        return htmlContents;
    }

    private String generateHtml(Person person, Contract contract) {
        final org.thymeleaf.context.Context thymeleafContext = new org.thymeleaf.context.Context();
        thymeleafContext.setVariable("person", person);
        thymeleafContext.setVariable("contract", contract);

        String templateName = getTemplateNameForContract(contract);
        return templateEngine.process(templateName, thymeleafContext);
    }

    private String getTemplateNameForContract(Contract contract) {
        switch (contract.getTitle()) {
            case BACKEND_JAVA:
                return "person_contract_template_backend";
            case FRONTEND_ANGULAR:
                return "person_contract_template_frontend";
            case MOBILE_IOS:
                return "person_contract_template_mobile_ios";
            case MOBILE_ANDROID:
                return "person_contract_template_mobile_android";
            case DEVOPS:
                return "person_contract_template_devops";
            default:
                throw new IllegalArgumentException("Invalid contract title");
        }
    }

}
