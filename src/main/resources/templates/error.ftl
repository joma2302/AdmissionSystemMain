<#import "layout.ftl" as layout>
<@layout.page title="Помилка">
    <div class="error-page">
        <h1>Упс! Сталася помилка</h1>
        <p class="error-label">Причина помилки:</p>
        <p class="error-message">${error! "Невідома помилка."}</p>
        <a href="/" class="btn-primary">Повернутися на головну</a>
    </div>

    <style>
        .error-page { text-align: center; margin-top: 50px; }
        h1 { color: #b91c1c; margin-bottom: 20px; }
        .error-message { background: #fee2e2; color: #b91c1c; padding: 15px 20px; border-radius: 8px; display: inline-block; margin-bottom: 20px; }
        .btn-primary { display: inline-block; background: #2563eb; color: #fff; padding: 10px 20px; border-radius: 5px; text-decoration: none; transition: background 0.2s; }
        .btn-primary:hover { background: #1d4ed8; }
    </style>
</@layout.page>
