const API_BASE = 'http://localhost:8080/api';

export type LoginResponse = {
  access_token: string;
  refresh_token: string;
};

export type Category = {
  id: string;
  name: string;
  type: 'income' | 'expense';
  color: string;
  is_system: boolean;
};

export type Transaction = {
  id: string;
  type: 'income' | 'expense';
  amount: number;
  memo: string | null;
  date: string;
  category: Category;
  recurring_income_id: string | null;
};

export type TransactionListResponse = {
  data: Transaction[];
  summary: {
    total_income: number;
    total_expense: number;
    balance: number;
  };
};

export type RecurringIncome = {
  id: string;
  category: Category;
  amount: number;
  memo: string | null;
  day_of_month: number;
  is_active: boolean;
};

function jsonHeaders(token?: string) {
  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {})
  };
}

export async function login(email: string, password: string): Promise<LoginResponse> {
  const res = await fetch(`${API_BASE}/auth/login`, {
    method: 'POST',
    headers: jsonHeaders(),
    body: JSON.stringify({ email, password })
  });
  if (!res.ok) throw new Error('ログインに失敗しました');
  return res.json();
}

export async function getCategories(token: string): Promise<Category[]> {
  const res = await fetch(`${API_BASE}/categories`, { headers: jsonHeaders(token) });
  if (!res.ok) throw new Error('カテゴリ取得に失敗しました');
  return res.json();
}

export async function createTransaction(
  token: string,
  payload: { category_id: string; type: 'income' | 'expense'; amount: number; memo: string; date: string }
) {
  const res = await fetch(`${API_BASE}/transactions`, {
    method: 'POST',
    headers: jsonHeaders(token),
    body: JSON.stringify(payload)
  });
  if (!res.ok) throw new Error('明細登録に失敗しました');
  return res.json();
}

export async function getTransactions(token: string, year: number, month: number): Promise<TransactionListResponse> {
  const res = await fetch(`${API_BASE}/transactions?year=${year}&month=${month}`, {
    headers: jsonHeaders(token)
  });
  if (!res.ok) throw new Error('明細取得に失敗しました');
  return res.json();
}

export async function getRecurringIncomes(token: string): Promise<RecurringIncome[]> {
  const res = await fetch(`${API_BASE}/recurring-incomes`, { headers: jsonHeaders(token) });
  if (!res.ok) throw new Error('定期収入取得に失敗しました');
  return res.json();
}

export async function createRecurringIncome(
  token: string,
  payload: { category_id: string; amount: number; memo: string; day_of_month: number }
) {
  const res = await fetch(`${API_BASE}/recurring-incomes`, {
    method: 'POST',
    headers: jsonHeaders(token),
    body: JSON.stringify(payload)
  });
  if (!res.ok) throw new Error('定期収入登録に失敗しました');
  return res.json();
}
