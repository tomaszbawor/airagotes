package sh.tbawor.airagotes.domain.port;

import sh.tbawor.airagotes.domain.model.ConfluencePage;

import java.util.List;

/**
 * Port for retrieving pages from Confluence.
 */
public interface ConfluenceClient {

  /**
   * Retrieves pages from a specified Confluence space.
   *
   * @param spaceKey the key of the Confluence space
   * @param limit the maximum number of pages to retrieve
   * @return a list of Confluence pages
   */
  List<ConfluencePage> getPagesFromSpace(String spaceKey, int limit);
}
