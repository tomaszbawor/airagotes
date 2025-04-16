package sh.tbawor.airagotes.infrastructure.file;

import org.springframework.stereotype.Component;

import sh.tbawor.airagotes.domain.port.DocumentReader;

/**
 * Factory for creating MarkdownFolderDocumentReader instances.
 * This is a component in the infrastructure layer that creates adapters for the domain.
 */
@Component
public class MarkdownFolderDocumentReaderFactory {

    /**
     * Creates a new MarkdownFolderDocumentReader for the given folder path.
     *
     * @param folderPath the path to the folder containing markdown documents
     * @return a new MarkdownFolderDocumentReader
     */
    public DocumentReader createReader(String folderPath) {
        return new MarkdownFolderDocumentReader(folderPath);
    }
}
