package mops.businesslogic.config;

import mops.businesslogic.query.FileQueryConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConverterRegistry implements WebMvcConfigurer {

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(FileQueryConverter.INSTANCE);
    }
}
