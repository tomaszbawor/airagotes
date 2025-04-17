import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { SearchForm } from '../SearchForm';

describe('SearchForm', () => {
  const defaultProps = {
    query: '',
    setQuery: vi.fn(),
    useWebSearch: true,
    setUseWebSearch: vi.fn(),
    isLoading: false,
    onSubmit: vi.fn(),
  };

  it('renders correctly', () => {
    render(<SearchForm {...defaultProps} />);

    expect(screen.getByLabelText(/ask a question/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/what would you like to know/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/include web search results/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /search/i })).toBeInTheDocument();
  });

  it('calls setQuery when input changes', () => {
    render(<SearchForm {...defaultProps} />);

    const input = screen.getByLabelText(/ask a question/i);
    fireEvent.change(input, { target: { value: 'test query' } });

    expect(defaultProps.setQuery).toHaveBeenCalledWith('test query');
  });

  it('calls setUseWebSearch when checkbox changes', () => {
    render(<SearchForm {...defaultProps} />);

    const checkbox = screen.getByLabelText(/include web search results/i);
    fireEvent.click(checkbox);

    expect(defaultProps.setUseWebSearch).toHaveBeenCalledWith(false);
  });

  it('calls onSubmit when form is submitted', () => {
    render(<SearchForm {...defaultProps} />);

    const form = screen.getByRole('form');
    fireEvent.submit(form);

    expect(defaultProps.onSubmit).toHaveBeenCalled();
  });

  it('disables the button when isLoading is true', () => {
    render(<SearchForm {...defaultProps} isLoading={true} />);

    const button = screen.getByRole('button', { name: /searching/i });
    expect(button).toBeDisabled();
  });

  it('shows "Searching..." text when isLoading is true', () => {
    render(<SearchForm {...defaultProps} isLoading={true} />);

    expect(screen.getByText(/searching/i)).toBeInTheDocument();
  });
});
