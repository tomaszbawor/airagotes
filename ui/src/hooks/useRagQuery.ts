import { useState } from 'react';
import type { RagQueryParams, RagResponse } from '../types/rag';

/**
 * Custom hook for querying the RAG API
 *
 * @returns {Object} The hook state and methods
 */
export function useRagQuery() {
  const [isLoading, setIsLoading] = useState(false);
  const [response, setResponse] = useState<RagResponse | null>(null);
  const [error, setError] = useState<Error | null>(null);

  /**
   * Execute a query to the RAG API
   *
   * @param {RagQueryParams} params - The query parameters
   * @returns {Promise<RagResponse | null>} The response or null if there was an error
   */
  const executeQuery = async (params: RagQueryParams): Promise<RagResponse | null> => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await fetch('/api/rag/query', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(params),
      });

      if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
      }

      const data = await response.json();
      setResponse(data);
      return data;
    } catch (err) {
      const error = err instanceof Error ? err : new Error('Unknown error occurred');
      console.error('Error querying RAG:', error);
      setError(error);
      return null;
    } finally {
      setIsLoading(false);
    }
  };

  return {
    isLoading,
    response,
    error,
    executeQuery,
  };
}
