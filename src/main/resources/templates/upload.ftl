<#import "layout.ftl" as layout>
<@layout.page title="Завантаження документів">
    <h1>Завантаження документів</h1>

    <#if success??>
        <div class="alert alert-success">Документи успішно завантажено!</div>
    </#if>
    <#if error??>
        <div class="alert alert-error">${error}</div>
    </#if>

    <form method="post" action="/upload" enctype="multipart/form-data" class="upload-form">
        <#if _csrf??>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        </#if>

        <div class="form-group">
            <label for="applicantId">Ваш ID абітурієнта</label>
            <input type="text" name="applicantId" id="applicantId" required placeholder="Введіть ваш ID">
        </div>

        <div class="form-group">
            <label for="file">Оберіть файл (скан-копія атестата тощо)</label>
            <input type="file" name="file" id="file" required>
        </div>

        <button type="submit" class="btn-primary">Завантажити</button>
    </form>

    <style>
        .upload-form { max-width: 500px; background: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
        .form-group { margin-bottom: 20px; }
        label { display: block; margin-bottom: 8px; font-weight: 500; }
        input[type="text"], input[type="file"] { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; }
        .alert-success { background: #d1fae5; color: #065f46; padding: 10px; border-radius: 4px; margin-bottom: 20px; }
        .alert-error { background: #fee2e2; color: #b91c1c; padding: 10px; border-radius: 4px; margin-bottom: 20px; }
    </style>
</@layout.page>
