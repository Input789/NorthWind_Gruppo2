function resolveContextPath() {
    if (window.location.protocol === 'file:') {
        return null;
    }

    const pathSegments = window.location.pathname.split('/').filter(Boolean);
    if (pathSegments.length === 0) {
        return '';
    }

    return pathSegments[0].includes('.') ? '' : `/${pathSegments[0]}`;
}

const CONTEXT_PATH = resolveContextPath();
const API_BASE = CONTEXT_PATH === null ? null : `${CONTEXT_PATH}/api`;
const ENDPOINTS = {
    products: `${API_BASE}/products`,
    categories: `${API_BASE}/categories`
};

const state = {
    products: [],
    categories: []
};

const elements = {
    tabButtons: document.querySelectorAll('.tab-button'),
    productsFeedback: document.getElementById('productsFeedback'),
    categoriesFeedback: document.getElementById('categoriesFeedback'),
    productsLoading: document.getElementById('productsLoading'),
    categoriesLoading: document.getElementById('categoriesLoading'),
    productSearchId: document.getElementById('productSearchId'),
    searchProductBtn: document.getElementById('searchProductBtn'),
    loadAllProductsBtn: document.getElementById('loadAllProductsBtn'),
    newProductBtn: document.getElementById('newProductBtn'),
    productsBody: document.getElementById('productsBody'),
    productForm: document.getElementById('productForm'),
    productFormMode: document.getElementById('productFormMode'),
    productId: document.getElementById('productId'),
    productName: document.getElementById('productName'),
    productSupplierId: document.getElementById('productSupplierId'),
    productCategoryId: document.getElementById('productCategoryId'),
    productUnitPrice: document.getElementById('productUnitPrice'),
    productQuantityPerUnit: document.getElementById('productQuantityPerUnit'),
    productUnitsInStock: document.getElementById('productUnitsInStock'),
    productUnitsOnOrder: document.getElementById('productUnitsOnOrder'),
    productReorderLevel: document.getElementById('productReorderLevel'),
    productDiscontinued: document.getElementById('productDiscontinued'),
    cancelProductEditBtn: document.getElementById('cancelProductEditBtn'),
    categorySearchId: document.getElementById('categorySearchId'),
    searchCategoryBtn: document.getElementById('searchCategoryBtn'),
    loadAllCategoriesBtn: document.getElementById('loadAllCategoriesBtn'),
    newCategoryBtn: document.getElementById('newCategoryBtn'),
    categoriesBody: document.getElementById('categoriesBody'),
    categoryForm: document.getElementById('categoryForm'),
    categoryFormMode: document.getElementById('categoryFormMode'),
    categoryId: document.getElementById('categoryId'),
    categoryName: document.getElementById('categoryName'),
    categoryDescription: document.getElementById('categoryDescription'),
    cancelCategoryEditBtn: document.getElementById('cancelCategoryEditBtn')
};

function switchTab(tabName) {
    elements.tabButtons.forEach(button => {
        button.classList.toggle('active', button.getAttribute('data-tab') === tabName);
    });

    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.toggle('active', tab.id === tabName);
    });
}

function initTabs() {
    elements.tabButtons.forEach(button => {
        button.addEventListener('click', () => switchTab(button.getAttribute('data-tab')));
    });
}

async function requestJson(url, options = {}) {
    if (API_BASE === null) {
        throw new Error('Apri l\'applicazione tramite Tomcat o NetBeans, non dal file system.');
    }

    const response = await fetch(url, {
        headers: {
            'Content-Type': 'application/json',
            ...(options.headers || {})
        },
        ...options
    });

    const rawText = await response.text();
    let payload = null;

    if (rawText) {
        try {
            payload = JSON.parse(rawText);
        } catch (error) {
            payload = null;
        }
    }

    if (!response.ok) {
        const message = payload && payload.error ? payload.error : `HTTP error! status: ${response.status}`;
        throw new Error(message);
    }

    return payload;
}

function setFeedback(element, message = '', type = '') {
    element.textContent = message;
    element.className = 'status-message';

    if (!message) {
        return;
    }

    element.classList.add('active');
    element.classList.add(type === 'success' ? 'success-message' : 'error-message');
}

function clearFeedback() {
    setFeedback(elements.productsFeedback);
    setFeedback(elements.categoriesFeedback);
}

function showProductsLoading(show = true) {
    elements.productsLoading.classList.toggle('active', show);
}

function showCategoriesLoading(show = true) {
    elements.categoriesLoading.classList.toggle('active', show);
}

function toNumberOrNull(value) {
    if (value === '' || value === null || value === undefined) {
        return null;
    }

    const parsed = Number(value);
    return Number.isNaN(parsed) ? null : parsed;
}

function toTrimmedValue(value) {
    const trimmed = value.trim();
    return trimmed === '' ? null : trimmed;
}

function formatPrice(price) {
    if (price === null || price === undefined || price === '') {
        return '-';
    }

    return `EUR ${Number(price).toFixed(2)}`;
}

function resetProductForm() {
    elements.productForm.reset();
    elements.productId.value = '';
    elements.productFormMode.textContent = "Modalita': inserimento";
}

function resetCategoryForm() {
    elements.categoryForm.reset();
    elements.categoryId.value = '';
    elements.categoryFormMode.textContent = "Modalita': inserimento";
}

function populateProductForm(product) {
    elements.productId.value = product.productId ?? '';
    elements.productName.value = product.productName ?? '';
    elements.productSupplierId.value = product.supplierId ?? '';
    elements.productCategoryId.value = product.category?.categoryId ?? '';
    elements.productUnitPrice.value = product.unitPrice ?? '';
    elements.productQuantityPerUnit.value = product.quantityPerUnit ?? '';
    elements.productUnitsInStock.value = product.unitsInStock ?? '';
    elements.productUnitsOnOrder.value = product.unitsOnOrder ?? '';
    elements.productReorderLevel.value = product.reorderLevel ?? '';
    elements.productDiscontinued.value = product.discontinued ?? '';
    elements.productFormMode.textContent = `Modalita': modifica ID ${product.productId}`;
}

function populateCategoryForm(category) {
    elements.categoryId.value = category.categoryId ?? '';
    elements.categoryName.value = category.categoryName ?? '';
    elements.categoryDescription.value = category.description ?? '';
    elements.categoryFormMode.textContent = `Modalita': modifica ID ${category.categoryId}`;
}

function buildProductPayload() {
    return {
        productName: elements.productName.value.trim(),
        supplierId: toNumberOrNull(elements.productSupplierId.value),
        category: elements.productCategoryId.value
            ? { categoryId: Number(elements.productCategoryId.value) }
            : null,
        quantityPerUnit: toTrimmedValue(elements.productQuantityPerUnit.value),
        unitPrice: toNumberOrNull(elements.productUnitPrice.value),
        unitsInStock: toNumberOrNull(elements.productUnitsInStock.value),
        unitsOnOrder: toNumberOrNull(elements.productUnitsOnOrder.value),
        reorderLevel: toNumberOrNull(elements.productReorderLevel.value),
        discontinued: toTrimmedValue(elements.productDiscontinued.value)
    };
}

function buildCategoryPayload() {
    return {
        categoryName: elements.categoryName.value.trim(),
        description: toTrimmedValue(elements.categoryDescription.value)
    };
}

async function loadCategoryOptions() {
    const categories = await requestJson(ENDPOINTS.categories);
    state.categories = Array.isArray(categories) ? categories : categories ? [categories] : [];

    const options = [
        '<option value="">Nessuna categoria</option>',
        ...state.categories.map(category =>
            `<option value="${category.categoryId}">${category.categoryName}</option>`)
    ];

    elements.productCategoryId.innerHTML = options.join('');
}

function displayProducts(products) {
    const productArray = Array.isArray(products) ? products : products ? [products] : [];
    state.products = productArray;

    if (productArray.length === 0) {
        elements.productsBody.innerHTML = '<tr class="empty-state"><td colspan="8">Nessun prodotto trovato</td></tr>';
        return;
    }

    elements.productsBody.innerHTML = productArray.map(product => `
        <tr>
            <td><strong>${product.productId ?? '-'}</strong></td>
            <td>${product.productName || '-'}</td>
            <td>${product.supplierId ?? '-'}</td>
            <td>${product.category?.categoryName || '-'}</td>
            <td>${formatPrice(product.unitPrice)}</td>
            <td>${product.quantityPerUnit || '-'}</td>
            <td>${product.unitsInStock ?? '-'}</td>
            <td class="actions-cell">
                <button type="button" class="btn btn-secondary btn-small" data-action="edit-product" data-id="${product.productId}">Modifica</button>
                <button type="button" class="btn btn-danger btn-small" data-action="delete-product" data-id="${product.productId}">Elimina</button>
            </td>
        </tr>
    `).join('');
}

function displayCategories(categories) {
    const categoryArray = Array.isArray(categories) ? categories : categories ? [categories] : [];
    state.categories = categoryArray;

    if (categoryArray.length === 0) {
        elements.categoriesBody.innerHTML = '<tr class="empty-state"><td colspan="4">Nessuna categoria trovata</td></tr>';
        return;
    }

    elements.categoriesBody.innerHTML = categoryArray.map(category => `
        <tr>
            <td><strong>${category.categoryId ?? '-'}</strong></td>
            <td>${category.categoryName || '-'}</td>
            <td>${category.description || '-'}</td>
            <td class="actions-cell">
                <button type="button" class="btn btn-secondary btn-small" data-action="edit-category" data-id="${category.categoryId}">Modifica</button>
                <button type="button" class="btn btn-danger btn-small" data-action="delete-category" data-id="${category.categoryId}">Elimina</button>
            </td>
        </tr>
    `).join('');
}

async function loadAllProducts() {
    showProductsLoading(true);
    setFeedback(elements.productsFeedback);

    try {
        const products = await requestJson(ENDPOINTS.products);
        displayProducts(products);
    } catch (error) {
        setFeedback(elements.productsFeedback, error.message, 'error');
        elements.productsBody.innerHTML = '<tr class="empty-state"><td colspan="8">Errore nel caricamento dei prodotti</td></tr>';
    } finally {
        showProductsLoading(false);
    }
}

async function loadAllCategories() {
    showCategoriesLoading(true);
    setFeedback(elements.categoriesFeedback);

    try {
        const categories = await requestJson(ENDPOINTS.categories);
        displayCategories(categories);
        await loadCategoryOptions();
    } catch (error) {
        setFeedback(elements.categoriesFeedback, error.message, 'error');
        elements.categoriesBody.innerHTML = '<tr class="empty-state"><td colspan="4">Errore nel caricamento delle categorie</td></tr>';
    } finally {
        showCategoriesLoading(false);
    }
}

async function searchProduct() {
    const productId = elements.productSearchId.value.trim();

    if (!productId) {
        await loadAllProducts();
        return;
    }

    showProductsLoading(true);
    setFeedback(elements.productsFeedback);

    try {
        const product = await requestJson(`${ENDPOINTS.products}?id=${productId}`);
        displayProducts(product);
    } catch (error) {
        setFeedback(elements.productsFeedback, error.message, 'error');
        elements.productsBody.innerHTML = '<tr class="empty-state"><td colspan="8">Nessun risultato</td></tr>';
    } finally {
        showProductsLoading(false);
    }
}

async function searchCategory() {
    const categoryId = elements.categorySearchId.value.trim();

    if (!categoryId) {
        await loadAllCategories();
        return;
    }

    showCategoriesLoading(true);
    setFeedback(elements.categoriesFeedback);

    try {
        const category = await requestJson(`${ENDPOINTS.categories}?id=${categoryId}`);
        displayCategories(category);
    } catch (error) {
        setFeedback(elements.categoriesFeedback, error.message, 'error');
        elements.categoriesBody.innerHTML = '<tr class="empty-state"><td colspan="4">Nessun risultato</td></tr>';
    } finally {
        showCategoriesLoading(false);
    }
}

async function submitProductForm(event) {
    event.preventDefault();
    setFeedback(elements.productsFeedback);

    const productId = elements.productId.value;
    const payload = buildProductPayload();
    const isUpdate = productId !== '';
    const url = isUpdate ? `${ENDPOINTS.products}?id=${productId}` : ENDPOINTS.products;
    const method = isUpdate ? 'PUT' : 'POST';

    try {
        await requestJson(url, {
            method,
            body: JSON.stringify(payload)
        });

        resetProductForm();
        await loadAllProducts();
        setFeedback(elements.productsFeedback, isUpdate ? 'Prodotto aggiornato con successo.' : 'Prodotto creato con successo.', 'success');
    } catch (error) {
        setFeedback(elements.productsFeedback, error.message, 'error');
    }
}

async function submitCategoryForm(event) {
    event.preventDefault();
    setFeedback(elements.categoriesFeedback);

    const categoryId = elements.categoryId.value;
    const payload = buildCategoryPayload();
    const isUpdate = categoryId !== '';
    const url = isUpdate ? `${ENDPOINTS.categories}?id=${categoryId}` : ENDPOINTS.categories;
    const method = isUpdate ? 'PUT' : 'POST';

    try {
        await requestJson(url, {
            method,
            body: JSON.stringify(payload)
        });

        resetCategoryForm();
        await loadAllCategories();
        setFeedback(elements.categoriesFeedback, isUpdate ? 'Categoria aggiornata con successo.' : 'Categoria creata con successo.', 'success');
    } catch (error) {
        setFeedback(elements.categoriesFeedback, error.message, 'error');
    }
}

async function deleteProduct(productId) {
    const confirmed = window.confirm(`Vuoi eliminare il prodotto con ID ${productId}?`);
    if (!confirmed) {
        return;
    }

    try {
        await requestJson(`${ENDPOINTS.products}?id=${productId}`, { method: 'DELETE' });
        resetProductForm();
        await loadAllProducts();
        setFeedback(elements.productsFeedback, 'Prodotto eliminato con successo.', 'success');
    } catch (error) {
        setFeedback(elements.productsFeedback, error.message, 'error');
    }
}

async function deleteCategory(categoryId) {
    const confirmed = window.confirm(`Vuoi eliminare la categoria con ID ${categoryId}?`);
    if (!confirmed) {
        return;
    }

    try {
        await requestJson(`${ENDPOINTS.categories}?id=${categoryId}`, { method: 'DELETE' });
        resetCategoryForm();
        await loadAllCategories();
        setFeedback(elements.categoriesFeedback, 'Categoria eliminata con successo.', 'success');
    } catch (error) {
        setFeedback(elements.categoriesFeedback, error.message, 'error');
    }
}

function handleProductsTableClick(event) {
    const button = event.target.closest('button[data-action]');
    if (!button) {
        return;
    }

    const productId = Number(button.dataset.id);
    const action = button.dataset.action;
    const product = state.products.find(item => item.productId === productId);

    if (action === 'edit-product' && product) {
        populateProductForm(product);
        switchTab('products');
        window.scrollTo({ top: 0, behavior: 'smooth' });
        return;
    }

    if (action === 'delete-product') {
        deleteProduct(productId);
    }
}

function handleCategoriesTableClick(event) {
    const button = event.target.closest('button[data-action]');
    if (!button) {
        return;
    }

    const categoryId = Number(button.dataset.id);
    const action = button.dataset.action;
    const category = state.categories.find(item => item.categoryId === categoryId);

    if (action === 'edit-category' && category) {
        populateCategoryForm(category);
        switchTab('categories');
        window.scrollTo({ top: 0, behavior: 'smooth' });
        return;
    }

    if (action === 'delete-category') {
        deleteCategory(categoryId);
    }
}

function initEventListeners() {
    elements.searchProductBtn.addEventListener('click', searchProduct);
    elements.loadAllProductsBtn.addEventListener('click', loadAllProducts);
    elements.newProductBtn.addEventListener('click', resetProductForm);
    elements.cancelProductEditBtn.addEventListener('click', resetProductForm);
    elements.productForm.addEventListener('submit', submitProductForm);
    elements.productsBody.addEventListener('click', handleProductsTableClick);
    elements.productSearchId.addEventListener('keypress', event => {
        if (event.key === 'Enter') {
            searchProduct();
        }
    });

    elements.searchCategoryBtn.addEventListener('click', searchCategory);
    elements.loadAllCategoriesBtn.addEventListener('click', loadAllCategories);
    elements.newCategoryBtn.addEventListener('click', resetCategoryForm);
    elements.cancelCategoryEditBtn.addEventListener('click', resetCategoryForm);
    elements.categoryForm.addEventListener('submit', submitCategoryForm);
    elements.categoriesBody.addEventListener('click', handleCategoriesTableClick);
    elements.categorySearchId.addEventListener('keypress', event => {
        if (event.key === 'Enter') {
            searchCategory();
        }
    });
}

document.addEventListener('DOMContentLoaded', async () => {
    initTabs();
    initEventListeners();
    clearFeedback();

    if (API_BASE === null) {
        const message = 'Questa pagina va avviata tramite Tomcat o NetBeans. Aprire index.html direttamente non permette di usare il backend.';
        setFeedback(elements.productsFeedback, message, 'error');
        setFeedback(elements.categoriesFeedback, message, 'error');
        return;
    }

    try {
        await loadCategoryOptions();
    } catch (error) {
        setFeedback(elements.productsFeedback, error.message, 'error');
    }

    await Promise.all([loadAllProducts(), loadAllCategories()]);
});
