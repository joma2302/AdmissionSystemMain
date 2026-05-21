<#import "layout.ftl" as layout>
<@layout.page title="Сторінку не знайдено">
    <div class="error-page">
        <h1>404 - Сторінку не знайдено</h1>
        <p>На жаль, сторінка, яку ви шукаєте, не існує.</p>
        <a href="/" class="btn-primary">Повернутися на головну</a>
    </div>
    <style>
        .error-page { text-align: center; margin-top: 50px; }
        h1 { color: #b91c1c; margin-bottom: 20px; }
        .btn-primary { display: inline-block; background: #2563eb; color: #fff; padding: 10px 20px; border-radius: 5px; text-decoration: none; transition: background 0.2s; }
        .btn-primary:hover { background: #1d4ed8; }
    </style>
</@layout.page>
