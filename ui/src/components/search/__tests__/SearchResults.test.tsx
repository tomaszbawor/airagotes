import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { SearchResults } from '../SearchResults';
import type { RagResponse } from '../../../types/rag';

describe('SearchResults', () => {
  const createMockResponse = (overrides?: Partial<RagResponse>): RagResponse => ({
    answer: 'This is a test answer',
    webSearchUsed: true,
    sourcesCount: 3,
    ...overrides,
  });

  it('renders the answer correctly', () => {
    const mockResponse = createMockResponse();
    render(<SearchResults response={mockResponse} />);

    expect(screen.getByText('Answer:')).toBeInTheDocument();
    expect(screen.getByText('This is a test answer')).toBeInTheDocument();
  });

  it('shows web search indicator when webSearchUsed is true', () => {
    const mockResponse = createMockResponse({ webSearchUsed: true });
    render(<SearchResults response={mockResponse} />);

    expect(screen.getByText('Web search used')).toBeInTheDocument();
    expect(screen.queryByText('Local knowledge only')).not.toBeInTheDocument();
  });

  it('shows local knowledge indicator when webSearchUsed is false', () => {
    const mockResponse = createMockResponse({ webSearchUsed: false });
    render(<SearchResults response={mockResponse} />);

    expect(screen.getByText('Local knowledge only')).toBeInTheDocument();
    expect(screen.queryByText('Web search used')).not.toBeInTheDocument();
  });

  it('displays the correct number of sources', () => {
    const mockResponse = createMockResponse({ sourcesCount: 5 });
    render(<SearchResults response={mockResponse} />);

    expect(screen.getByText('5 sources')).toBeInTheDocument();
  });
});
