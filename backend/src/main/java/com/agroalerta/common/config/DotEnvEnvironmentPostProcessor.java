package com.agroalerta.common.config;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

/**
 * Carga {@code backend/.env} (formato {@code KEY=VALUE}) si existe, para que la
 * aplicación arranque igual con {@code run.ps1}, con {@code mvn spring-boot:run}
 * a mano o desde el IDE.
 *
 * <p>Se añade con {@code addLast}: las variables de entorno reales tienen
 * siempre prioridad sobre el fichero.
 */
public class DotEnvEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String SOURCE_NAME = "dotenv";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {
        if (environment.getPropertySources().contains(SOURCE_NAME)) {
            return;
        }
        Path file = locate();
        if (file == null) {
            return;
        }
        Properties props = new Properties();
        try (InputStreamReader reader = new InputStreamReader(
                Files.newInputStream(file), StandardCharsets.UTF_8)) {
            props.load(reader);
        } catch (IOException ex) {
            return;
        }
        if (props.isEmpty()) {
            return;
        }
        environment.getPropertySources().addLast(
                new PropertiesPropertySource(SOURCE_NAME, props));
    }

    /** Busca .env en el directorio de trabajo y en backend/. */
    private Path locate() {
        Path cwd = Paths.get(System.getProperty("user.dir", "."));
        Path parent = cwd.getParent();
        Path[] candidates = {
                cwd.resolve(".env"),
                cwd.resolve("backend").resolve(".env"),
                parent == null ? null : parent.resolve("backend").resolve(".env")
        };
        for (Path candidate : candidates) {
            if (candidate != null && Files.isRegularFile(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
