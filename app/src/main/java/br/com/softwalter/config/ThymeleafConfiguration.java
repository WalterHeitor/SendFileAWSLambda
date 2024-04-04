package br.com.softwalter.config;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class ThymeleafConfiguration {

    public TemplateEngine templateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        return templateEngine;
    }

    private ClassLoaderTemplateResolver templateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/"); // Caminho para os templates dentro da pasta resources
        templateResolver.setSuffix(".html"); // Extensão dos arquivos de template
        templateResolver.setTemplateMode("HTML"); // Modo do template (HTML neste caso)
        templateResolver.setCharacterEncoding("UTF-8"); // Codificação dos templates
        return templateResolver;
    }
}
