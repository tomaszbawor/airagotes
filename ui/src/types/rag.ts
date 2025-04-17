/**
 * Types for the RAG (Retrieval-Augmented Generation) API
 */

/**
 * Request parameters for the RAG query API
 */
export interface RagQueryParams {
  query: string;
  useWebSearch: boolean;
  webSearchResults?: number;
  topK?: number;
}

/**
 * Response from the RAG query API
 */
export interface RagResponse {
  answer: string;
  webSearchUsed: boolean;
  sourcesCount: number;
}
