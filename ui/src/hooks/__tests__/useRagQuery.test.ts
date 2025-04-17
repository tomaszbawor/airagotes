import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { useRagQuery } from '../useRagQuery';
import type { RagResponse } from '../../types/rag';

describe('useRagQuery', () => {
  const mockResponse: RagResponse = {
    answer: 'This is a test answer',
    webSearchUsed: true,
    sourcesCount: 3,
  };

  beforeEach(() => {
    // Reset the fetch mock before each test
    vi.resetAllMocks();
  });

  it('should initialize with default values', () => {
    const { result } = renderHook(() => useRagQuery());

    expect(result.current.isLoading).toBe(false);
    expect(result.current.response).toBeNull();
    expect(result.current.error).toBeNull();
  });

  it('should set isLoading to true while fetching', async () => {
    // Mock a delayed response
    global.fetch = vi.fn().mockImplementation(() =>
      new Promise(resolve => {
        setTimeout(() => {
          resolve({
            ok: true,
            json: () => Promise.resolve(mockResponse),
          });
        }, 100);
      })
    );

    const { result } = renderHook(() => useRagQuery());

    let promise: Promise<RagResponse | null>;

    act(() => {
      promise = result.current.executeQuery({
        query: 'test query',
        useWebSearch: true,
      });
    });

    // Check that isLoading is true immediately after calling executeQuery
    expect(result.current.isLoading).toBe(true);

    // Wait for the promise to resolve
    await promise;

    // Wait for any pending state updates
    await act(async () => {
      // Just waiting for any pending state updates
    });

    // Check that isLoading is false after the promise resolves
    expect(result.current.isLoading).toBe(false);
  });

  it('should set response when fetch is successful', async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(mockResponse),
    });

    const { result } = renderHook(() => useRagQuery());

    await act(async () => {
      await result.current.executeQuery({
        query: 'test query',
        useWebSearch: true,
      });
    });

    expect(result.current.response).toEqual(mockResponse);
    expect(result.current.error).toBeNull();
  });

  it('should set error when fetch fails', async () => {
    const errorMessage = 'API error: 500';
    global.fetch = vi.fn().mockResolvedValue({
      ok: false,
      status: 500,
    });

    const { result } = renderHook(() => useRagQuery());

    await act(async () => {
      await result.current.executeQuery({
        query: 'test query',
        useWebSearch: true,
      });
    });

    expect(result.current.response).toBeNull();
    expect(result.current.error).toBeInstanceOf(Error);
    expect(result.current.error?.message).toBe(errorMessage);
  });

  it('should call fetch with the correct parameters', async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(mockResponse),
    });

    const { result } = renderHook(() => useRagQuery());

    const queryParams = {
      query: 'test query',
      useWebSearch: true,
      webSearchResults: 5,
      topK: 10,
    };

    await act(async () => {
      await result.current.executeQuery(queryParams);
    });

    expect(global.fetch).toHaveBeenCalledWith('/api/rag/query', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(queryParams),
    });
  });
});
