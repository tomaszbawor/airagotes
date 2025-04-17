package sh.tbawor.airagotes.infrastructure.reranking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import sh.tbawor.airagotes.domain.model.Document;
import sh.tbawor.airagotes.domain.port.RerankerService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the RerankerService interface using a simple reranking algorithm.
 * This service reranks documents based on keyword matching and source prioritization.
 */
@Service
public class SimpleRerankerService implements RerankerService {

    private static final Logger log = LoggerFactory.getLogger(SimpleRerankerService.class);
    private static final double VECTOR_STORE_WEIGHT = 0.7;
    private static final double KEYWORD_MATCH_WEIGHT = 0.5;
    private static final double SOURCE_WEIGHT = 0.5;

    @Override
    public List<Document> rerank(List<Document> documents, String query) {
        log.info("Reranking {} documents for query: {}", documents.size(), query);

        if (documents.isEmpty()) {
            return documents;
        }

        // Extract keywords from the query
        List<String> keywords = extractKeywords(query);

        // Calculate scores for each document
        Map<Document, Double> scores = new HashMap<>();

        for (Document document : documents) {
            double score = calculateScore(document, keywords);
            scores.put(document, score);
        }

        // Sort documents by score (descending)
        List<Document> rerankedDocuments = documents.stream()
                .sorted(Comparator.comparing(scores::get).reversed())
                .collect(Collectors.toList());

        log.info("Reranking complete. First document score: {}",
                rerankedDocuments.isEmpty() ? 0 : scores.get(rerankedDocuments.get(0)));

        return rerankedDocuments;
    }

    private List<String> extractKeywords(String query) {
        // Simple keyword extraction: split by spaces and filter out common words
        String[] words = query.toLowerCase().split("\\s+");
        List<String> keywords = new ArrayList<>();

        for (String word : words) {
            // Filter out common words and short words
            if (word.length() > 3 && !isCommonWord(word)) {
                keywords.add(word);
            }
        }

        return keywords;
    }

    private boolean isCommonWord(String word) {
        // List of common words to filter out
        String[] commonWords = {"the", "and", "for", "with", "that", "this", "what", "which", "how"};

        for (String commonWord : commonWords) {
            if (commonWord.equals(word)) {
                return true;
            }
        }

        return false;
    }

    private double calculateScore(Document document, List<String> keywords) {
        // Calculate keyword match score
        double keywordScore = calculateKeywordScore(document, keywords);

        // Calculate source score
        double sourceScore = calculateSourceScore(document);

        // Combine scores
        return (keywordScore * KEYWORD_MATCH_WEIGHT) + (sourceScore * SOURCE_WEIGHT);
    }

    private double calculateKeywordScore(Document document, List<String> keywords) {
        if (keywords.isEmpty()) {
            return 0.5; // Neutral score if no keywords
        }

        String content = document.getContent().toLowerCase();
        int matches = 0;

        for (String keyword : keywords) {
            if (content.contains(keyword)) {
                matches++;
            }
        }

        return (double) matches / keywords.size();
    }

    private double calculateSourceScore(Document document) {
        Map<String, Object> metadata = document.getMetadata();

        if (metadata == null) {
            return 0.5; // Neutral score if no metadata
        }

        Object source = metadata.get("source");

        if (source == null) {
            return 0.5; // Neutral score if no source
        }

        // All documents are from vector store now
        return VECTOR_STORE_WEIGHT;
    }
}
