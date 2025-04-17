import React from 'react';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';

interface SearchFormProps {
  query: string;
  setQuery: (query: string) => void;
  useWebSearch: boolean;
  setUseWebSearch: (useWebSearch: boolean) => void;
  isLoading: boolean;
  onSubmit: (e: React.FormEvent) => void;
}

/**
 * Form component for the search interface
 */
export function SearchForm({
  query,
  setQuery,
  useWebSearch,
  setUseWebSearch,
  isLoading,
  onSubmit,
}: SearchFormProps) {
  return (
    <form onSubmit={onSubmit} className="space-y-4" role="form">
      <div className="space-y-2">
        <Label htmlFor="query">Ask a question</Label>
        <Input
          id="query"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="What would you like to know?"
          className="w-full"
        />
      </div>

      <div className="flex items-center space-x-2">
        <input
          type="checkbox"
          id="useWebSearch"
          checked={useWebSearch}
          onChange={(e) => setUseWebSearch(e.target.checked)}
          className="h-4 w-4"
        />
        <Label htmlFor="useWebSearch" className="text-sm cursor-pointer">
          Include web search results
        </Label>
      </div>

      <Button type="submit" disabled={isLoading} className="w-full">
        {isLoading ? 'Searching...' : 'Search'}
      </Button>
    </form>
  );
}
