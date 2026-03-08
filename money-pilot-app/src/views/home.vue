<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import {
  createRecurringIncome,
  createTransaction,
  getCategories,
  getRecurringIncomes,
  getTransactions,
  login,
  type Category,
  type RecurringIncome,
  type TransactionListResponse
} from '../services/api';

const email = ref('demo@example.com');
const password = ref('password123');
const token = ref('');
const error = ref('');
const loading = ref(false);

const categories = ref<Category[]>([]);
const recurringIncomes = ref<RecurringIncome[]>([]);
const transactions = ref<TransactionListResponse | null>(null);

const selectedType = ref<'income' | 'expense'>('expense');
const selectedCategoryId = ref('');
const amount = ref(0);
const memo = ref('');
const date = ref(new Date().toISOString().slice(0, 10));

const recurringCategoryId = ref('');
const recurringAmount = ref(0);
const recurringMemo = ref('');
const recurringDay = ref(25);

const now = new Date();
const year = ref(now.getFullYear());
const month = ref(now.getMonth() + 1);

const filteredCategories = computed(() => categories.value.filter((c) => c.type === selectedType.value));
const incomeCategories = computed(() => categories.value.filter((c) => c.type === 'income'));

async function doLogin() {
  try {
    loading.value = true;
    error.value = '';
    const data = await login(email.value, password.value);
    token.value = data.access_token;
    await Promise.all([loadCategories(), loadTransactions(), loadRecurringIncomes()]);
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'ログインエラー';
  } finally {
    loading.value = false;
  }
}

async function loadCategories() {
  categories.value = await getCategories(token.value);
  if (!selectedCategoryId.value && filteredCategories.value.length > 0) {
    selectedCategoryId.value = filteredCategories.value[0].id;
  }
  if (!recurringCategoryId.value && incomeCategories.value.length > 0) {
    recurringCategoryId.value = incomeCategories.value[0].id;
  }
}

async function loadTransactions() {
  transactions.value = await getTransactions(token.value, year.value, month.value);
}

async function loadRecurringIncomes() {
  recurringIncomes.value = await getRecurringIncomes(token.value);
}

async function submitTransaction() {
  if (!token.value) return;
  await createTransaction(token.value, {
    category_id: selectedCategoryId.value,
    type: selectedType.value,
    amount: amount.value,
    memo: memo.value,
    date: date.value
  });
  amount.value = 0;
  memo.value = '';
  await loadTransactions();
}

async function submitRecurringIncome() {
  if (!token.value) return;
  await createRecurringIncome(token.value, {
    category_id: recurringCategoryId.value,
    amount: recurringAmount.value,
    memo: recurringMemo.value,
    day_of_month: recurringDay.value
  });
  recurringAmount.value = 0;
  recurringMemo.value = '';
  await loadRecurringIncomes();
}

onMounted(() => {
  void doLogin();
});
</script>

<template>
  <main class="page">
    <header class="hero">
      <h1>MoneyPilot</h1>
      <p>家計簿 API/バッチ連携デモ</p>
    </header>

    <section class="panel login">
      <h2>ログイン</h2>
      <div class="row">
        <input v-model="email" placeholder="email" />
        <input v-model="password" type="password" placeholder="password" />
        <button :disabled="loading" @click="doLogin">接続</button>
      </div>
      <p v-if="error" class="error">{{ error }}</p>
      <p class="hint">初期ユーザー: demo@example.com / password123</p>
    </section>

    <section class="grid">
      <article class="panel">
        <h2>明細登録</h2>
        <div class="stack">
          <select v-model="selectedType">
            <option value="expense">支出</option>
            <option value="income">収入</option>
          </select>
          <select v-model="selectedCategoryId">
            <option v-for="c in filteredCategories" :key="c.id" :value="c.id">{{ c.name }}</option>
          </select>
          <input v-model.number="amount" type="number" min="1" placeholder="金額" />
          <input v-model="memo" placeholder="メモ" />
          <input v-model="date" type="date" />
          <button @click="submitTransaction">明細を登録</button>
        </div>
      </article>

      <article class="panel">
        <h2>定期収入設定</h2>
        <div class="stack">
          <select v-model="recurringCategoryId">
            <option v-for="c in incomeCategories" :key="c.id" :value="c.id">{{ c.name }}</option>
          </select>
          <input v-model.number="recurringAmount" type="number" min="1" placeholder="金額" />
          <input v-model="recurringMemo" placeholder="メモ" />
          <input v-model.number="recurringDay" type="number" min="1" max="28" placeholder="反映日" />
          <button @click="submitRecurringIncome">定期収入を登録</button>
        </div>
        <ul class="list">
          <li v-for="r in recurringIncomes" :key="r.id">
            {{ r.category.name }} / {{ r.amount.toLocaleString() }}円 / 毎月{{ r.day_of_month }}日
          </li>
        </ul>
      </article>
    </section>

    <section class="panel">
      <div class="row between">
        <h2>月次サマリー</h2>
        <div class="row compact">
          <input v-model.number="year" type="number" />
          <input v-model.number="month" type="number" min="1" max="12" />
          <button @click="loadTransactions">再取得</button>
        </div>
      </div>
      <div v-if="transactions" class="summary">
        <div>
          <strong>収入</strong>
          <span>{{ transactions.summary.total_income.toLocaleString() }}円</span>
        </div>
        <div>
          <strong>支出</strong>
          <span>{{ transactions.summary.total_expense.toLocaleString() }}円</span>
        </div>
        <div>
          <strong>残高</strong>
          <span>{{ transactions.summary.balance.toLocaleString() }}円</span>
        </div>
      </div>
      <table v-if="transactions" class="table">
        <thead>
          <tr>
            <th>日付</th>
            <th>区分</th>
            <th>カテゴリ</th>
            <th>金額</th>
            <th>メモ</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in transactions.data" :key="item.id">
            <td>{{ item.date }}</td>
            <td>{{ item.type === 'income' ? '収入' : '支出' }}</td>
            <td>{{ item.category.name }}</td>
            <td>{{ item.amount.toLocaleString() }}円</td>
            <td>{{ item.memo }}</td>
          </tr>
        </tbody>
      </table>
    </section>
  </main>
</template>
