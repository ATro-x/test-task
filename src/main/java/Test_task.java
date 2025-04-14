import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested 
 */
public class DocumentManager {

    private final Map<String, Document> dataStore;


    public DocumentManager() {
        dataStore = new HashMap<>();
    }


    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (document.getId() == null)
            document.setId(UUID.randomUUID().toString());

        dataStore.put(document.getId(), document);

        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        return dataStore.values().stream()
                .filter(doc -> doc.getCreated().isAfter(request.getCreatedFrom()))
                .filter(doc -> doc.getCreated().isBefore(request.getCreatedTo()))
                .filter(doc -> request.getAuthorIds().contains(doc.getAuthor().getId()))
                .filter(doc -> containsPrefix(doc, request.getTitlePrefixes()))
                .filter(doc -> containsContent(doc, request.getContainsContents()))
                // Unmodifiable list is returned
                .toList();
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(dataStore.get(id));
    }


    private boolean containsPrefix(Document doc, List<String> prefixes) {
        String title = doc.getTitle();
        return prefixes.stream()
                .anyMatch(title::startsWith);
    }

    // Only documents containing at least one content string are matched
    private boolean containsContent(Document doc, List<String> contents) {
        String content = doc.getContent();
        return contents.stream()
                .anyMatch(content::contains);
    }



    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }



    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }



    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }

}