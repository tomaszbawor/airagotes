import React from 'react';
import { Globe, Database } from 'lucide-react';
import { CardFooter } from '../ui/card';
import type { RagResponse } from '../../types/rag';

interface SearchResultsProps {
  response: RagResponse;
}

/**
 * Component to display search results
 */
export function SearchResults({ response }: SearchResultsProps) {
  return (
    <CardFooter className="flex flex-col">
      <div className="w-full border-t pt-4">
        <h3 className="font-medium mb-2">Answer:</h3>
        <p className="whitespace-pre-line">{response.answer}</p>

        <div className="mt-4 text-sm text-gray-500 flex items-center gap-2">
          {response.webSearchUsed ? (
            <span className="flex items-center">
              <Globe className="h-4 w-4 mr-1" />
              Web search used
            </span>
          ) : (
            <span className="flex items-center">
              <Database className="h-4 w-4 mr-1" />
              Local knowledge only
            </span>
          )}
          <span>|</span>
          <span>{response.sourcesCount} sources</span>
        </div>
      </div>
    </CardFooter>
  );
}
