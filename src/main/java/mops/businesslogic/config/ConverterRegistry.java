package mops.businesslogic.config;

import mops.businesslogic.file.query.FileQueryConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registers parser to web mvc.
 */
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
