import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import WebSearchInterface from '../WebSearchInterface';
import { useRagQuery } from '../../hooks/useRagQuery';
import type { RagResponse } from '../../types/rag';


// Mock the useRagQuery hook
vi.mock('../../hooks/useRagQuery', () => ({
  useRagQuery: vi.fn(),
}));

describe('WebSearchInterface', () => {
  const mockResponse: RagResponse = {
    answer: 'This is a test answer',
    webSearchUsed: true,
    sourcesCount: 3,
  };

  const mockExecuteQuery = vi.fn();

  beforeEach(() => {
    vi.resetAllMocks();

    // Default mock implementation
    (useRagQuery as unknown as ReturnType<typeof vi.fn>).mockReturnValue({
      isLoading: false,
      response: null,
      error: null,
      executeQuery: mockExecuteQuery,
    });
  });

  it('renders the search form', () => {
    render(<WebSearchInterface />);

    expect(screen.getByText('AI-RAG-Otes AI Rag Enhanced Notes')).toBeInTheDocument();
    expect(screen.getByLabelText(/ask a question/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /search/i })).toBeInTheDocument();
  });

  it('does not render search results when there is no response', () => {
    render(<WebSearchInterface />);

    expect(screen.queryByText('Answer:')).not.toBeInTheDocument();
  });

  it('renders search results when there is a response', () => {
    // Mock the hook to return a response
    (useRagQuery as unknown as ReturnType<typeof vi.fn>).mockReturnValue({
      isLoading: false,
      response: mockResponse,
      error: null,
      executeQuery: mockExecuteQuery,
    });

    render(<WebSearchInterface />);

    expect(screen.getByText('Answer:')).toBeInTheDocument();
    expect(screen.getByText('This is a test answer')).toBeInTheDocument();
  });

  it('passes the correct props to SearchForm', () => {
    const { rerender } = render(<WebSearchInterface />);

    // Initial state
    const searchButton = screen.getByRole('button', { name: /search/i });
    expect(searchButton).not.toBeDisabled();

    // Update mock to simulate loading state
    (useRagQuery as unknown as ReturnType<typeof vi.fn>).mockReturnValue({
      isLoading: true,
      response: null,
      error: null,
      executeQuery: mockExecuteQuery,
    });

    rerender(<WebSearchInterface />);

    // Button should now be disabled and show "Searching..."
    const loadingButton = screen.getByRole('button', { name: /searching/i });
    expect(loadingButton).toBeDisabled();
  });
});
