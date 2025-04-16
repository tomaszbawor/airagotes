import { useState } from 'react';
import { Search, Globe, Database } from 'lucide-react';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Card, CardHeader, CardTitle, CardContent, CardFooter } from './ui/card';
import { Label } from './ui/label';

export default function WebSearchInterface() {
  const [query, setQuery] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [response, setResponse] = useState(null);
  const [useWebSearch, setUseWebSearch] = useState(true);

  const handleSubmit = async (e: any) => {
    e.preventDefault();
    if (!query.trim()) return;

    setIsLoading(true);

    try {
      const response = await fetch('/api/rag/query', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          query,
          useWebSearch,
          webSearchResults: 3,
          topK: 5
        }),
      });

      const data = await response.json();
      setResponse(data);
    } catch (error) {
      console.error('Error querying RAG:', error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="w-full max-w-3xl mx-auto p-4">
      <Card>
        <CardHeader>
          <CardTitle className="text-xl flex items-center gap-2">
            <Search className="h-5 w-5" />
            AI-RAG-Otes AI Rag Enhanced Notes
          </CardTitle>
        </CardHeader>

        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
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
        </CardContent>

        {response && (
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
        )}
      </Card>
    </div>
  );
}
