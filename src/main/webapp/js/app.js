// ============================================
// APP.JS - IL CERVELLO FRONTEND
// Gestisce le chiamate AJAX e la manipolazione del DOM
// ============================================

// Configurazione
const API_BASE = '/NorthWind_Gruppo2/api';
const ENDPOINTS = {
    products: `${API_BASE}/products`,
    categories: `${API_BASE}/categories`
};

// Elementi DOM
const elements = {
    // Tab buttons
    tabButtons: document.querySelectorAll('.tab-button'),
    // Products section
    productsTab: document.getElementById('products'),
    productIdInput: document.getElementById('productId'),
    searchProductBtn: document.getElementById('searchProductBtn'),
    loadAllProductsBtn: document.getElementById('loadAllProductsBtn'),
    productsTable: document.getElementById('productsTable'),
    productsBody: document.getElementById('productsBody'),
    productsLoading: document.getElementById('productsLoading'),
    productsError: document.getElementById('productsError'),
    // Categories section
    categoriesTab: document.getElementById('categories'),
    categoryIdInput: document.getElementById('categoryId'),
    searchCategoryBtn: document.getElementById('searchCategoryBtn'),
    loadAllCategoriesBtn: document.getElementById('loadAllCategoriesBtn'),
    categoriesTable: document.getElementById('categoriesTable'),
    categoriesBody: document.getElementById('categoriesBody'),
    categoriesLoading: document.getElementById('categoriesLoading'),
    categoriesError: document.getElementById('categoriesError')
};

// ============================================
// GESTIONE DEI TAB
// ============================================
function initTabs() {
    elements.tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            const tabName = button.getAttribute('data-tab');
            switchTab(tabName);
        });
    });
}

function switchTab(tabName) {
    // Rimuovi active da tutti i tab
    elements.tabButtons.forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });

    // Aggiungi active al tab selezionato
    document.querySelector(`[data-tab="${tabName}"]`).classList.add('active');
    document.getElementById(tabName).classList.add('active');
}

// ============================================
// FUNZIONI AJAX GENERICHE
// ============================================
async function fetchData(url) {
    try {
        const response = await fetch(url);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Errore nella fetch:', error);
        throw error;
    }
}

// ============================================
// PRODUCTS - Funzioni
// ============================================
function showProductsLoading(show = true) {
    elements.productsLoading.classList.toggle('active', show);
}

function showProductsError(message = '') {
    if (message) {
        elements.productsError.textContent = message;
        elements.productsError.classList.add('active');
    } else {
        elements.productsError.classList.remove('active');
    }
}

async function loadAllProducts() {
    try {
        showProductsLoading(true);
        showProductsError();
        
        const data = await fetchData(ENDPOINTS.products);
        displayProducts(data);
        
    } catch (error) {
        showProductsError('❌ Errore nel caricamento dei prodotti. Riprova più tardi.');
        console.error('Errore loadAllProducts:', error);
    } finally {
        showProductsLoading(false);
    }
}

async function searchProduct() {
    const productId = elements.productIdInput.value.trim();
    
    if (!productId) {
        showProductsError('⚠️ Inserisci un ID prodotto valido');
        return;
    }

    try {
        showProductsLoading(true);
        showProductsError();
        
        const url = `${ENDPOINTS.products}?id=${productId}`;
        const data = await fetchData(url);
        displayProducts(data);
        
    } catch (error) {
        showProductsError(`❌ Prodotto con ID ${productId} non trovato.`);
        elements.productsBody.innerHTML = '<tr class="empty-state"><td colspan="6">Nessun risultato</td></tr>';
    } finally {
        showProductsLoading(false);
    }
}

function displayProducts(products) {
    if (!products || products.length === 0) {
        elements.productsBody.innerHTML = '<tr class="empty-state"><td colspan="6">Nessun prodotto trovato</td></tr>';
        return;
    }

    // Se è un singolo oggetto, convertilo in array
    const productArray = Array.isArray(products) ? products : [products];

    elements.productsBody.innerHTML = productArray.map(product => `
        <tr>
            <td><strong>${product.id || '-'}</strong></td>
            <td>${product.name || '-'}</td>
            <td>${product.description || '-'}</td>
            <td>${formatPrice(product.price)}</td>
            <td>${product.quantityPerUnit || '-'}</td>
            <td>
                <span class="category-badge">${product.categoryId || '-'}</span>
            </td>
        </tr>
    `).join('');
}

// ============================================
// CATEGORIES - Funzioni
// ============================================
function showCategoriesLoading(show = true) {
    elements.categoriesLoading.classList.toggle('active', show);
}

function showCategoriesError(message = '') {
    if (message) {
        elements.categoriesError.textContent = message;
        elements.categoriesError.classList.add('active');
    } else {
        elements.categoriesError.classList.remove('active');
    }
}

async function loadAllCategories() {
    try {
        showCategoriesLoading(true);
        showCategoriesError();
        
        const data = await fetchData(ENDPOINTS.categories);
        displayCategories(data);
        
    } catch (error) {
        showCategoriesError('❌ Errore nel caricamento delle categorie. Riprova più tardi.');
        console.error('Errore loadAllCategories:', error);
    } finally {
        showCategoriesLoading(false);
    }
}

async function searchCategory() {
    const categoryId = elements.categoryIdInput.value.trim();
    
    if (!categoryId) {
        showCategoriesError('⚠️ Inserisci un ID categoria valido');
        return;
    }

    try {
        showCategoriesLoading(true);
        showCategoriesError();
        
        const url = `${ENDPOINTS.categories}?id=${categoryId}`;
        const data = await fetchData(url);
        displayCategories(data);
        
    } catch (error) {
        showCategoriesError(`❌ Categoria con ID ${categoryId} non trovata.`);
        elements.categoriesBody.innerHTML = '<tr class="empty-state"><td colspan="3">Nessun risultato</td></tr>';
    } finally {
        showCategoriesLoading(false);
    }
}

function displayCategories(categories) {
    if (!categories || categories.length === 0) {
        elements.categoriesBody.innerHTML = '<tr class="empty-state"><td colspan="3">Nessuna categoria trovata</td></tr>';
        return;
    }

    // Se è un singolo oggetto, convertilo in array
    const categoryArray = Array.isArray(categories) ? categories : [categories];

    elements.categoriesBody.innerHTML = categoryArray.map(category => `
        <tr>
            <td><strong>${category.id || '-'}</strong></td>
            <td>${category.name || '-'}</td>
            <td>${category.description || '-'}</td>
        </tr>
    `).join('');
}

// ============================================
// FUNZIONI UTILITY
// ============================================
function formatPrice(price) {
    if (!price) return '-';
    return `€${parseFloat(price).toFixed(2)}`;
}

// ============================================
// EVENT LISTENERS
// ============================================
function initEventListeners() {
    // Products events
    elements.loadAllProductsBtn.addEventListener('click', loadAllProducts);
    elements.searchProductBtn.addEventListener('click', searchProduct);
    elements.productIdInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') searchProduct();
    });

    // Categories events
    elements.loadAllCategoriesBtn.addEventListener('click', loadAllCategories);
    elements.searchCategoryBtn.addEventListener('click', searchCategory);
    elements.categoryIdInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') searchCategory();
    });
}

// ============================================
// INIZIALIZZAZIONE
// ============================================
document.addEventListener('DOMContentLoaded', () => {
    console.log('✅ App inizializzata');
    console.log('🌐 API Base:', API_BASE);
    
    initTabs();
    initEventListeners();
    
    // Carica automaticamente i prodotti al primo accesso
    loadAllProducts();
});

// ============================================
// LOG PER DEBUG
// ============================================
console.log('📱 Frontend App caricato');
console.log('🔌 Endpoint Prodotti:', ENDPOINTS.products);
console.log('🔌 Endpoint Categorie:', ENDPOINTS.categories);
