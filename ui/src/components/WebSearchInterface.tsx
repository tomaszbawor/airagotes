import { useState } from 'react';
import { Search } from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from './ui/card';
import { SearchForm } from './search/SearchForm';
import { SearchResults } from './search/SearchResults';
import { useRagQuery } from '../hooks/useRagQuery';

export default function WebSearchInterface() {
  const [query, setQuery] = useState('');
  const [useWebSearch, setUseWebSearch] = useState(true);
  const { isLoading, response, executeQuery } = useRagQuery();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!query.trim()) return;

    await executeQuery({
      query,
      useWebSearch,
      webSearchResults: 3,
      topK: 5
    });
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
          <SearchForm
            query={query}
            setQuery={setQuery}
            useWebSearch={useWebSearch}
            setUseWebSearch={setUseWebSearch}
            isLoading={isLoading}
            onSubmit={handleSubmit}
          />
        </CardContent>

        {response && <SearchResults response={response} />}
      </Card>
    </div>
  );
}
