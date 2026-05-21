<#-- Сторінка подачі заявки на факультет -->
<#import "layout.ftl" as layout>
<@layout.page title="Подати заявку">
    <h1>Подати заявку на факультет</h1>

    <#if success??>
        <div class="alert-success">Заявку подано успішно!</div>
    </#if>

    <#if error??>
        <div class="alert-error">${error}</div>
    </#if>

    <form method="post" action="/apply" class="apply-form">
        <#if _csrf??>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        </#if>

        <div class="form-group">
            <label for="applicantId">ID абітурієнта</label>
            <input type="text" id="applicantId" name="applicantId" required>
        </div>

        <div class="form-group">
            <label for="faculty">Факультет</label>
            <select name="facultyName" id="faculty" required>
                <option value="">-- Оберіть факультет --</option>
                <#if faculties??>
                    <#list faculties as faculty>
                        <option value="${faculty.name}">${faculty.name}</option>
                    </#list>
                </#if>
            </select>
        </div>

        <button type="submit" class="btn-primary">Подати заявку</button>
    </form>

    <style>
        h1 { color: #1f2937; margin-bottom: 20px; }
        .apply-form { max-width: 400px; margin-top: 20px; }
        .form-group { margin-bottom: 15px; display: flex; flex-direction: column; }
        label { margin-bottom: 5px; font-weight: 500; }
        input[type="text"], select { padding: 8px 10px; border-radius: 5px; border: 1px solid #d1d5db; }
        .btn-primary { background: #2563eb; color: #fff; border: none; padding: 10px 15px; border-radius: 5px; cursor: pointer; transition: background 0.2s; }
        .btn-primary:hover { background: #1d4ed8; }
        .alert-success { background: #d1fae5; color: #065f46; padding: 10px 15px; border-radius: 5px; margin-bottom: 15px; }
        .alert-error { background: #fee2e2; color: #b91c1c; padding: 10px 15px; border-radius: 5px; margin-bottom: 15px; }
    </style>
</@layout.page>
