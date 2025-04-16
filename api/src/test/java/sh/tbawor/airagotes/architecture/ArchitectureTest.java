package sh.tbawor.airagotes.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationRunner;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

/**
 * Tests to validate the onion architecture of the application.
 */
public class ArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("sh.tbawor.airagotes");
    }

    @Test
    public void domainShouldNotDependOnApplicationOrInfrastructure() {
        ArchRule rule = noClasses().that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage("..application..", "..infrastructure..");

        rule.check(importedClasses);
    }

    @Test
    public void applicationShouldNotDependOnInfrastructure() {
        ArchRule rule = noClasses().that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure..");

        rule.check(importedClasses);
    }

    @Test
    public void onionArchitectureShouldBeRespected() {
        ArchRule rule = onionArchitecture()
                .domainModels("..domain.model..")
                .domainServices("..domain.port..")
                .applicationServices("..application..")
                .adapter("persistence", "..infrastructure.persistence..")
                .adapter("web", "..infrastructure.web..")
                .adapter("ai", "..infrastructure.ai..")
                .adapter("confluence", "..infrastructure.confluence..")
                .adapter("file", "..infrastructure.file..")
                .adapter("reranking", "..infrastructure.reranking..")
                .adapter("config", "..infrastructure.config..")
                // Exclude configuration classes and runners from the architecture rules
                .withOptionalLayers(true)
                .ignoreDependency(sh.tbawor.airagotes.KnowledgebaseInitRunner.class, sh.tbawor.airagotes.application.DocumentIngestionService.class)
                .ignoreDependency(sh.tbawor.airagotes.KnowledgebaseInitRunner.class, sh.tbawor.airagotes.infrastructure.file.MarkdownFolderDocumentReaderFactory.class)
                .ignoreDependency(sh.tbawor.airagotes.infrastructure.config.ApplicationConfig.class, sh.tbawor.airagotes.infrastructure.ai.OllamaChatService.class)
                .ignoreDependency(sh.tbawor.airagotes.infrastructure.config.ApplicationConfig.class, sh.tbawor.airagotes.infrastructure.persistence.VectorStoreDocumentRepository.class);

        rule.check(importedClasses);
    }

    @Test
    public void portsShouldBeInterfaces() {
        ArchRule rule = classes().that().resideInAPackage("..domain.port..")
                .should().beInterfaces();

        rule.check(importedClasses);
    }

    @Test
    public void implementationsShouldImplementPorts() {
        ArchRule rule = classes().that().resideInAPackage("..infrastructure..")
                .and().haveNameMatching(".*Service|.*Repository|.*Client")
                .should().dependOnClassesThat().resideInAPackage("..domain.port..");

        rule.check(importedClasses);
    }

    @Test
    public void controllersShouldResideInWebPackage() {
        ArchRule rule = classes().that().haveNameMatching(".*Controller")
                .should().resideInAPackage("..infrastructure.web..");

        rule.check(importedClasses);
    }

    @Test
    public void servicesShouldHaveServiceSuffix() {
        ArchRule rule = classes().that().resideInAPackage("..application..")
                .and().areNotInterfaces()
                .and().doNotImplement(ApplicationRunner.class)
                .and().haveNameNotMatching(".*Scrapper")
                .should().haveNameMatching(".*Service");

        rule.check(importedClasses);
    }

    @Test
    public void repositoriesShouldImplementRepositoryInterface() {
        ArchRule rule = classes().that().haveNameMatching(".*Repository")
                .and().resideInAPackage("..infrastructure.persistence..")
                .should().dependOnClassesThat().resideInAPackage("..domain.port..");

        rule.check(importedClasses);
    }
}
