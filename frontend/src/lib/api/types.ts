export interface ApiErrorBody {
  message?: string;
  data?: Record<string, string> | unknown;
  statusCode?: number;
  errors?: unknown;
}

export interface PageResponse<T> {
  items: T[];
  pageNumber: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
  hasPrevious: boolean;
}
