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
                .filter(doc ->
                        isBetween(doc, request.getCreatedFrom(), request.getCreatedFrom()))
                .filter(doc -> containsAuthor(doc, request.getAuthorIds()))
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
        return anyNull(prefixes) || prefixes.stream()
                .anyMatch(title::startsWith);
    }

    // Only documents containing at least one content string are matched
    private boolean containsContent(Document doc, List<String> contents) {
        String content = doc.getContent();
        return anyNull(contents) || contents.stream()
                .anyMatch(content::contains);
    }

    private boolean isBetween(Document document, Instant from, Instant to) {
        Instant created = document.getCreated();
        return anyNull(from, to) || created.isAfter(from) && created.isBefore(to);
    }

    private boolean containsAuthor(Document doc, List<String> authorIds) {
        return anyNull(authorIds) || authorIds.contains(doc.getAuthor().getId());
    }

    private boolean anyNull(Object... args) {
        return Arrays.stream(args)
                .anyMatch(Objects::isNull);
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